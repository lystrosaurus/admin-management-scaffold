package io.github.lystrosaurus.admin.shared.infrastructure.lock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.annotation.Idempotent;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

@DisplayName("IdempotentAspect 测试")
class IdempotentAspectTest {

  private StringRedisTemplate stringRedisTemplate;
  private ValueOperations<String, String> valueOperations;
  private ObjectMapper objectMapper;
  private IdempotentAspect aspect;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    stringRedisTemplate = mock(StringRedisTemplate.class);
    valueOperations = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    objectMapper = mock(ObjectMapper.class);
    when(objectMapper.writeValueAsString(any())).thenReturn("[\"alice\"]");

    aspect = new IdempotentAspect(stringRedisTemplate, objectMapper);
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
  @DisplayName("首次请求应放行并写入带 TTL 的幂等键")
  void should_allow_first_request() throws Exception {
    when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(10L), eq(TimeUnit.SECONDS)))
        .thenReturn(true);

    assertDoesNotThrow(() -> aspect.checkIdempotent(createJoinPoint(), createIdempotent()));

    ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
    verify(valueOperations)
        .setIfAbsent(keyCaptor.capture(), eq("1"), eq(10L), eq(TimeUnit.SECONDS));
    String key = keyCaptor.getValue();
    org.junit.jupiter.api.Assertions.assertTrue(
        key.startsWith("idempotent:user:create:"), "幂等键前缀不正确: " + key);
    assertEquals(64, key.substring("idempotent:user:create:".length()).length(), "应为 SHA-256 十六进制");
  }

  @Test
  @DisplayName("重复请求应抛出 SYSTEM_409")
  void should_throw_system_409_when_duplicate() throws Exception {
    when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(10L), eq(TimeUnit.SECONDS)))
        .thenReturn(false);

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> aspect.checkIdempotent(createJoinPoint(), createIdempotent()));

    assertEquals(ErrorCode.SYSTEM_409.getCode(), ex.getCode());
    assertEquals("重复请求，请稍后再试", ex.getMessage());
  }

  @Test
  @DisplayName("存在 X-Idempotency-Key 时应优先使用请求头")
  void should_prefer_idempotency_header() throws Exception {
    when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(10L), eq(TimeUnit.SECONDS)))
        .thenReturn(true);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Idempotency-Key", "client-key-001");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    assertDoesNotThrow(() -> aspect.checkIdempotent(createJoinPoint(), createIdempotent()));

    verify(valueOperations)
        .setIfAbsent(
            eq("idempotent:user:create:client-key-001"), eq("1"), eq(10L), eq(TimeUnit.SECONDS));
    verify(objectMapper, never()).writeValueAsString(any());
  }

  @Test
  @DisplayName("Redis 异常时应 fail-open 放行")
  void should_fail_open_when_redis_unavailable() throws Exception {
    when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(10L), eq(TimeUnit.SECONDS)))
        .thenThrow(new RuntimeException("redis down"));

    assertDoesNotThrow(() -> aspect.checkIdempotent(createJoinPoint(), createIdempotent()));
  }

  @Test
  @DisplayName("关闭开关时不应访问 Redis")
  void should_skip_redis_when_disabled() throws Exception {
    ReflectionTestUtils.setField(aspect, "enabled", false);

    assertDoesNotThrow(() -> aspect.checkIdempotent(createJoinPoint(), createIdempotent()));

    verifyNoInteractions(stringRedisTemplate);
  }

  private JoinPoint createJoinPoint() throws NoSuchMethodException {
    JoinPoint joinPoint = mock(JoinPoint.class);
    MethodSignature signature = mock(MethodSignature.class);
    Method method = SampleController.class.getMethod("create", String.class);
    when(joinPoint.getSignature()).thenReturn(signature);
    when(signature.getMethod()).thenReturn(method);
    when(joinPoint.getArgs()).thenReturn(new Object[] {"alice"});
    when(joinPoint.getTarget()).thenReturn(new SampleController());
    return joinPoint;
  }

  private Idempotent createIdempotent() throws NoSuchMethodException {
    return SampleController.class
        .getMethod("create", String.class)
        .getAnnotation(Idempotent.class);
  }

  /** 承载注解的方法签名样本。 */
  static class SampleController {

    @Idempotent(prefix = "user:create", ttlSeconds = 10)
    public void create(String username) {
      // 仅用于反射获取方法签名与注解
    }
  }
}
