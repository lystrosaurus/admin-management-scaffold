package io.github.lystrosaurus.admin.shared.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 接口限流注解（基于 Redis 滑动窗口计数，由 {@code RateLimitAspect} 实现）。 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

  /** 限流键前缀，用于区分不同接口。 */
  String prefix();

  /** SpEL 表达式，可引用方法参数（如 {@code #request.username()}）；为空表示仅按客户端 IP 限流。 */
  String key() default "";

  /** 窗口内允许的最大请求数。 */
  int limit() default 5;

  /** 计数窗口长度（秒）。 */
  int windowSeconds() default 60;

  /** 是否将客户端 IP 拼入限流键。 */
  boolean byClientIp() default true;

  /** 超限时的错误消息。 */
  String message() default "请求过于频繁，请稍后再试";
}
