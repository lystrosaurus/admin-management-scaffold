package io.github.lystrosaurus.admin.shared.infrastructure.lock;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.annotation.AggregateLock;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

/**
 * 聚合级分布式锁切面。
 *
 * <p>在 {@code @Transactional} 外层（{@code @Order(0)}）按聚合键获取 Redisson 锁，获取失败抛出 {@link
 * ErrorCode#SYSTEM_429}。开关关闭、Redisson 不可用或 Redis 异常时 fail-open，直接执行业务逻辑。
 */
@Slf4j
@Aspect
@Component
@Order(0)
public class AggregateLockAspect {

  private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER =
      new DefaultParameterNameDiscoverer();

  private final ExpressionParser expressionParser = new SpelExpressionParser();

  private final Optional<RedissonClient> redissonClient;

  /** 分布式锁开关，默认开启。 */
  @Value("${app.aggregate-lock.enabled:true}")
  private boolean enabled;

  public AggregateLockAspect(Optional<RedissonClient> redissonClient) {
    this.redissonClient = redissonClient;
  }

  @Around("@annotation(aggregateLock)")
  public Object lock(ProceedingJoinPoint joinPoint, AggregateLock aggregateLock) throws Throwable {
    if (!enabled) {
      return joinPoint.proceed();
    }
    RedissonClient client = redissonClient.orElse(null);
    if (client == null) {
      log.warn("RedissonClient unavailable, aggregate lock disabled");
      return joinPoint.proceed();
    }

    RLock lock;
    boolean acquired;
    try {
      lock = client.getLock(buildKey(joinPoint, aggregateLock));
      acquired =
          lock.tryLock(
              aggregateLock.waitSeconds(), aggregateLock.leaseSeconds(), TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("Aggregate lock interrupted, fail-open: {}", e.getMessage());
      return joinPoint.proceed();
    } catch (Exception e) {
      log.warn("Aggregate lock failed, fail-open: {}", e.getMessage());
      return joinPoint.proceed();
    }

    if (!acquired) {
      throw new BusinessException(ErrorCode.SYSTEM_429.getCode(), "操作正在处理中，请稍后重试");
    }

    try {
      return joinPoint.proceed();
    } finally {
      try {
        if (lock.isHeldByCurrentThread()) {
          lock.unlock();
        }
      } catch (Exception e) {
        log.warn("Aggregate unlock failed: {}", e.getMessage());
      }
    }
  }

  /** 锁键：{@code lock:{prefix}:{keyPart}}。 */
  private String buildKey(ProceedingJoinPoint joinPoint, AggregateLock aggregateLock) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    MethodBasedEvaluationContext context =
        new MethodBasedEvaluationContext(
            joinPoint.getTarget(), method, joinPoint.getArgs(), PARAMETER_NAME_DISCOVERER);
    Object value = expressionParser.parseExpression(aggregateLock.key()).getValue(context);
    String keyPart = value == null ? "" : value.toString();
    return "lock:" + aggregateLock.prefix() + ":" + keyPart;
  }
}
