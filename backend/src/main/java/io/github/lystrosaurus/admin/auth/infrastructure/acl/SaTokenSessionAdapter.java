package io.github.lystrosaurus.admin.auth.infrastructure.acl;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Component;

/** 令牌会话端口的 Sa-Token 实现：框架细节收敛在此处。 */
@Component
public class SaTokenSessionAdapter implements TokenSessionPort {

  @Override
  public Long currentUserId() {
    if (!StpUtil.isLogin()) {
      throw new BusinessException(ErrorCode.AUTH_401);
    }
    return StpUtil.getLoginIdAsLong();
  }

  @Override
  public boolean isLogin() {
    return StpUtil.isLogin();
  }

  @Override
  public void login(Long userId) {
    StpUtil.login(userId);
  }

  @Override
  public void logout() {
    StpUtil.logout();
  }

  @Override
  public String currentToken() {
    return StpUtil.getTokenValue();
  }

  @Override
  public void renewTimeout(long timeout) {
    StpUtil.renewTimeout(timeout);
  }

  @Override
  public List<String> currentPermissions() {
    return StpUtil.getPermissionList();
  }
}
