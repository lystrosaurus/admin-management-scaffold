package io.github.lystrosaurus.admin.web.datascope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 数据权限注解 — 标注在 Service/DAO 方法上，由 DataScopeHelper 拦截并拼接数据过滤条件 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

  /** 部门ID列名（对应 SQL 别名.字段，如 d.org_id） */
  String orgIdColumn() default "org_id";

  /** 创建人ID列名（用于 SELF 范围过滤） */
  String createdByColumn() default "created_by";

  /** 部门过滤前缀（用于 ORG_TREE / ORG_ONLY / CUSTOM） */
  String orgFilter() default "";

  /** 本人过滤前缀（用于 SELF） */
  String selfFilter() default "";
}
