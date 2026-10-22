package io.github.lystrosaurus.admin.shared.infrastructure.lock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.lystrosaurus.admin.auth.adapter.request.LoginRequest;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.annotation.RateLimit;
import java.lang.reflect.Method;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@DisplayName("RateLimitAspect 测试")
class RateLimitAspectTest {

  private StringRedisTemplate stringRedisTemplate;
  private RateLimitAspect aspect;

  @BeforeEach
  void setUp() {
    stringRedisTemplate = mock(StringRedisTemplate.class);
    aspect = new RateLimitAspect(stringRedisTemplate);
    ReflectionTestUtils.setField(aspect, "enabled", true);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRemoteAddr("127.0.0.1");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("未超限时应放行并按 SpEL + IP 拼接限流键")
  void should_allow_request_when_under_limit() throws Exception {
    when(stringRedisTemplate.execute(any(RedisScript.class), anyList(), any(Object[].class)))
        .thenReturn(1L);

    JoinPoint joinPoint = loginJoinPoint("alice");

    assertDoesNotThrow(() -> aspect.checkRateLimit(joinPoint, loginRateLimit()));

    verify(stringRedisTemplate)
        .execute(
            any(RedisScript.class),
            eq(List.of("ratelimit:auth:login:alice:127.0.0.1")),
            eq("60"));
  }

  @Test
  @DisplayName("超过阈值时应抛出 SYSTEM_429")
  void should_throw_system_429_when_over_limit() throws Exception {
    when(stringRedisTemplate.execute(any(RedisScript.class), anyList(), any(Object[].class)))
        .thenReturn(6L);

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> aspect.checkRateLimit(loginJoinPoint("alice"), loginRateLimit()));

    assertEquals(ErrorCode.SYSTEM_429.getCode(), ex.getCode());
    assertEquals("请求过于频繁，请稍后再试", ex.getMessage());
  }

  @Test
  @DisplayName("Redis 异常时应 fail-open 放行")
  void should_fail_open_when_redis_unavailable() throws Exception {
    when(stringRedisTemplate.execute(any(RedisScript.class), anyList(), any(Object[].class)))
        .thenThrow(new RuntimeException("redis down"));

    assertDoesNotThrow(() -> aspect.checkRateLimit(loginJoinPoint("alice"), loginRateLimit()));
  }

  @Test
  @DisplayName("关闭开关时不应访问 Redis")
  void should_skip_redis_when_disabled() throws Exception {
    ReflectionTestUtils.setField(aspect, "enabled", false);

    assertDoesNotThrow(() -> aspect.checkRateLimit(loginJoinPoint("alice"), loginRateLimit()));

    org.mockito.Mockito.verifyNoInteractions(stringRedisTemplate);
  }

  private JoinPoint loginJoinPoint(String username) throws NoSuchMethodException {
    JoinPoint joinPoint = mock(JoinPoint.class);
    MethodSignature signature = mock(MethodSignature.class);
    Method method = SampleController.class.getMethod("login", LoginRequest.class);
    when(joinPoint.getSignature()).thenReturn(signature);
    when(signature.getMethod()).thenReturn(method);
    when(joinPoint.getArgs()).thenReturn(new Object[] {new LoginRequest(username, "password123")});
    when(joinPoint.getTarget()).thenReturn(new SampleController());
    return joinPoint;
  }

  private RateLimit loginRateLimit() throws NoSuchMethodException {
    return SampleController.class
        .getMethod("login", LoginRequest.class)
        .getAnnotation(RateLimit.class);
  }

  /** 承载注解的方法签名样本。 */
  static class SampleController {

    @RateLimit(
        prefix = "auth:login",
        key = "#request.username()",
        limit = 5,
        windowSeconds = 60)
    public void login(LoginRequest request) {
      // 仅用于反射获取方法签名与注解
    }
  }
}
