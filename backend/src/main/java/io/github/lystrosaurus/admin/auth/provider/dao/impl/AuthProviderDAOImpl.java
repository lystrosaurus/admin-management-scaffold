package io.github.lystrosaurus.admin.auth.provider.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.auth.provider.dao.AuthProviderDAO;
import io.github.lystrosaurus.admin.auth.provider.entity.AuthProvider;
import io.github.lystrosaurus.admin.auth.provider.mapper.AuthProviderMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 认证源数据访问对象实现 */
@Service
@RequiredArgsConstructor
public class AuthProviderDAOImpl implements AuthProviderDAO {

  private final AuthProviderMapper providerMapper;

  @Override
  public void save(AuthProvider provider) {
    providerMapper.insert(provider);
  }

  @Override
  public AuthProvider findById(Long id) {
    return providerMapper.selectById(id);
  }

  @Override
  public AuthProvider findByCode(String code) {
    return providerMapper.selectOne(
        new LambdaQueryWrapper<AuthProvider>().eq(AuthProvider::getCode, code));
  }

  @Override
  public void updateById(AuthProvider provider) {
    providerMapper.updateById(provider);
  }

  @Override
  public void deleteById(Long id) {
    providerMapper.deleteById(id);
  }

  @Override
  public List<AuthProvider> listAll() {
    return providerMapper.selectList(
        new LambdaQueryWrapper<AuthProvider>().orderByDesc(AuthProvider::getCreatedAt));
  }
}
