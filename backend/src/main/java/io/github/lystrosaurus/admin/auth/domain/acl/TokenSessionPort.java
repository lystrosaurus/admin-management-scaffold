package io.github.lystrosaurus.admin.auth.domain.acl;

import java.util.List;

/** 令牌会话端口：隔离应用层对具体令牌框架的依赖。 */
public interface TokenSessionPort {

  /** 获取当前登录用户ID（未登录时抛出认证异常） */
  Long currentUserId();

  /** 是否已登录 */
  boolean isLogin();

  /** 以指定用户身份登录 */
  void login(Long userId);

  /** 登出当前会话 */
  void logout();

  /** 获取当前会话令牌 */
  String currentToken();

  /** 延长当前令牌有效期（秒） */
  void renewTimeout(long timeout);

  /** 获取当前用户权限码列表 */
  List<String> currentPermissions();
}
