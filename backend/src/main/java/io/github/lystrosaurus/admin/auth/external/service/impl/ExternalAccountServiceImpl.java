package io.github.lystrosaurus.admin.auth.external.service.impl;

import io.github.lystrosaurus.admin.auth.external.dao.AuthExternalAccountDAO;
import io.github.lystrosaurus.admin.auth.external.dto.ExternalAccountBindDTO;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import io.github.lystrosaurus.admin.auth.external.mapstruct.ExternalAccountMapStruct;
import io.github.lystrosaurus.admin.auth.external.service.ExternalAccountService;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 三方账号服务实现 */
@Service
@RequiredArgsConstructor
public class ExternalAccountServiceImpl implements ExternalAccountService {

  private final AuthExternalAccountDAO accountDAO;
  private final ExternalAccountMapStruct accountMapStruct;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ExternalAccountVO bind(ExternalAccountBindDTO dto) {
    // 检查 providerId+providerUserId 唯一性
    if (accountDAO.findByProviderIdAndProviderUserId(dto.providerId(), dto.providerUserId())
        != null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND);
    }

    // 检查 providerId+userId 唯一性
    if (dto.userId() != null
        && accountDAO.findByProviderIdAndUserId(dto.providerId(), dto.userId()) != null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND);
    }

    AuthExternalAccount entity = accountMapStruct.toEntity(dto);
    entity.setBindStatus("BOUND");
    accountDAO.save(entity);
    return accountMapStruct.toVO(entity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void unbind(Long id) {
    AuthExternalAccount account = accountDAO.findById(id);
    if (account == null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }

    account.setBindStatus("UNBOUND");
    accountDAO.updateById(account);
  }

  @Override
  public ExternalAccountVO getById(Long id) {
    AuthExternalAccount account = accountDAO.findById(id);
    if (account == null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }
    return accountMapStruct.toVO(account);
  }

  @Override
  public List<ExternalAccountVO> listByUserId(Long userId) {
    return accountDAO.listByUserId(userId).stream()
        .map(accountMapStruct::toVO)
        .collect(Collectors.toList());
  }

  @Override
  public List<ExternalAccountVO> listByEmployeeId(Long employeeId) {
    return accountDAO.listByEmployeeId(employeeId).stream()
        .map(accountMapStruct::toVO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateLastLoginAt(Long id) {
    AuthExternalAccount account = accountDAO.findById(id);
    if (account == null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }

    account.setLastLoginAt(LocalDateTime.now());
    accountDAO.updateById(account);
  }
}
