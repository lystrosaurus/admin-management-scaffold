package io.github.lystrosaurus.admin.system.role.dao;

import io.github.lystrosaurus.admin.system.role.entity.SysRoleOrg;
import java.util.List;

/** 角色-组织关联数据访问对象接口 */
public interface RoleOrgDAO {

  /**
   * 保存角色-组织关联
   *
   * @param roleOrg 关联实体
   */
  void save(SysRoleOrg roleOrg);

  /**
   * 根据角色ID查找关联列表
   *
   * @param roleId 角色ID
   * @return 关联列表
   */
  List<SysRoleOrg> findByRoleId(Long roleId);

  /**
   * 根据角色ID删除所有关联
   *
   * @param roleId 角色ID
   */
  void deleteByRoleId(Long roleId);

  /**
   * 批量保存角色-组织关联
   *
   * @param roleOrgs 关联列表
   */
  void batchSave(List<SysRoleOrg> roleOrgs);
}
