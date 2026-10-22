package io.github.lystrosaurus.admin.web.datascope;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryPort;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleAssociationPort;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryPort;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleSummary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 数据权限辅助工具
 *
 * <p>根据当前登录用户的角色 data_scope_type 计算可访问的部门ID集合，并提供便捷的查询条件拼接方法。
 *
 * <p>返回值约定：
 *
 * <ul>
 *   <li>null — ALL 范围，不限制
 *   <li>空集合 — 无任何数据访问权限
 *   <li>非空集合 — 只能访问集合内的部门数据
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class DataScopeHelper {

  private final RoleQueryPort roleQueryPort;
  private final RoleAssociationPort roleAssociationPort;
  private final OrgUnitQueryPort orgUnitQueryPort;

  /**
   * 获取当前用户可访问的部门ID集合
   *
   * @return null 表示 ALL（不限制），空集合表示无权限，非空集合为可访问的部门ID
   */
  public Set<Long> getAccessibleOrgIds() {
    Long userId = getCurrentUserId();
    List<RoleSummary> roles = getRolesByUserId(userId);
    return doGetAccessibleOrgIds(roles);
  }

  /**
   * 当前用户是否有 SELF 数据权限
   *
   * @return true 表示用户至少有一个 SELF 角色
   */
  public boolean hasSelfScope() {
    Long userId = getCurrentUserId();
    List<RoleSummary> roles = getRolesByUserId(userId);
    return doHasSelfScope(roles);
  }

  /**
   * 当前用户是否有 ALL 数据权限
   *
   * @return true 表示用户至少有一个 ALL 角色
   */
  public boolean hasAllScope() {
    Long userId = getCurrentUserId();
    List<RoleSummary> roles = getRolesByUserId(userId);
    return doHasAllScope(roles);
  }

  /**
   * 对查询条件追加部门范围过滤
   *
   * @param wrapper MyBatis-Plus 查询条件
   * @param orgIdColumn 部门ID字段引用
   * @param <T> 实体类型
   */
  public <T> void applyOrgScope(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> orgIdColumn) {
    Set<Long> orgIds = getAccessibleOrgIds();
    if (orgIds == null) {
      // ALL 范围，不追加条件
      return;
    }
    if (orgIds.isEmpty()) {
      // 无权限，返回不可能匹配的条件
      wrapper.apply("1 = 0");
      return;
    }
    wrapper.in(orgIdColumn, orgIds);
  }

  /**
   * 对查询条件追加完整的数据权限过滤（部门 + 本人）
   *
   * @param wrapper MyBatis-Plus 查询条件
   * @param orgIdColumn 部门ID字段引用
   * @param createdByColumn 创建人ID字段引用
   * @param <T> 实体类型
   */
  public <T> void applyDataScope(
      LambdaQueryWrapper<T> wrapper, SFunction<T, ?> orgIdColumn, SFunction<T, ?> createdByColumn) {
    // 只查询一次角色，避免重复查询
    Long userId = getCurrentUserId();
    List<RoleSummary> roles = getRolesByUserId(userId);

    Set<Long> orgIds = doGetAccessibleOrgIds(roles);

    if (orgIds == null) {
      // ALL 范围
      return;
    }

    boolean hasSelf = doHasSelfScope(roles);

    if (orgIds.isEmpty() && !hasSelf) {
      wrapper.apply("1 = 0");
      return;
    }

    if (!orgIds.isEmpty() && hasSelf) {
      // ORG + SELF：部门范围内 OR 本人创建
      wrapper.and(w -> w.in(orgIdColumn, orgIds).or().eq(createdByColumn, userId));
    } else if (!orgIds.isEmpty()) {
      wrapper.in(orgIdColumn, orgIds);
    } else if (hasSelf) {
      wrapper.eq(createdByColumn, userId);
    }
  }

  // ==================== 内部方法 ====================

  /** 基于角色列表计算可访问的部门ID集合 */
  private Set<Long> doGetAccessibleOrgIds(List<RoleSummary> roles) {
    if (roles.isEmpty()) {
      return Set.of();
    }

    // 如果任意角色为 ALL，则不限制
    boolean hasAll =
        roles.stream().anyMatch(r -> DataScopeType.ALL.name().equals(r.dataScopeType()));
    if (hasAll) {
      return null;
    }

    // 合并所有角色可访问的部门ID
    Set<Long> orgIds = new HashSet<>();
    for (RoleSummary role : roles) {
      DataScopeType scopeType = DataScopeType.valueOf(role.dataScopeType());
      switch (scopeType) {
        case ORG_TREE:
          // 获取该角色绑定的部门ID，并查找所有下级部门
          Set<Long> treeOrgIds = getRoleOrgIds(role.id());
          for (Long orgId : treeOrgIds) {
            orgIds.add(orgId);
            orgIds.addAll(getDescendantOrgIds(orgId));
          }
          break;
        case ORG_ONLY:
        case CUSTOM:
          orgIds.addAll(getRoleOrgIds(role.id()));
          break;
        case SELF:
          // SELF 范围不贡献部门ID，由 hasSelfScope() 单独处理
          break;
        default:
          break;
      }
    }

    return orgIds;
  }

  /** 基于角色列表判断是否有 SELF 数据权限 */
  private boolean doHasSelfScope(List<RoleSummary> roles) {
    return roles.stream().anyMatch(r -> DataScopeType.SELF.name().equals(r.dataScopeType()));
  }

  /** 基于角色列表判断是否有 ALL 数据权限 */
  private boolean doHasAllScope(List<RoleSummary> roles) {
    return roles.stream().anyMatch(r -> DataScopeType.ALL.name().equals(r.dataScopeType()));
  }

  /** 获取当前登录用户ID */
  private Long getCurrentUserId() {
    if (!StpUtil.isLogin()) {
      throw new BusinessException(ErrorCode.AUTH_401);
    }
    return StpUtil.getLoginIdAsLong();
  }

  /**
   * 通过 sys_user_role + sys_role 查询用户的所有角色
   *
   * <p>查询链路：userId → sys_user_role → sys_role（对应 spec 中的要求）
   */
  List<RoleSummary> getRolesByUserId(Long userId) {
    return roleQueryPort.findByUserId(userId);
  }

  /** 获取角色绑定的部门ID列表 */
  private Set<Long> getRoleOrgIds(Long roleId) {
    return new HashSet<>(roleAssociationPort.findOrgIdsByRoleId(roleId));
  }

  /** 获取某个部门的所有下级部门ID（委托 organization 上下文查询端口） */
  private Set<Long> getDescendantOrgIds(Long orgId) {
    return orgUnitQueryPort.findDescendantIds(orgId);
  }
}
