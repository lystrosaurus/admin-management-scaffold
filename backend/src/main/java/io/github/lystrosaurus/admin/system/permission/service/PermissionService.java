package io.github.lystrosaurus.admin.system.permission.service;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.permission.dao.PermissionDAO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionCreateDTO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionUpdateDTO;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapstruct.PermissionMapper;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 权限服务实现 */
@Service
@RequiredArgsConstructor
public class PermissionService {

  private final PermissionDAO permissionDAO;
  private final PermissionMapper permissionMapper;

  @Transactional(rollbackFor = Exception.class)
  public PermissionVO create(PermissionCreateDTO dto) {
    // 检查权限编码唯一性
    if (permissionDAO.existsByCode(dto.code())) {
      throw new BusinessException(ErrorCode.DATA_DUPLICATE_KEY);
    }

    // 转换并保存权限
    SysPermission permission = new SysPermission();
    permission.setCode(dto.code());
    permission.setName(dto.name());
    permission.setType(dto.type());
    permission.setModule(dto.module());
    permission.setResource(dto.resource());
    permission.setAction(dto.action());
    permission.setStatus("ENABLED");
    permissionDAO.save(permission);

    return permissionMapper.toPermissionVO(permission);
  }

  @Transactional(rollbackFor = Exception.class)
  public PermissionVO update(Long id, PermissionUpdateDTO dto) {
    // 查找权限
    SysPermission permission = permissionDAO.findById(id);
    if (permission == null) {
      throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND);
    }

    // 更新字段
    if (StringUtils.hasText(dto.name())) {
      permission.setName(dto.name());
    }
    if (StringUtils.hasText(dto.type())) {
      permission.setType(dto.type());
    }
    if (StringUtils.hasText(dto.module())) {
      permission.setModule(dto.module());
    }
    if (StringUtils.hasText(dto.resource())) {
      permission.setResource(dto.resource());
    }
    if (StringUtils.hasText(dto.action())) {
      permission.setAction(dto.action());
    }
    if (StringUtils.hasText(dto.status())) {
      permission.setStatus(dto.status());
    }

    permissionDAO.update(permission);
    return permissionMapper.toPermissionVO(permission);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    permissionDAO.deleteById(id);
  }

  public List<PermissionVO> findByRoleId(Long roleId) {
    List<SysPermission> permissions = permissionDAO.findByRoleId(roleId);
    return permissions.stream().map(permissionMapper::toPermissionVO).toList();
  }

  public List<PermissionVO> findByUserId(Long userId) {
    List<SysPermission> permissions = permissionDAO.findByUserId(userId);
    return permissions.stream().map(permissionMapper::toPermissionVO).toList();
  }

  public List<PermissionVO> findAll() {
    List<SysPermission> permissions = permissionDAO.findAll();
    return permissions.stream().map(permissionMapper::toPermissionVO).toList();
  }
}
