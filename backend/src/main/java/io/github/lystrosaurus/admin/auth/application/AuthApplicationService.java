package io.github.lystrosaurus.admin.auth.application;

import io.github.lystrosaurus.admin.auth.adapter.response.LoginResponse;
import io.github.lystrosaurus.admin.auth.application.command.LoginCommand;
import io.github.lystrosaurus.admin.auth.domain.acl.ClientRequestInfoPort;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.auth.domain.event.LoginSucceeded;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证应用服务
 *
 * <p>编排登录、登出、刷新 Token 用例；令牌与请求上下文细节经 ACL 端口隔离。
 */
@Slf4j
@Service
public class AuthApplicationService {

  private static final String TOKEN_VERSION_KEY_PREFIX = "satoken:login:token-version:";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final DomainEventPublisher domainEventPublisher;
  private final RedisTemplate<String, Object> redisTemplate;
  private final TokenSessionPort tokenSessionPort;
  private final ClientRequestInfoPort clientRequestInfoPort;

  public AuthApplicationService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      DomainEventPublisher domainEventPublisher,
      @Nullable RedisTemplate<String, Object> redisTemplate,
      TokenSessionPort tokenSessionPort,
      ClientRequestInfoPort clientRequestInfoPort) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.domainEventPublisher = domainEventPublisher;
    this.redisTemplate = redisTemplate;
    this.tokenSessionPort = tokenSessionPort;
    this.clientRequestInfoPort = clientRequestInfoPort;
  }

  /** 用户登录 */
  public LoginResponse login(LoginCommand command) {
    // 1. 查找用户
    User user =
        userRepository
            .findByUsername(command.username())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 2. 检查用户状态
    if (user.status() == UserStatus.DISABLED) {
      throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
    }
    if (user.status() == UserStatus.LOCKED) {
      throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
    }

    // 3. 验证密码
    if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
      throw new BusinessException(ErrorCode.USER_INVALID_PASSWORD);
    }

    // 4. 登录
    tokenSessionPort.login(user.id().value());

    // 5. 将 tokenVersion 写入 Redis，支持后续校验和强制下线
    if (redisTemplate != null) {
      String redisKey = TOKEN_VERSION_KEY_PREFIX + user.id().value();
      redisTemplate.opsForValue().set(redisKey, user.tokenVersion(), 2, TimeUnit.HOURS);
    }

    // 5.1 发布登录成功事件（审计上下文监听落库）
    domainEventPublisher.publish(
        new LoginSucceeded(
            user.id().value(),
            "PASSWORD",
            null,
            clientRequestInfoPort.clientIp(),
            clientRequestInfoPort.userAgent(),
            LocalDateTime.now()));

    // 6. 获取 JWT Token
    String token = tokenSessionPort.currentToken();

    // 7. 更新最后登录时间和IP
    user.recordLogin(LocalDateTime.now(), clientRequestInfoPort.clientIp());
    userRepository.save(user);

    // 8. 返回登录响应
    UserResponse userResponse = toUserResponse(user);

    return new LoginResponse(token, userResponse);
  }

  /** 用户登出 */
  public void logout() {
    Long userId = tokenSessionPort.currentUserId();
    tokenSessionPort.logout();
    if (redisTemplate != null && userId != null) {
      redisTemplate.delete(TOKEN_VERSION_KEY_PREFIX + userId);
    }
  }

  /** 刷新 Token */
  public LoginResponse refreshToken() {
    // 1. 检查是否已登录
    if (!tokenSessionPort.isLogin()) {
      throw new BusinessException(ErrorCode.AUTH_401);
    }

    // 2. 获取当前用户ID
    Long userId = tokenSessionPort.currentUserId();

    // 3. 获取用户信息
    User user =
        userRepository
            .findById(UserId.of(userId))
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 4. 延长 Token 有效期
    tokenSessionPort.renewTimeout(7200);

    // 5. 获取新的 Token
    String token = tokenSessionPort.currentToken();

    // 6. 返回新的登录响应
    UserResponse userResponse = toUserResponse(user);

    return new LoginResponse(token, userResponse);
  }

  /** 判断用户是否设置了密码 */
  public boolean hasPassword(Long userId) {
    return userRepository
        .findById(UserId.of(userId))
        .map(user -> user.passwordHash() != null && !user.passwordHash().isBlank())
        .orElse(false);
  }

  /** 聚合 → 用户响应（保持登录相关接口的 wire 契约不变） */
  private UserResponse toUserResponse(User user) {
    return new UserResponse(
        user.id().value(),
        user.username(),
        user.nickname(),
        user.phone() == null ? null : user.phone().value(),
        user.email() == null ? null : user.email().value(),
        user.status() == null ? null : user.status().name(),
        user.lastLoginAt(),
        user.createdAt());
  }
}
