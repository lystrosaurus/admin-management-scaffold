package io.github.lystrosaurus.admin.system.role.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.system.role.dao.RoleOrgDAO;
import io.github.lystrosaurus.admin.system.role.entity.SysRoleOrg;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleOrgMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 角色-组织关联数据访问对象实现 */
@Service
@RequiredArgsConstructor
public class RoleOrgDAOImpl implements RoleOrgDAO {

  private final SysRoleOrgMapper roleOrgMapper;

  @Override
  public void save(SysRoleOrg roleOrg) {
    roleOrgMapper.insert(roleOrg);
  }

  @Override
  public List<SysRoleOrg> findByRoleId(Long roleId) {
    return roleOrgMapper.selectList(
        new LambdaQueryWrapper<SysRoleOrg>().eq(SysRoleOrg::getRoleId, roleId));
  }

  @Override
  public void deleteByRoleId(Long roleId) {
    roleOrgMapper.delete(new LambdaQueryWrapper<SysRoleOrg>().eq(SysRoleOrg::getRoleId, roleId));
  }

  @Override
  public void batchSave(List<SysRoleOrg> roleOrgs) {
    if (roleOrgs == null || roleOrgs.isEmpty()) {
      return;
    }
    for (SysRoleOrg roleOrg : roleOrgs) {
      roleOrgMapper.insert(roleOrg);
    }
  }
}
