package io.github.lystrosaurus.admin.shared.infrastructure.lock;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.annotation.RateLimit;
import io.github.lystrosaurus.admin.web.util.ClientIpUtil;
import java.lang.reflect.Method;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 接口限流切面。
 *
 * <p>对 {@link RateLimit} 标注的方法执行 Redis 原子计数（INCR + 首次 EXPIRE），超过阈值时抛出
 * {@link ErrorCode#SYSTEM_429}。Redis 不可用时 fail-open，仅记录告警并放行，避免限流组件本身导致接口 500。
 */
@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitAspect {

  /** 原子计数脚本：首次自增时设置过期时间。 */
  private static final RedisScript<Long> INCR_WITH_EXPIRE_SCRIPT =
      new DefaultRedisScript<>(
          "local current = redis.call('incr', KEYS[1]) "
              + "if tonumber(current) == 1 then redis.call('expire', KEYS[1], ARGV[1]) end "
              + "return current",
          Long.class);

  private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER =
      new DefaultParameterNameDiscoverer();

  private final ExpressionParser expressionParser = new SpelExpressionParser();

  private final StringRedisTemplate stringRedisTemplate;

  /** 限流开关，默认开启。 */
  @Value("${app.rate-limit.enabled:true}")
  private boolean enabled;

  @Before("@annotation(rateLimit)")
  public void checkRateLimit(JoinPoint joinPoint, RateLimit rateLimit) {
    if (!enabled) {
      return;
    }

    Long current;
    try {
      String redisKey = buildKey(joinPoint, rateLimit);
      current =
          stringRedisTemplate.execute(
              INCR_WITH_EXPIRE_SCRIPT,
              List.of(redisKey),
              String.valueOf(rateLimit.windowSeconds()));
    } catch (Exception e) {
      log.warn("Rate limit check failed, fail-open: {}", e.getMessage());
      return;
    }

    if (current != null && current > rateLimit.limit()) {
      throw new BusinessException(ErrorCode.SYSTEM_429.getCode(), rateLimit.message());
    }
  }

  /** 限流键：{@code ratelimit:{prefix}:{keyPart}:{clientIp}}。 */
  private String buildKey(JoinPoint joinPoint, RateLimit rateLimit) {
    String keyPart = rateLimit.key().isBlank() ? "" : evaluateKey(joinPoint, rateLimit.key());
    String key = "ratelimit:" + rateLimit.prefix() + ":" + keyPart;
    if (rateLimit.byClientIp()) {
      key = key + ":" + clientIp();
    }
    return key;
  }

  private String evaluateKey(JoinPoint joinPoint, String expression) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    MethodBasedEvaluationContext context =
        new MethodBasedEvaluationContext(
            joinPoint.getTarget(), method, joinPoint.getArgs(), PARAMETER_NAME_DISCOVERER);
    Object value = expressionParser.parseExpression(expression).getValue(context);
    return value == null ? "" : value.toString();
  }

  private String clientIp() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return "unknown";
    }
    return ClientIpUtil.getClientIp(attributes.getRequest());
  }
}
