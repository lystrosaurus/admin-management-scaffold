package io.github.lystrosaurus.admin.auth.external.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.auth.external.dao.AuthExternalAccountDAO;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import io.github.lystrosaurus.admin.auth.external.mapper.AuthExternalAccountMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 三方账号数据访问对象实现 */
@Service
@RequiredArgsConstructor
public class AuthExternalAccountDAOImpl implements AuthExternalAccountDAO {

  private final AuthExternalAccountMapper accountMapper;

  @Override
  public void save(AuthExternalAccount account) {
    accountMapper.insert(account);
  }

  @Override
  public AuthExternalAccount findById(Long id) {
    return accountMapper.selectById(id);
  }

  @Override
  public AuthExternalAccount findByProviderIdAndProviderUserId(
      Long providerId, String providerUserId) {
    return accountMapper.selectOne(
        new LambdaQueryWrapper<AuthExternalAccount>()
            .eq(AuthExternalAccount::getProviderId, providerId)
            .eq(AuthExternalAccount::getProviderUserId, providerUserId));
  }

  @Override
  public AuthExternalAccount findByProviderIdAndUserId(Long providerId, Long userId) {
    return accountMapper.selectOne(
        new LambdaQueryWrapper<AuthExternalAccount>()
            .eq(AuthExternalAccount::getProviderId, providerId)
            .eq(AuthExternalAccount::getUserId, userId));
  }

  @Override
  public void updateById(AuthExternalAccount account) {
    accountMapper.updateById(account);
  }

  @Override
  public List<AuthExternalAccount> listByUserId(Long userId) {
    return accountMapper.selectList(
        new LambdaQueryWrapper<AuthExternalAccount>()
            .eq(AuthExternalAccount::getUserId, userId)
            .orderByDesc(AuthExternalAccount::getCreatedAt));
  }

  @Override
  public List<AuthExternalAccount> listByEmployeeId(Long employeeId) {
    return accountMapper.selectList(
        new LambdaQueryWrapper<AuthExternalAccount>()
            .eq(AuthExternalAccount::getEmployeeId, employeeId)
            .orderByDesc(AuthExternalAccount::getCreatedAt));
  }
}
