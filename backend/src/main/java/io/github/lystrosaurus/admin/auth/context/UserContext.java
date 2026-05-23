package io.github.lystrosaurus.admin.auth.context;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.util.List;

/**
 * 用户上下文工具类
 *
 * <p>提供获取当前登录用户信息、权限、角色等方法
 */
public final class UserContext {

  private UserContext() {
    // 私有构造函数，防止实例化
  }

  /**
   * 获取当前登录用户ID
   *
   * @return 用户ID
   * @throws BusinessException 如果未登录
   */
  public static Long getCurrentUserId() {
    if (!StpUtil.isLogin()) {
      throw new BusinessException(ErrorCode.AUTH_401);
    }
    return StpUtil.getLoginIdAsLong();
  }

  /**
   * 获取当前登录用户信息
   *
   * @return 用户信息，如果未登录返回 null
   */
  public static Object getCurrentUser() {
    if (!StpUtil.isLogin()) {
      return null;
    }
    return StpUtil.getSession().get("currentUser");
  }

  /**
   * 检查是否有某个权限
   *
   * @param permission 权限编码
   * @return 是否具有权限
   */
  public static boolean hasPermission(String permission) {
    if (!StpUtil.isLogin()) {
      return false;
    }
    // 从 Sa-Token 会话中获取用户权限列表
    // 暂时返回 true，后续需要实现权限接口
    return true;
  }

  /**
   * 检查是否有某个角色
   *
   * @param role 角色编码
   * @return 是否具有角色
   */
  public static boolean hasRole(String role) {
    if (!StpUtil.isLogin()) {
      return false;
    }
    // 从 Sa-Token 会话中获取用户角色列表
    // 暂时返回 true，后续需要实现角色接口
    return true;
  }

  /**
   * 获取当前用户权限列表
   *
   * @return 权限列表，如果未登录返回空列表
   */
  public static List<String> getPermissions() {
    if (!StpUtil.isLogin()) {
      return List.of();
    }
    // 从 Sa-Token 会话中获取用户权限列表
    // 暂时返回空列表，后续需要实现权限接口
    return List.of();
  }

  /**
   * 获取当前用户角色列表
   *
   * @return 角色列表，如果未登录返回空列表
   */
  public static List<String> getRoles() {
    if (!StpUtil.isLogin()) {
      return List.of();
    }
    // 从 Sa-Token 会话中获取用户角色列表
    // 暂时返回空列表，后续需要实现角色接口
    return List.of();
  }

  /**
   * 检查是否已登录
   *
   * @return 是否已登录
   */
  public static boolean isLoggedIn() {
    return StpUtil.isLogin();
  }

  /**
   * 获取当前用户 Token
   *
   * @return Token，如果未登录返回 null
   */
  public static String getToken() {
    if (!StpUtil.isLogin()) {
      return null;
    }
    return StpUtil.getTokenValue();
  }
}
