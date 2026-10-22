package io.github.lystrosaurus.admin.shared.infrastructure.lock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.annotation.AggregateLock;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("聚合级分布式锁切面测试")
class AggregateLockAspectTest {

  private RedissonClient redissonClient;
  private RLock lock;
  private ProceedingJoinPoint joinPoint;
  private AggregateLockAspect aspect;

  @BeforeEach
  void setUp() throws Throwable {
    redissonClient = mock(RedissonClient.class);
    lock = mock(RLock.class);
    when(redissonClient.getLock("lock:user:update:1")).thenReturn(lock);
    when(lock.tryLock(3L, 10L, TimeUnit.SECONDS)).thenReturn(true);
    when(lock.isHeldByCurrentThread()).thenReturn(true);

    joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature signature = mock(MethodSignature.class);
    Method method = SampleService.class.getMethod("update", Long.class);
    when(joinPoint.getSignature()).thenReturn(signature);
    when(signature.getMethod()).thenReturn(method);
    when(joinPoint.getTarget()).thenReturn(new SampleService());
    when(joinPoint.getArgs()).thenReturn(new Object[] {1L});
    when(joinPoint.proceed()).thenReturn("done");

    aspect = new AggregateLockAspect(Optional.of(redissonClient));
    ReflectionTestUtils.setField(aspect, "enabled", true);
  }

  @Test
  @DisplayName("成功获取锁后执行业务并解锁")
  void should_lock_proceed_and_unlock() throws Throwable {
    assertEquals("done", aspect.lock(joinPoint, aggregateLock()));

    verify(lock).tryLock(3L, 10L, TimeUnit.SECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("获取锁失败时抛出 SYSTEM_429")
  void should_throw_system_429_when_lock_not_acquired() throws Throwable {
    when(lock.tryLock(3L, 10L, TimeUnit.SECONDS)).thenReturn(false);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> aspect.lock(joinPoint, aggregateLock()));

    assertEquals(ErrorCode.SYSTEM_429.getCode(), ex.getCode());
    assertEquals("操作正在处理中，请稍后重试", ex.getMessage());
    verify(joinPoint, never()).proceed();
  }

  @Test
  @DisplayName("开关关闭时直接执行业务，不访问 Redisson")
  void should_skip_lock_when_disabled() throws Throwable {
    ReflectionTestUtils.setField(aspect, "enabled", false);

    assertEquals("done", aspect.lock(joinPoint, aggregateLock()));

    verifyNoInteractions(redissonClient);
    verify(joinPoint).proceed();
  }

  @Test
  @DisplayName("Redisson 异常时 fail-open 执行业务")
  void should_fail_open_when_redisson_unavailable() throws Throwable {
    when(redissonClient.getLock("lock:user:update:1"))
        .thenThrow(new RuntimeException("redis down"));

    assertEquals("done", aspect.lock(joinPoint, aggregateLock()));
  }

  @Test
  @DisplayName("缺少 RedissonClient 时 fail-open 执行业务")
  void should_fail_open_without_redisson_client() {
    AggregateLockAspect noClientAspect = new AggregateLockAspect(Optional.empty());
    ReflectionTestUtils.setField(noClientAspect, "enabled", true);

    assertDoesNotThrow(() -> noClientAspect.lock(joinPoint, aggregateLock()));
  }

  private AggregateLock aggregateLock() throws NoSuchMethodException {
    return SampleService.class
        .getMethod("update", Long.class)
        .getAnnotation(AggregateLock.class);
  }

  /** 承载注解的方法签名样本。 */
  static class SampleService {

    @AggregateLock(prefix = "user:update", key = "#id")
    public String update(Long id) {
      return "done";
    }
  }
}
