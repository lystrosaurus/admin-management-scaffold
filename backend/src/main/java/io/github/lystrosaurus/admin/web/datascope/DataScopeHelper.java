package io.github.lystrosaurus.admin.web.datascope;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.entity.SysRoleOrg;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMapper;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleOrgMapper;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

  private final SysUserRoleMapper userRoleMapper;
  private final SysRoleMapper roleMapper;
  private final SysRoleOrgMapper roleOrgMapper;
  private final OrgUnitDAO orgUnitDAO;

  /**
   * 获取当前用户可访问的部门ID集合
   *
   * @return null 表示 ALL（不限制），空集合表示无权限，非空集合为可访问的部门ID
   */
  public Set<Long> getAccessibleOrgIds() {
    Long userId = getCurrentUserId();
    List<SysRole> roles = getRolesByUserId(userId);

    if (roles.isEmpty()) {
      return Set.of();
    }

    // 如果任意角色为 ALL，则不限制
    boolean hasAll =
        roles.stream().anyMatch(r -> DataScopeType.ALL.name().equals(r.getDataScopeType()));
    if (hasAll) {
      return null;
    }

    // 合并所有角色可访问的部门ID
    Set<Long> orgIds = new HashSet<>();
    for (SysRole role : roles) {
      DataScopeType scopeType = DataScopeType.valueOf(role.getDataScopeType());
      switch (scopeType) {
        case ORG_TREE:
          // 获取该角色绑定的部门ID，并查找所有下级部门
          Set<Long> treeOrgIds = getRoleOrgIds(role.getId());
          for (Long orgId : treeOrgIds) {
            orgIds.add(orgId);
            orgIds.addAll(getDescendantOrgIds(orgId));
          }
          break;
        case ORG_ONLY:
        case CUSTOM:
          orgIds.addAll(getRoleOrgIds(role.getId()));
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

  /**
   * 当前用户是否有 SELF 数据权限
   *
   * @return true 表示用户至少有一个 SELF 角色
   */
  public boolean hasSelfScope() {
    Long userId = getCurrentUserId();
    List<SysRole> roles = getRolesByUserId(userId);
    return roles.stream().anyMatch(r -> DataScopeType.SELF.name().equals(r.getDataScopeType()));
  }

  /**
   * 当前用户是否有 ALL 数据权限
   *
   * @return true 表示用户至少有一个 ALL 角色
   */
  public boolean hasAllScope() {
    Long userId = getCurrentUserId();
    List<SysRole> roles = getRolesByUserId(userId);
    return roles.stream().anyMatch(r -> DataScopeType.ALL.name().equals(r.getDataScopeType()));
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
    Set<Long> orgIds = getAccessibleOrgIds();

    if (orgIds == null) {
      // ALL 范围
      return;
    }

    boolean hasSelf = hasSelfScope();

    if (orgIds.isEmpty() && !hasSelf) {
      wrapper.apply("1 = 0");
      return;
    }

    if (!orgIds.isEmpty() && hasSelf) {
      // ORG + SELF：部门范围内 OR 本人创建
      wrapper.and(w -> w.in(orgIdColumn, orgIds).or().eq(createdByColumn, getCurrentUserId()));
    } else if (!orgIds.isEmpty()) {
      wrapper.in(orgIdColumn, orgIds);
    } else if (hasSelf) {
      wrapper.eq(createdByColumn, getCurrentUserId());
    }
  }

  // ==================== 内部方法 ====================

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
  List<SysRole> getRolesByUserId(Long userId) {
    List<SysUserRole> userRoles =
        userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
    if (userRoles.isEmpty()) {
      return List.of();
    }
    List<Long> roleIds =
        userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds));
  }

  /** 获取角色绑定的部门ID列表 */
  private Set<Long> getRoleOrgIds(Long roleId) {
    List<SysRoleOrg> roleOrgs =
        roleOrgMapper.selectList(
            new LambdaQueryWrapper<SysRoleOrg>().eq(SysRoleOrg::getRoleId, roleId));
    return roleOrgs.stream().map(SysRoleOrg::getOrgId).collect(Collectors.toSet());
  }

  /**
   * 获取某个部门的所有下级部门ID
   *
   * <p>通过 OrgUnitDAO 递归查找所有子部门。
   */
  private Set<Long> getDescendantOrgIds(Long orgId) {
    Set<Long> result = new HashSet<>();
    collectDescendantIds(orgId, result);
    return result;
  }

  /** 递归收集下级部门ID */
  private void collectDescendantIds(Long parentId, Set<Long> result) {
    List<Long> childIds =
        orgUnitDAO.findByParentId(parentId).stream()
            .map(org -> org.getId())
            .collect(Collectors.toList());
    for (Long childId : childIds) {
      result.add(childId);
      collectDescendantIds(childId, result);
    }
  }
}
