package io.github.lystrosaurus.admin.audit.application.listener;

import io.github.lystrosaurus.admin.audit.application.LoginLogApplicationService;
import io.github.lystrosaurus.admin.auth.domain.event.LoginSucceeded;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** 登录成功事件的审计监听者：事务提交后落库登录日志（无事务时也执行）。 */
@Component
@RequiredArgsConstructor
public class LoginAuditListener {

  private final LoginLogApplicationService loginLogApplicationService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void onLoginSucceeded(LoginSucceeded event) {
    loginLogApplicationService.recordLogin(event);
  }
}
