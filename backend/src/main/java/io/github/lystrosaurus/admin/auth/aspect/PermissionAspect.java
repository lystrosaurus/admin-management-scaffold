package io.github.lystrosaurus.admin.auth.aspect;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.annotation.RequiresRole;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 权限切面
 *
 * <p>拦截权限注解，验证用户是否具有所需权限
 */
@Slf4j
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
    try {
      if (!StpUtil.hasPermission(requiresPermission.value())) {
        throw new BusinessException(ErrorCode.PERMISSION_DENIED);
      }
    } catch (SaTokenException e) {
      log.debug("Permission check failed: {}", e.getMessage());
      throw new BusinessException(ErrorCode.SYSTEM_401);
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
    try {
      if (!StpUtil.hasRole(requiresRole.value())) {
        throw new BusinessException(ErrorCode.AUTH_403);
      }
    } catch (SaTokenException e) {
      log.debug("Role check failed: {}", e.getMessage());
      throw new BusinessException(ErrorCode.SYSTEM_401);
    }
  }
}
