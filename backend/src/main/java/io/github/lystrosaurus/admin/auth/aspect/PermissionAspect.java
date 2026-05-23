package io.github.lystrosaurus.admin.auth.aspect;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.annotation.RequiresRole;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 权限切面
 *
 * <p>拦截权限注解，验证用户是否具有所需权限
 */
@Aspect
@Component
public class PermissionAspect {

  /**
   * 拦截 @RequiresPermission 注解
   *
   * @param joinPoint 连接点
   * @param requiresPermission 权限注解
   */
  @Before("@annotation(requiresPermission)")
  public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
    String permission = requiresPermission.value();
    if (!hasPermission(permission)) {
      throw new BusinessException(ErrorCode.PERMISSION_DENIED);
    }
  }

  /**
   * 拦截 @RequiresRole 注解
   *
   * @param joinPoint 连接点
   * @param requiresRole 角色注解
   */
  @Before("@annotation(requiresRole)")
  public void checkRole(JoinPoint joinPoint, RequiresRole requiresRole) {
    String role = requiresRole.value();
    if (!hasRole(role)) {
      throw new BusinessException(ErrorCode.AUTH_403);
    }
  }

  /**
   * 检查用户是否具有指定权限
   *
   * @param permission 权限编码
   * @return 是否具有权限
   */
  private boolean hasPermission(String permission) {
    // 从 Sa-Token 会话中获取用户权限列表
    // 暂时返回 true，后续需要实现权限接口
    return true;
  }

  /**
   * 检查用户是否具有指定角色
   *
   * @param role 角色编码
   * @return 是否具有角色
   */
  private boolean hasRole(String role) {
    // 从 Sa-Token 会话中获取用户角色列表
    // 暂时返回 true，后续需要实现角色接口
    return true;
  }
}
