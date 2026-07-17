package io.github.lystrosaurus.admin.auth.aspect;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.auth.annotation.PermitAll;
import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.annotation.RequiresRole;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 权限切面
 *
 * <p>拦截权限注解，验证用户是否具有所需权限。核心能力：
 * <ul>
 *   <li>方法级 / 类级 {@link RequiresPermission}（方法级优先于类级）。</li>
 *   <li>{@link PermitAll} 逃生舱：即使全局默认拒绝开启，也放行（仅限 GET / 自身变更）。</li>
 *   <li>全局默认拒绝（default-deny）：未声明任何授权注解的 {@code /api/app/**} 端点，
 *       在 {@code auth.default-deny.enabled=true} 时一律拒绝（fail-closed）。</li>
 * </ul>
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

  /** 全局默认拒绝开关，默认关闭（保持既有行为：未标注端点放行）。 */
  @Value("${auth.default-deny.enabled:false}")
  private boolean defaultDenyEnabled;

  /**
   * 4 态默认拒绝决策（纯函数，无 Sa-Token 调用，便于单测）。
   *
   * <p>优先级：方法级 {@link PermitAll}（逃生舱） &gt; {@link RequiresPermission}（方法/类）
   * &gt; {@link RequiresRole} &gt; 默认拒绝 / 放行。
   *
   * @param methodPermitAll      方法是否声明 {@link PermitAll}
   * @param requiresPermission   方法或类是否声明 {@link RequiresPermission}
   * @param requiresRole         方法是否声明 {@link RequiresRole}
   * @param defaultDenyEnabled   全局默认拒绝开关
   * @return 决策结果
   */
  static AuthDecision decidePolicy(
      boolean methodPermitAll,
      boolean requiresPermission,
      boolean requiresRole,
      boolean defaultDenyEnabled) {
    if (methodPermitAll) {
      return AuthDecision.ALLOW;
    }
    if (requiresPermission) {
      return AuthDecision.CHECK_PERMISSION;
    }
    if (requiresRole) {
      return AuthDecision.CHECK_ROLE;
    }
    return defaultDenyEnabled ? AuthDecision.DENY : AuthDecision.ALLOW;
  }

  /**
   * 拦截 @RequiresPermission（方法级或类级），排除方法级 @PermitAll（逃生舱优先）。
   */
  @Before(
      "(@annotation(io.github.lystrosaurus.admin.auth.annotation.RequiresPermission) "
          + "|| @within(io.github.lystrosaurus.admin.auth.annotation.RequiresPermission)) "
          + "&& !@annotation(io.github.lystrosaurus.admin.auth.annotation.PermitAll)")
  public void checkPermission(JoinPoint joinPoint) {
    RequiresPermission annotation = resolveRequiresPermission(joinPoint);
    if (annotation == null) {
      return;
    }
    String code = annotation.value();
    try {
      if (!StpUtil.hasPermission(code)) {
        throw new BusinessException(ErrorCode.PERMISSION_DENIED);
      }
    } catch (SaTokenException e) {
      log.debug("Permission check failed: {}", e.getMessage());
      throw new BusinessException(ErrorCode.SYSTEM_401);
    }
  }

  /**
   * 全局默认拒绝：作用于所有 @RestController 的 public 方法，排除已声明
   * @RequiresPermission / @PermitAll / @RequiresRole 的方法，仅对 {@code /api/app/**} 生效。
   */
  @Before(
      "within(@org.springframework.web.bind.annotation.RestController *) "
          + "&& execution(public * *(..)) "
          + "&& !@annotation(io.github.lystrosaurus.admin.auth.annotation.RequiresPermission) "
          + "&& !@within(io.github.lystrosaurus.admin.auth.annotation.RequiresPermission) "
          + "&& !@annotation(io.github.lystrosaurus.admin.auth.annotation.PermitAll) "
          + "&& !@annotation(io.github.lystrosaurus.admin.auth.annotation.RequiresRole)")
  public void defaultDeny(JoinPoint joinPoint) {
    if (!isAppPath()) {
      return;
    }
    if (decidePolicy(false, false, false, defaultDenyEnabled) == AuthDecision.DENY) {
      throw new BusinessException(ErrorCode.PERMISSION_DENIED);
    }
  }

  /**
   * 拦截 @RequiresRole 注解
   */
  @Before("@annotation(requiresRole)")
  public void checkRole(JoinPoint joinPoint, RequiresRole requiresRole) {
    try {
      if (!StpUtil.hasRole(requiresRole.value())) {
        throw new BusinessException(ErrorCode.AUTH_403);
      }
    } catch (SaTokenException e) {
      log.debug("Role check failed: {}", e.getMessage());
      throw new BusinessException(ErrorCode.SYSTEM_401);
    }
  }

  private RequiresPermission resolveRequiresPermission(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    RequiresPermission methodAnn = AnnotationUtils.findAnnotation(method, RequiresPermission.class);
    if (methodAnn != null) {
      return methodAnn;
    }
    return AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequiresPermission.class);
  }

  private boolean isAppPath() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return false;
    }
    String uri = attributes.getRequest().getRequestURI();
    return uri != null && uri.startsWith("/api/app/");
  }

  /** 4 态决策枚举 */
  enum AuthDecision {
    ALLOW,
    CHECK_PERMISSION,
    CHECK_ROLE,
    DENY
  }
}
