package io.github.lystrosaurus.admin.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.auth.service.AuthService;
import io.github.lystrosaurus.admin.auth.vo.LoginVO;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.user.dao.UserDAO;
import io.github.lystrosaurus.admin.system.user.dto.LoginDTO;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 *
 * <p>实现登录、登出、刷新 Token 等认证功能
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserDAO userDAO;
  private final BCryptPasswordEncoder passwordEncoder;

  @Override
  public LoginVO login(LoginDTO dto) {
    // 1. 查找用户
    SysUser user = userDAO.findByUsername(dto.username());
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // 2. 检查用户状态
    if ("DISABLED".equals(user.getStatus())) {
      throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
    }
    if ("LOCKED".equals(user.getStatus())) {
      throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
    }

    // 3. 验证密码
    if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
      throw new BusinessException(ErrorCode.USER_INVALID_PASSWORD);
    }

    // 4. 登录
    StpUtil.login(user.getId());

    // 5. 获取 JWT Token
    String token = StpUtil.getTokenValue();

    // 6. 更新最后登录时间和IP
    user.setLastLoginAt(LocalDateTime.now());
    // 暂时使用默认IP，后续可以从RequestContextHolder获取
    user.setLastLoginIp("127.0.0.1");
    userDAO.update(user);

    // 7. 返回 LoginVO
    UserVO userVO =
        new UserVO(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getPhone(),
            user.getEmail(),
            user.getStatus(),
            user.getLastLoginAt(),
            user.getCreatedAt());

    return new LoginVO(token, userVO);
  }

  @Override
  public void logout() {
    StpUtil.logout();
  }

  @Override
  public LoginVO refreshToken() {
    // 1. 检查是否已登录
    if (!StpUtil.isLogin()) {
      throw new BusinessException(ErrorCode.AUTH_401);
    }

    // 2. 获取当前用户ID
    Long userId = StpUtil.getLoginIdAsLong();

    // 3. 获取用户信息
    SysUser user = userDAO.findById(userId);
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // 4. 延长 Token 有效期
    StpUtil.renewTimeout(7200);

    // 5. 获取新的 Token
    String token = StpUtil.getTokenValue();

    // 6. 返回新的 LoginVO
    UserVO userVO =
        new UserVO(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getPhone(),
            user.getEmail(),
            user.getStatus(),
            user.getLastLoginAt(),
            user.getCreatedAt());

    return new LoginVO(token, userVO);
  }

  @Override
  public boolean hasPassword(Long userId) {
    SysUser user = userDAO.findById(userId);
    return user != null && user.getPasswordHash() != null && !user.getPasswordHash().isBlank();
  }
}
