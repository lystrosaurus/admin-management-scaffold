package io.github.lystrosaurus.admin.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 放行注解（安全逃生舱）
 *
 * <p>标注在方法上表示：即使全局默认拒绝（default-deny）开启，该方法也允许已登录用户访问。
 * 仅用于 GET / 自身变更（self-mutation）类端点，例如"当前登录用户的菜单树""个人资料"。
 *
 * <p>优先级：方法级 {@code @PermitAll} 高于类级 {@code @RequiresPermission}，即逃生舱优先生效。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermitAll {

  /** 放行原因，用于审计与代码可读 */
  String reason();
}
