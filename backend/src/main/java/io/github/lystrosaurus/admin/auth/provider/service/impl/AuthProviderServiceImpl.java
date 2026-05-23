package io.github.lystrosaurus.admin.auth.provider.service.impl;

import io.github.lystrosaurus.admin.auth.provider.dao.AuthProviderDAO;
import io.github.lystrosaurus.admin.auth.provider.dto.AuthProviderCreateDTO;
import io.github.lystrosaurus.admin.auth.provider.dto.AuthProviderUpdateDTO;
import io.github.lystrosaurus.admin.auth.provider.entity.AuthProvider;
import io.github.lystrosaurus.admin.auth.provider.mapstruct.AuthProviderMapStruct;
import io.github.lystrosaurus.admin.auth.provider.service.AuthProviderService;
import io.github.lystrosaurus.admin.auth.provider.vo.AuthProviderVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 认证源服务实现 */
@Service
@RequiredArgsConstructor
public class AuthProviderServiceImpl implements AuthProviderService {

  private final AuthProviderDAO providerDAO;
  private final AuthProviderMapStruct providerMapStruct;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AuthProviderVO create(AuthProviderCreateDTO dto) {
    // 检查编码唯一性
    if (providerDAO.findByCode(dto.code()) != null) {
      throw new BusinessException(ErrorCode.AUTH_PROVIDER_ALREADY_EXISTS);
    }

    AuthProvider entity = providerMapStruct.toEntity(dto);
    providerDAO.save(entity);
    return providerMapStruct.toVO(entity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AuthProviderVO update(Long id, AuthProviderUpdateDTO dto) {
    AuthProvider entity = providerDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.AUTH_PROVIDER_NOT_FOUND);
    }

    // 使用 MapStruct 更新非null字段
    providerMapStruct.updateEntity(dto, entity);
    providerDAO.updateById(entity);
    return providerMapStruct.toVO(entity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    AuthProvider entity = providerDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.AUTH_PROVIDER_NOT_FOUND);
    }
    providerDAO.deleteById(id);
  }

  @Override
  public AuthProviderVO getById(Long id) {
    AuthProvider entity = providerDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.AUTH_PROVIDER_NOT_FOUND);
    }
    return providerMapStruct.toVO(entity);
  }

  @Override
  public List<AuthProviderVO> list() {
    return providerDAO.listAll().stream().map(providerMapStruct::toVO).collect(Collectors.toList());
  }

  @Override
  public AuthProviderVO getByCode(String code) {
    AuthProvider entity = providerDAO.findByCode(code);
    if (entity == null) {
      throw new BusinessException(ErrorCode.AUTH_PROVIDER_NOT_FOUND);
    }
    return providerMapStruct.toVO(entity);
  }
}
