package io.github.lystrosaurus.admin.auth.external.service;

import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.auth.external.dao.AuthExternalAccountDAO;
import io.github.lystrosaurus.admin.auth.external.dto.ExternalAccountBindDTO;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import io.github.lystrosaurus.admin.auth.external.mapstruct.ExternalAccountMapStruct;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 三方账号服务实现 */
@Service
@RequiredArgsConstructor
public class ExternalAccountService {

  private final AuthExternalAccountDAO accountDAO;
  private final ExternalAccountMapStruct accountMapStruct;
  private final UserDAO userDAO;

  @Transactional(rollbackFor = Exception.class)
  public ExternalAccountVO bind(ExternalAccountBindDTO dto) {
    // 归属安全：三方账号始终绑定到当前登录用户，绝不信任调用方传入的 userId
    Long currentUserId = UserContext.getCurrentUserId();

    // 检查 providerId+providerUserId 唯一性
    if (accountDAO.findByProviderIdAndProviderUserId(dto.providerId(), dto.providerUserId())
        != null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND);
    }

    // 检查 providerId+userId 唯一性（以当前登录用户为准，而非调用方传入的 userId）
    if (accountDAO.findByProviderIdAndUserId(dto.providerId(), currentUserId) != null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND);
    }

    AuthExternalAccount entity = accountMapStruct.toEntity(dto);
    // 强制归属：以当前登录用户身份绑定，覆盖调用方可能传入的任意 userId（IDOR 防护）
    entity.setUserId(currentUserId);
    entity.setBindStatus("BOUND");
    accountDAO.save(entity);
    return accountMapStruct.toVO(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void unbind(Long id) {
    AuthExternalAccount account = accountDAO.findById(id);
    if (account == null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }
    // 归属安全：仅允许解绑当前登录用户自己的三方账号（与 OAuthService.unbindAccount 一致）
    assertOwnedByCurrentUser(account);

    // 解绑前安全检查：避免用户失去唯一登录方式
    checkUnbindSafety(account.getUserId());

    account.setBindStatus("UNBOUND");
    accountDAO.updateById(account);
  }

  /**
   * 归属校验：断言账号属于当前登录用户，否则抛出与“不存在”相同的异常，避免泄露资源是否存在
   *
   * @param account 三方账号实体
   */
  private void assertOwnedByCurrentUser(AuthExternalAccount account) {
    Long currentUserId = UserContext.getCurrentUserId();
    if (!account.getUserId().equals(currentUserId)) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }
  }

  /**
   * 解绑前安全检查：如果用户无密码且只剩 1 个有效绑定，拒绝解绑
   *
   * @param userId 用户ID
   */
  private void checkUnbindSafety(Long userId) {
    // 检查用户是否有密码
    SysUser user = userDAO.findById(userId);
    boolean hasPassword =
        user != null && user.getPasswordHash() != null && !user.getPasswordHash().isBlank();

    // 统计当前用户的有效绑定数
    long activeBoundCount = accountDAO.countActiveBindsByUserId(userId);

    // 如果无密码且只剩 1 个绑定，拒绝解绑
    if (!hasPassword && activeBoundCount <= 1) {
      throw new BusinessException(ErrorCode.UNBIND_LAST_LOGIN_METHOD);
    }
  }

  public ExternalAccountVO getById(Long id) {
    AuthExternalAccount account = accountDAO.findById(id);
    if (account == null) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
    }
    // 归属安全：仅允许查看当前登录用户自己的三方账号（IDOR 防护）
    assertOwnedByCurrentUser(account);
    return accountMapStruct.toVO(account);
  }

  public List<ExternalAccountVO> listByUserId(Long userId) {
    return accountDAO.listByUserId(userId).stream().map(accountMapStruct::toVO).toList();
  }

  public List<ExternalAccountVO> listByEmployeeId(Long employeeId) {
    return accountDAO.listByEmployeeId(employeeId).stream().map(accountMapStruct::toVO).toList();
  }

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
