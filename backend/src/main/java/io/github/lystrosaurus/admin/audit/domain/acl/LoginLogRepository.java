package io.github.lystrosaurus.admin.audit.domain.acl;

import io.github.lystrosaurus.admin.audit.domain.model.LoginLog;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import java.time.LocalDateTime;
import java.util.List;

/** 登录日志仓储端口。 */
public interface LoginLogRepository {

  /** 保存登录日志 */
  void save(LoginLog log);

  /** 查询用户最近的登录记录 */
  List<LoginLog> listByUserId(Long userId, int limit);

  /** 条件分页查询登录日志 */
  Page<LoginLog> findPage(
      Long userId,
      String ipAddress,
      String status,
      LocalDateTime start,
      LocalDateTime end,
      int page,
      int size);
}
