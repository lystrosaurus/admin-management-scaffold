package io.github.lystrosaurus.admin.auth.application;

import io.github.lystrosaurus.admin.auth.adapter.response.AuthProviderResponse;
import io.github.lystrosaurus.admin.auth.application.command.AuthProviderCreateCommand;
import io.github.lystrosaurus.admin.auth.application.command.AuthProviderUpdateCommand;
import io.github.lystrosaurus.admin.auth.domain.acl.AuthProviderRepository;
import io.github.lystrosaurus.admin.auth.domain.model.AuthProvider;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 认证源应用服务。 */
@Service
@RequiredArgsConstructor
public class AuthProviderApplicationService {

  private final AuthProviderRepository providerRepository;

  /** 创建认证源 */
  @Transactional(rollbackFor = Exception.class)
  public AuthProviderResponse create(AuthProviderCreateCommand command) {
    if (providerRepository.findByCode(command.code()).isPresent()) {
      throw new BusinessException(ErrorCode.AUTH_PROVIDER_ALREADY_EXISTS);
    }

    AuthProvider provider =
        new AuthProvider(
            null,
            command.code(),
            command.name(),
            command.clientId(),
            command.clientSecretEncrypted(),
            command.redirectUri(),
            command.scopes(),
            command.enabled(),
            command.configJson(),
            null,
            null);
    return toResponse(providerRepository.save(provider));
  }

  /** 更新认证源（部分更新：仅非空字段生效） */
  @Transactional(rollbackFor = Exception.class)
  public AuthProviderResponse update(Long id, AuthProviderUpdateCommand command) {
    AuthProvider provider = findOrThrow(id);
    AuthProvider updated =
        provider.update(
            command.name(),
            command.clientId(),
            command.clientSecretEncrypted(),
            command.redirectUri(),
            command.scopes(),
            command.enabled(),
            command.configJson());
    return toResponse(providerRepository.save(updated));
  }

  /** 删除认证源 */
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    findOrThrow(id);
    providerRepository.deleteById(id);
  }

  /** 按ID查询认证源 */
  public AuthProviderResponse getById(Long id) {
    return toResponse(findOrThrow(id));
  }

  /** 查询所有认证源 */
  public List<AuthProviderResponse> list() {
    return providerRepository.findAll().stream().map(this::toResponse).toList();
  }

  /** 按编码查询认证源 */
  public AuthProviderResponse getByCode(String code) {
    return toResponse(
        providerRepository
            .findByCode(code)
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_PROVIDER_NOT_FOUND)));
  }

  /** 按编码查询已启用认证源 */
  public AuthProviderResponse getEnabledByCode(String code) {
    AuthProvider provider =
        providerRepository
            .findByCode(code)
            .orElseThrow(() -> new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND));
    if (!provider.isEnabled()) {
      throw new BusinessException(ErrorCode.OAUTH_PROVIDER_DISABLED);
    }
    return toResponse(provider);
  }

  /** 按编码查询客户端密钥（V1 直接返回明文） */
  public String getClientSecret(String code) {
    return providerRepository
        .findByCode(code)
        .orElseThrow(() -> new BusinessException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND))
        .clientSecretEncrypted();
  }

  private AuthProvider findOrThrow(Long id) {
    return providerRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_PROVIDER_NOT_FOUND));
  }

  private AuthProviderResponse toResponse(AuthProvider provider) {
    return new AuthProviderResponse(
        provider.id(),
        provider.code(),
        provider.name(),
        provider.clientId(),
        provider.redirectUri(),
        provider.scopes(),
        provider.enabled(),
        provider.configJson(),
        provider.createdAt(),
        provider.updatedAt());
  }
}
