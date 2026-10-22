package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper;

import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RoleOrgPO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色-组织关联 Mapper。
 *
 * <p>sys_role_org 为无审计列的轻量关联表，且 {@link RoleOrgPO} 不携带 {@code @TableName}，故不继承
 * MyBatis-Plus {@code BaseMapper}，改用显式 SQL 映射。
 */
@Mapper
public interface RoleOrgMpMapper {

  /** 查询角色绑定的组织关联列表 */
  @Select(
      "SELECT id, role_id AS roleId, org_id AS orgId, created_at AS createdAt, created_by AS createdBy "
          + "FROM sys_role_org WHERE role_id = #{roleId}")
  List<RoleOrgPO> findByRoleId(@Param("roleId") Long roleId);
}
