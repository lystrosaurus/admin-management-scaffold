package io.github.lystrosaurus.admin.auth.config;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Token Version 校验拦截器
 *
 * <p>在 {@link cn.dev33.satoken.interceptor.SaInterceptor} 之后执行，校验当前会话的 tokenVersion 是否与数据库一致。不一致说明
 * Token 已被吊销（如改密/禁用），拒绝访问。
 */
@Slf4j
@Component
@ConditionalOnBean(RedisTemplate.class)
@RequiredArgsConstructor
public class TokenVersionInterceptor implements HandlerInterceptor {

  private static final String TOKEN_VERSION_KEY_PREFIX = "satoken:login:token-version:";

  private final RedisTemplate<String, Object> redisTemplate;
  private final UserDAO userDAO;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    Long userId = StpUtil.getLoginIdAsLong();

    // 1. 从 Redis 获取会话中存储的 tokenVersion
    String redisKey = TOKEN_VERSION_KEY_PREFIX + userId;
    Object storedVersion = redisTemplate.opsForValue().get(redisKey);
    if (storedVersion == null) {
      // 会话中无 tokenVersion，说明是旧 Token（登录时未写入新版 Sa-Token Session），放行
      return true;
    }

    // 2. 从数据库获取当前 tokenVersion
    SysUser user = userDAO.findById(userId);
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // 3. 比较，不一致则 Token 已失效
    Integer stored =
        storedVersion instanceof Integer
            ? (Integer) storedVersion
            : Integer.parseInt(storedVersion.toString());
    if (!stored.equals(user.getTokenVersion())) {
      log.warn(
          "Token version mismatch for userId={}, stored={}, actual={}",
          userId,
          stored,
          user.getTokenVersion());
      StpUtil.logout();
      throw new BusinessException(ErrorCode.AUTH_401);
    }

    return true;
  }
}
