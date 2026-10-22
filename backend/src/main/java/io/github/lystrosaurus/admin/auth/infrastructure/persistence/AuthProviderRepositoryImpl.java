package io.github.lystrosaurus.admin.auth.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.auth.domain.acl.AuthProviderCatalog;
import io.github.lystrosaurus.admin.auth.domain.acl.AuthProviderRepository;
import io.github.lystrosaurus.admin.auth.domain.model.AuthProvider;
import io.github.lystrosaurus.admin.auth.infrastructure.converter.AuthProviderPoConverter;
import io.github.lystrosaurus.admin.auth.infrastructure.persistence.mapper.AuthProviderMapper;
import io.github.lystrosaurus.admin.auth.infrastructure.persistence.po.AuthProviderPO;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** 认证源仓储的 MyBatis-Plus 实现。 */
@Repository
@RequiredArgsConstructor
public class AuthProviderRepositoryImpl implements AuthProviderRepository, AuthProviderCatalog {

  private final AuthProviderMapper mapper;
  private final AuthProviderPoConverter converter;

  @Override
  public Optional<AuthProvider> findById(Long id) {
    return Optional.ofNullable(mapper.selectById(id)).map(converter::toDomain);
  }

  @Override
  public Optional<AuthProvider> findByCode(String code) {
    return Optional.ofNullable(
            mapper.selectOne(
                new LambdaQueryWrapper<AuthProviderPO>().eq(AuthProviderPO::getCode, code)))
        .map(converter::toDomain);
  }

  @Override
  public List<AuthProvider> findAll() {
    return mapper
        .selectList(
            new LambdaQueryWrapper<AuthProviderPO>().orderByDesc(AuthProviderPO::getCreatedAt))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public AuthProvider save(AuthProvider provider) {
    AuthProviderPO po = converter.toPo(provider);
    if (po.getId() == null) {
      mapper.insert(po);
    } else {
      mapper.updateById(po);
    }
    return converter.toDomain(po);
  }

  @Override
  public void deleteById(Long id) {
    mapper.deleteById(id);
  }
}
