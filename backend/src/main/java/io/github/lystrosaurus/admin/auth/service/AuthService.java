package io.github.lystrosaurus.admin.auth.service;

import io.github.lystrosaurus.admin.auth.vo.LoginVO;
import io.github.lystrosaurus.admin.system.user.dto.LoginDTO;

/**
 * 认证服务接口
 *
 * <p>定义认证相关的业务方法
 */
public interface AuthService {

  /**
   * 用户登录
   *
   * @param dto 登录参数
   * @return 登录结果
   */
  LoginVO login(LoginDTO dto);

  /** 用户登出 */
  void logout();

  /**
   * 刷新 Token
   *
   * @return 新的 Token
   */
  LoginVO refreshToken();

  /**
   * 判断用户是否设置了密码
   *
   * @param userId 用户ID
   * @return true 表示已设置密码
   */
  boolean hasPassword(Long userId);
}
