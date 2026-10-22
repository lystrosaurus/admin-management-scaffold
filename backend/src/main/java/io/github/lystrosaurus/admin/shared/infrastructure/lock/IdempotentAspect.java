package io.github.lystrosaurus.admin.shared.infrastructure.lock;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.shared.annotation.Idempotent;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

/**
 * 写接口幂等切面。
 *
 * <p>对 {@link Idempotent} 标注的方法执行 Redis SETNX：幂等键优先取请求头 {@code X-Idempotency-Key}，
 * 否则取 {@code SHA-256(方法签名 + 参数 JSON)}。键已存在时抛出 {@link ErrorCode#SYSTEM_409}。
 * Redis 不可用时 fail-open，仅记录告警并放行。
 */
@Slf4j
@Aspect
@Component
@Order(2)
@RequiredArgsConstructor
public class IdempotentAspect {

  private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";

  private final StringRedisTemplate stringRedisTemplate;

  private final ObjectMapper objectMapper;

  /** 幂等开关，默认开启。 */
  @Value("${app.idempotent.enabled:true}")
  private boolean enabled;

  @Before("@annotation(idempotent)")
  public void checkIdempotent(JoinPoint joinPoint, Idempotent idempotent) {
    if (!enabled) {
      return;
    }

    Boolean acquired;
    try {
      String key = buildKey(joinPoint, idempotent);
      acquired =
          stringRedisTemplate
              .opsForValue()
              .setIfAbsent(key, "1", idempotent.ttlSeconds(), TimeUnit.SECONDS);
    } catch (Exception e) {
      log.warn("Idempotency check failed, fail-open: {}", e.getMessage());
      return;
    }

    if (Boolean.FALSE.equals(acquired)) {
      throw new BusinessException(ErrorCode.SYSTEM_409.getCode(), idempotent.message());
    }
  }

  private String buildKey(JoinPoint joinPoint, Idempotent idempotent) {
    String headerKey = currentHeader();
    String rawKey =
        headerKey != null && !headerKey.isBlank()
            ? headerKey
            : sha256(methodSignature(joinPoint) + serializeArgs(joinPoint));
    return "idempotent:" + idempotent.prefix() + ":" + rawKey;
  }

  private String methodSignature(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    return method.getDeclaringClass().getName() + "#" + method.getName();
  }

  private String serializeArgs(JoinPoint joinPoint) {
    return objectMapper.writeValueAsString(joinPoint.getArgs());
  }

  private String currentHeader() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return null;
    }
    return attributes.getRequest().getHeader(IDEMPOTENCY_HEADER);
  }

  private String sha256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }
}
