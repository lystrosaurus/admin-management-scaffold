package io.github.lystrosaurus.admin.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.auth.dto.LoginDTO;
import io.github.lystrosaurus.admin.auth.vo.LoginVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.adapter.response.UserResponse;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import io.github.lystrosaurus.admin.auth.log.service.LoginLogService;
import io.github.lystrosaurus.admin.web.util.ClientIpUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 认证服务实现
 *
 * <p>实现登录、登出、刷新 Token 等认证功能
 */
@Slf4j
@Service
public class AuthService {

  private static final String TOKEN_VERSION_KEY_PREFIX = "satoken:login:token-version:";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final LoginLogService loginLogService;
  private final RedisTemplate<String, Object> redisTemplate;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      LoginLogService loginLogService,
      @Nullable RedisTemplate<String, Object> redisTemplate) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.loginLogService = loginLogService;
    this.redisTemplate = redisTemplate;
  }

  public LoginVO login(LoginDTO dto) {
    // 1. 查找用户
    User user =
        userRepository
            .findByUsername(dto.username())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 2. 检查用户状态
    if (user.status() == UserStatus.DISABLED) {
      throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
    }
    if (user.status() == UserStatus.LOCKED) {
      throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
    }

    // 3. 验证密码
    if (!passwordEncoder.matches(dto.password(), user.passwordHash())) {
      throw new BusinessException(ErrorCode.USER_INVALID_PASSWORD);
    }

    // 4. 登录
    StpUtil.login(user.id().value());

    // 5. 将 tokenVersion 写入 Redis，支持后续校验和强制下线
    if (redisTemplate != null) {
      String redisKey = TOKEN_VERSION_KEY_PREFIX + user.id().value();
      redisTemplate.opsForValue().set(redisKey, user.tokenVersion(), 2, TimeUnit.HOURS);
    }

    // 5.1 记录登录日志（审计）
    loginLogService.recordLogin(
        user.id().value(), "PASSWORD", null, getClientIp(), getClientUserAgent(), true, null);

    // 6. 获取 JWT Token
    String token = StpUtil.getTokenValue();

    // 7. 更新最后登录时间和IP
    user.recordLogin(LocalDateTime.now(), getClientIp());
    userRepository.save(user);

    // 8. 返回 LoginVO
    UserResponse userResponse = toUserResponse(user);

    return new LoginVO(token, userResponse);
  }

  public void logout() {
    Long userId = StpUtil.getLoginIdAsLong();
    StpUtil.logout();
    if (redisTemplate != null && userId != null) {
      redisTemplate.delete(TOKEN_VERSION_KEY_PREFIX + userId);
    }
  }

  public LoginVO refreshToken() {
    // 1. 检查是否已登录
    if (!StpUtil.isLogin()) {
      throw new BusinessException(ErrorCode.AUTH_401);
    }

    // 2. 获取当前用户ID
    Long userId = StpUtil.getLoginIdAsLong();

    // 3. 获取用户信息
    User user =
        userRepository
            .findById(UserId.of(userId))
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 4. 延长 Token 有效期
    StpUtil.renewTimeout(7200);

    // 5. 获取新的 Token
    String token = StpUtil.getTokenValue();

    // 6. 返回新的 LoginVO
    UserResponse userResponse = toUserResponse(user);

    return new LoginVO(token, userResponse);
  }

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

  /** 获取当前请求的客户端 User-Agent */
  private String getClientUserAgent() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) {
      return "unknown";
    }
    return attrs.getRequest().getHeader("User-Agent");
  }

  /** 获取当前请求的客户端真实 IP */
  private String getClientIp() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) {
      return "unknown";
    }
    HttpServletRequest request = attrs.getRequest();
    return ClientIpUtil.getClientIp(request);
  }
}
