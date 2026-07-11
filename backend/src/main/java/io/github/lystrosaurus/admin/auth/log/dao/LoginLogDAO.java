package io.github.lystrosaurus.admin.auth.log.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import java.time.LocalDateTime;
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

  /**
   * 条件分页查询登录日志
   *
   * @param userId 用户ID(可选)
   * @param ipAddress IP地址(可选)
   * @param status 登录状态(可选)
   * @param start 起始时间(可选)
   * @param end 结束时间(可选)
   * @param page 页码
   * @param size 每页大小
   * @return 登录日志分页
   */
  Page<LoginLog> findPage(
      Long userId,
      String ipAddress,
      String status,
      LocalDateTime start,
      LocalDateTime end,
      int page,
      int size);
}
