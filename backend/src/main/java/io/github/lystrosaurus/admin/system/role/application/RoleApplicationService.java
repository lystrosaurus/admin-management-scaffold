package io.github.lystrosaurus.admin.system.role.application;

import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryPort;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryPort;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleDetailResponse;
import io.github.lystrosaurus.admin.system.role.adapter.response.RoleResponse;
import io.github.lystrosaurus.admin.system.role.application.assembler.RoleAssembler;
import io.github.lystrosaurus.admin.system.role.application.command.RoleCreateCommand;
import io.github.lystrosaurus.admin.system.role.application.command.RoleUpdateCommand;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleQueryRepository;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleRepository;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import io.github.lystrosaurus.admin.system.role.domain.model.RoleErrorCode;
import io.github.lystrosaurus.admin.system.role.domain.model.RoleId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 角色应用服务：编排角色聚合与权限/菜单关联的读写。 */
@Service
@RequiredArgsConstructor
public class RoleApplicationService {

  private final RoleRepository roleRepository;
  private final RoleQueryRepository roleQueryRepository;
  private final PermissionQueryPort permissionQueryPort;
  private final MenuQueryPort menuQueryPort;
  private final IdGenerator idGenerator;
  private final RoleAssembler assembler;

  /** 创建角色 */
  @Transactional(rollbackFor = Exception.class)
  public RoleResponse create(RoleCreateCommand command) {
    if (roleRepository.existsByCode(command.code())) {
      throw new DomainException(RoleErrorCode.ROLE_ALREADY_EXISTS);
    }
    Role role =
        Role.create(
            RoleId.of(idGenerator.nextId()),
            command.code(),
            command.name(),
            command.description(),
            command.sortOrder(),
            command.dataScopeType());
    roleRepository.save(role);
    return assembler.toResponse(role);
  }

  /** 更新角色 */
  @Transactional(rollbackFor = Exception.class)
  public RoleResponse update(Long id, RoleUpdateCommand command) {
    Role role = findRole(id);
    role.update(
        command.name(),
        command.description(),
        command.sortOrder(),
        command.status(),
        command.dataScopeType());
    roleRepository.save(role);
    return assembler.toResponse(role);
  }

  /** 删除角色 */
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    roleRepository.deleteById(RoleId.of(id));
  }

  /** 查询角色详情（含权限与菜单） */
  public RoleDetailResponse findById(Long id) {
    Role role = findRole(id);
    return assembler.toDetailResponse(
        role, permissionQueryPort.findByRoleId(id), menuQueryPort.findByRoleId(id));
  }

  /** 查询全部角色 */
  public List<RoleResponse> findAll() {
    return roleQueryRepository.findAll().stream().map(assembler::toResponse).toList();
  }

  /** 分配权限给角色 */
  @Transactional(rollbackFor = Exception.class)
  public void assignPermissions(Long roleId, List<Long> permissionIds) {
    Role role = findRole(roleId);
    role.changePermissions(permissionIds);
    roleRepository.replacePermissions(RoleId.of(roleId), permissionIds);
    roleRepository.save(role);
  }

  /** 分配菜单给角色 */
  @Transactional(rollbackFor = Exception.class)
  public void assignMenus(Long roleId, List<Long> menuIds) {
    Role role = findRole(roleId);
    role.changeMenus(menuIds);
    roleRepository.replaceMenus(RoleId.of(roleId), menuIds);
    roleRepository.save(role);
  }

  private Role findRole(Long id) {
    return roleRepository
        .findById(RoleId.of(id))
        .orElseThrow(() -> new DomainException(RoleErrorCode.ROLE_NOT_FOUND));
  }
}
