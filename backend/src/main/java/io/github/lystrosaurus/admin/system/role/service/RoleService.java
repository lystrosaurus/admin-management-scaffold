package io.github.lystrosaurus.admin.system.role.service;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.mapstruct.MenuMapper;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapstruct.PermissionMapper;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
import io.github.lystrosaurus.admin.system.role.dao.RoleDAO;
import io.github.lystrosaurus.admin.system.role.dto.RoleCreateDTO;
import io.github.lystrosaurus.admin.system.role.dto.RoleUpdateDTO;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.mapstruct.RoleMapper;
import io.github.lystrosaurus.admin.system.role.vo.RoleDetailVO;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 角色服务实现 */
@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleDAO roleDAO;
  private final RoleMapper roleMapper;
  private final PermissionMapper permissionMapper;
  private final MenuMapper menuMapper;

  @Transactional(rollbackFor = Exception.class)
  public RoleVO create(RoleCreateDTO dto) {
    // 检查角色编码唯一性
    if (roleDAO.existsByCode(dto.code())) {
      throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
    }

    // 转换并保存角色
    SysRole role = roleMapper.toEntity(dto);
    role.setStatus("ENABLED");
    roleDAO.save(role);

    return roleMapper.toRoleVO(role);
  }

  @Transactional(rollbackFor = Exception.class)
  public RoleVO update(Long id, RoleUpdateDTO dto) {
    // 查找角色
    SysRole role = roleDAO.findById(id);
    if (role == null) {
      throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
    }

    // 更新字段
    if (StringUtils.hasText(dto.name())) {
      role.setName(dto.name());
    }
    if (StringUtils.hasText(dto.description())) {
      role.setDescription(dto.description());
    }
    if (dto.sortOrder() != null) {
      role.setSortOrder(dto.sortOrder());
    }
    if (StringUtils.hasText(dto.status())) {
      role.setStatus(dto.status());
    }
    if (StringUtils.hasText(dto.dataScopeType())) {
      role.setDataScopeType(dto.dataScopeType());
    }

    roleDAO.update(role);
    return roleMapper.toRoleVO(role);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    roleDAO.deleteById(id);
  }

  public RoleDetailVO findById(Long id) {
    // 查找角色
    SysRole role = roleDAO.findById(id);
    if (role == null) {
      throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
    }

    // 查询角色权限
    List<SysPermission> permissions = roleDAO.findPermissionsByRoleId(id);
    List<PermissionVO> permissionVOs =
        permissions.stream().map(permissionMapper::toPermissionVO).toList();

    // 查询角色菜单
    List<SysMenu> menus = roleDAO.findMenusByRoleId(id);
    List<MenuVO> menuVOs = menus.stream().map(menuMapper::toMenuVO).toList();

    // 直接从实体构建详情VO，避免先MapStruct转换再手动重建的浪费
    return new RoleDetailVO(
        role.getId(),
        role.getCode(),
        role.getName(),
        role.getDescription(),
        role.getSortOrder(),
        role.getStatus(),
        role.getDataScopeType(),
        permissionVOs,
        menuVOs,
        role.getCreatedAt());
  }

  public List<RoleVO> findAll() {
    List<SysRole> roles = roleDAO.findAll();
    return roles.stream().map(roleMapper::toRoleVO).toList();
  }

  public List<RoleVO> findByUserId(Long userId) {
    List<SysRole> roles = roleDAO.findByUserId(userId);
    return roles.stream().map(roleMapper::toRoleVO).toList();
  }

  @Transactional(rollbackFor = Exception.class)
  public void assignPermissions(Long roleId, List<Long> permissionIds) {
    // 检查角色是否存在
    SysRole role = roleDAO.findById(roleId);
    if (role == null) {
      throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
    }

    // 移除现有权限并分配新权限
    roleDAO.removePermissions(roleId);
    if (permissionIds != null && !permissionIds.isEmpty()) {
      roleDAO.assignPermissions(roleId, permissionIds);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void assignMenus(Long roleId, List<Long> menuIds) {
    // 检查角色是否存在
    SysRole role = roleDAO.findById(roleId);
    if (role == null) {
      throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
    }

    // 移除现有菜单并分配新菜单
    roleDAO.removeMenus(roleId);
    if (menuIds != null && !menuIds.isEmpty()) {
      roleDAO.assignMenus(roleId, menuIds);
    }
  }
}
