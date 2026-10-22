package io.github.lystrosaurus.admin.auth.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.auth.domain.acl.ExternalAccountRepository;
import io.github.lystrosaurus.admin.auth.domain.model.ExternalAccount;
import io.github.lystrosaurus.admin.auth.infrastructure.converter.ExternalAccountPoConverter;
import io.github.lystrosaurus.admin.auth.infrastructure.persistence.mapper.AuthExternalAccountMapper;
import io.github.lystrosaurus.admin.auth.infrastructure.persistence.po.AuthExternalAccountPO;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** 三方账号仓储的 MyBatis-Plus 实现。 */
@Repository
@RequiredArgsConstructor
public class ExternalAccountRepositoryImpl implements ExternalAccountRepository {

  private final AuthExternalAccountMapper mapper;
  private final ExternalAccountPoConverter converter;

  @Override
  public Optional<ExternalAccount> findById(Long id) {
    return Optional.ofNullable(mapper.selectById(id)).map(converter::toDomain);
  }

  @Override
  public Optional<ExternalAccount> findByProviderIdAndProviderUserId(
      Long providerId, String providerUserId) {
    return Optional.ofNullable(
            mapper.selectOne(
                new LambdaQueryWrapper<AuthExternalAccountPO>()
                    .eq(AuthExternalAccountPO::getProviderId, providerId)
                    .eq(AuthExternalAccountPO::getProviderUserId, providerUserId)))
        .map(converter::toDomain);
  }

  @Override
  public Optional<ExternalAccount> findByProviderIdAndUserId(Long providerId, Long userId) {
    return Optional.ofNullable(
            mapper.selectOne(
                new LambdaQueryWrapper<AuthExternalAccountPO>()
                    .eq(AuthExternalAccountPO::getProviderId, providerId)
                    .eq(AuthExternalAccountPO::getUserId, userId)))
        .map(converter::toDomain);
  }

  @Override
  public ExternalAccount save(ExternalAccount account) {
    AuthExternalAccountPO po = converter.toPo(account);
    mapper.insert(po);
    return converter.toDomain(po);
  }

  @Override
  public ExternalAccount update(ExternalAccount account) {
    AuthExternalAccountPO po = converter.toPo(account);
    mapper.updateById(po);
    return converter.toDomain(po);
  }

  @Override
  public List<ExternalAccount> listByUserId(Long userId) {
    return mapper
        .selectList(
            new LambdaQueryWrapper<AuthExternalAccountPO>()
                .eq(AuthExternalAccountPO::getUserId, userId)
                .orderByDesc(AuthExternalAccountPO::getCreatedAt))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public List<ExternalAccount> listByEmployeeId(Long employeeId) {
    return mapper
        .selectList(
            new LambdaQueryWrapper<AuthExternalAccountPO>()
                .eq(AuthExternalAccountPO::getEmployeeId, employeeId)
                .orderByDesc(AuthExternalAccountPO::getCreatedAt))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public long countActiveBindsByUserId(Long userId) {
    return mapper.selectCount(
        new LambdaQueryWrapper<AuthExternalAccountPO>()
            .eq(AuthExternalAccountPO::getUserId, userId)
            .eq(AuthExternalAccountPO::getBindStatus, "BOUND"));
  }
}
