package io.github.lystrosaurus.admin.auth.log.service;

import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import java.util.List;

/** 登录日志服务接口 */
public interface LoginLogService {

  /**
   * 记录登录日志
   *
   * @param userId 用户ID
   * @param loginType 登录类型
   * @param providerCode OAuth提供方编码
   * @param ipAddress IP地址
   * @param userAgent User-Agent
   * @param success 是否成功
   * @param failureReason 失败原因
   */
  void recordLogin(
      Long userId,
      String loginType,
      String providerCode,
      String ipAddress,
      String userAgent,
      boolean success,
      String failureReason);

  /**
   * 查询用户最近的登录记录
   *
   * @param userId 用户ID
   * @param limit 限制数量
   * @return 登录日志VO列表
   */
  List<LoginLogVO> getRecentLogins(Long userId, int limit);
}
