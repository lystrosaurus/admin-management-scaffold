package io.github.lystrosaurus.admin.system.permission.application;

import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.permission.application.assembler.PermissionAssembler;
import io.github.lystrosaurus.admin.system.permission.application.command.PermissionCreateCommand;
import io.github.lystrosaurus.admin.system.permission.application.command.PermissionUpdateCommand;
import io.github.lystrosaurus.admin.system.permission.adapter.response.PermissionResponse;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionQueryRepository;
import io.github.lystrosaurus.admin.system.permission.domain.acl.PermissionRepository;
import io.github.lystrosaurus.admin.system.permission.domain.model.Permission;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionErrorCode;
import io.github.lystrosaurus.admin.system.permission.domain.model.PermissionId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 权限应用服务：编排权限聚合的创建、更新、删除与查询。 */
@Service
@RequiredArgsConstructor
public class PermissionApplicationService {

  private final PermissionRepository permissionRepository;
  private final PermissionQueryRepository permissionQueryRepository;
  private final IdGenerator idGenerator;
  private final PermissionAssembler assembler;

  /** 创建权限 */
  @Transactional(rollbackFor = Exception.class)
  public PermissionResponse create(PermissionCreateCommand command) {
    if (permissionRepository.existsByCode(command.code())) {
      throw new DomainException(PermissionErrorCode.PERMISSION_DUPLICATE_KEY);
    }
    Permission permission =
        Permission.create(
            PermissionId.of(idGenerator.nextId()),
            command.code(),
            command.name(),
            command.type(),
            command.module(),
            command.resource(),
            command.action());
    permissionRepository.save(permission);
    return assembler.toResponse(permission);
  }

  /** 更新权限 */
  @Transactional(rollbackFor = Exception.class)
  public PermissionResponse update(Long id, PermissionUpdateCommand command) {
    Permission permission =
        permissionRepository
            .findById(PermissionId.of(id))
            .orElseThrow(() -> new DomainException(PermissionErrorCode.PERMISSION_NOT_FOUND));
    permission.update(
        command.name(),
        command.type(),
        command.module(),
        command.resource(),
        command.action(),
        command.status());
    permissionRepository.save(permission);
    return assembler.toResponse(permission);
  }

  /** 删除权限 */
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    permissionRepository.deleteById(PermissionId.of(id));
  }

  /** 按角色查询权限 */
  public List<PermissionResponse> findByRoleId(Long roleId) {
    return permissionQueryRepository.findByRoleId(roleId).stream()
        .map(assembler::toResponse)
        .toList();
  }

  /** 按用户查询权限 */
  public List<PermissionResponse> findByUserId(Long userId) {
    return permissionQueryRepository.findByUserId(userId).stream()
        .map(assembler::toResponse)
        .toList();
  }

  /** 查询全部权限 */
  public List<PermissionResponse> findAll() {
    return permissionQueryRepository.findAll().stream().map(assembler::toResponse).toList();
  }
}
