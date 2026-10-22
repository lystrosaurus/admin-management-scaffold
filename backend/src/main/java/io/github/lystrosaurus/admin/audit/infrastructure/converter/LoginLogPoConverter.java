package io.github.lystrosaurus.admin.audit.infrastructure.converter;

import io.github.lystrosaurus.admin.audit.domain.model.LoginLog;
import io.github.lystrosaurus.admin.audit.infrastructure.persistence.po.LoginLogPO;
import org.springframework.stereotype.Component;

/** 登录日志领域对象 ↔ 持久化对象转换器。 */
@Component
public class LoginLogPoConverter {

  /** 领域对象转持久化对象 */
  public LoginLogPO toPo(LoginLog log) {
    LoginLogPO po = new LoginLogPO();
    po.setId(log.id());
    po.setUserId(log.userId());
    po.setLoginType(log.loginType());
    po.setProviderCode(log.providerCode());
    po.setIpAddress(log.ipAddress());
    po.setUserAgent(log.userAgent());
    po.setStatus(log.status());
    po.setFailureReason(log.failureReason());
    po.setLoginAt(log.loginAt());
    return po;
  }

  /** 持久化对象转领域对象 */
  public LoginLog toDomain(LoginLogPO po) {
    return LoginLog.restore(
        po.getId(),
        po.getUserId(),
        po.getLoginType(),
        po.getProviderCode(),
        po.getIpAddress(),
        po.getUserAgent(),
        po.getStatus(),
        po.getFailureReason(),
        po.getLoginAt());
  }
}
