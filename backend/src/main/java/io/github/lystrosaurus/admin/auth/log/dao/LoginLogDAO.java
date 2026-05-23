package io.github.lystrosaurus.admin.auth.log.dao;

import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import java.util.List;

/** 登录日志数据访问对象接口 */
public interface LoginLogDAO {

  /**
   * 保存登录日志
   *
   * @param log 登录日志实体
   */
  void save(LoginLog log);

  /**
   * 查询用户最近的登录记录
   *
   * @param userId 用户ID
   * @param limit 限制数量
   * @return 登录日志列表
   */
  List<LoginLog> listByUserId(Long userId, int limit);
}
