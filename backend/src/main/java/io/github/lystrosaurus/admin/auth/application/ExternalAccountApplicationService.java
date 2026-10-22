package io.github.lystrosaurus.admin.auth.application;

import io.github.lystrosaurus.admin.auth.adapter.response.ExternalAccountResponse;
import io.github.lystrosaurus.admin.auth.application.command.BindExternalAccountCommand;
import io.github.lystrosaurus.admin.auth.domain.acl.ExternalAccountRepository;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.auth.domain.model.ExternalAccount;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 三方账号应用服务。 */
@Service
@RequiredArgsConstructor
public class ExternalAccountApplicationService {

  private final ExternalAccountRepository accountRepository;
  private final UserRepository userRepository;
  private final TokenSessionPort tokenSessionPort;

  /** 绑定三方账号 */
  @Transactional(rollbackFor = Exception.class)
  public ExternalAccountResponse bind(BindExternalAccountCommand command) {
    // 归属安全：三方账号始终绑定到当前登录用户，绝不信任调用方传入的 userId
    Long currentUserId = tokenSessionPort.currentUserId();

    // 检查 providerId+providerUserId 唯一性
    if (accountRepository
        .findByProviderIdAndProviderUserId(command.providerId(), command.providerUserId())
        .isPresent()) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND);
    }

    // 检查 providerId+userId 唯一性（以当前登录用户为准，而非调用方传入的 userId）
    if (accountRepository
        .findByProviderIdAndUserId(command.providerId(), currentUserId)
        .isPresent()) {
      throw new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_ALREADY_BOUND);
    }

    ExternalAccount account =
        new ExternalAccount(
            null,
            command.providerId(),
            command.providerUserId(),
            currentUserId,
            command.employeeId(),
            command.identifierJson(),
            command.nickname(),
            command.avatarUrl(),
            "BOUND",
            null,
            null,
            null);
    return toResponse(accountRepository.save(account));
  }

  /** 解绑三方账号 */
  @Transactional(rollbackFor = Exception.class)
  public void unbind(Long id) {
    ExternalAccount account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));
    // 归属安全：仅允许解绑当前登录用户自己的三方账号
    assertOwnedByCurrentUser(account);

    // 解绑前安全检查：避免用户失去唯一登录方式
    checkUnbindSafety(account.getUserId());

    account.markUnbound();
    accountRepository.update(account);
  }

  /** 按ID查询三方账号 */
  public ExternalAccountResponse getById(Long id) {
    ExternalAccount account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));
    // 归属安全：仅允许查看当前登录用户自己的三方账号（IDOR 防护）
    assertOwnedByCurrentUser(account);
    return toResponse(account);
  }

  /** 按用户ID查询三方账号列表 */
  public List<ExternalAccountResponse> listByUserId(Long userId) {
    return accountRepository.listByUserId(userId).stream().map(this::toResponse).toList();
  }

  /** 按员工ID查询三方账号列表 */
  public List<ExternalAccountResponse> listByEmployeeId(Long employeeId) {
    return accountRepository.listByEmployeeId(employeeId).stream().map(this::toResponse).toList();
  }

  /** 更新最后登录时间 */
  @Transactional(rollbackFor = Exception.class)
  public void updateLastLoginAt(Long id) {
    ExternalAccount account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));

    account.touchLastLogin(LocalDateTime.now());
    accountRepository.update(account);
  }

  /**
   * 归属校验：断言账号属于当前登录用户，否则抛出与“不存在”相同的异常，避免泄露资源是否存在
   *
   * @param account 三方账号
   */
  private void assertOwnedByCurrentUser(ExternalAccount account) {
    Long currentUserId = tokenSessionPort.currentUserId();
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
    User user = userRepository.findById(UserId.of(userId)).orElse(null);
    boolean hasPassword =
        user != null && user.passwordHash() != null && !user.passwordHash().isBlank();

    // 统计当前用户的有效绑定数
    long activeBoundCount = accountRepository.countActiveBindsByUserId(userId);

    // 如果无密码且只剩 1 个绑定，拒绝解绑
    if (!hasPassword && activeBoundCount <= 1) {
      throw new BusinessException(ErrorCode.UNBIND_LAST_LOGIN_METHOD);
    }
  }

  private ExternalAccountResponse toResponse(ExternalAccount account) {
    return new ExternalAccountResponse(
        account.getId(),
        account.getProviderId(),
        null,
        account.getProviderUserId(),
        account.getUserId(),
        account.getEmployeeId(),
        account.getNickname(),
        account.getAvatarUrl(),
        account.getBindStatus(),
        account.getLastLoginAt(),
        account.getCreatedAt(),
        account.getUpdatedAt());
  }
}
