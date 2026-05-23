package io.github.lystrosaurus.admin.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色注解
 *
 * <p>用于标注需要特定角色才能访问的方法或类
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
  /**
   * 角色编码
   *
   * @return 角色编码
   */
  String value();
}
