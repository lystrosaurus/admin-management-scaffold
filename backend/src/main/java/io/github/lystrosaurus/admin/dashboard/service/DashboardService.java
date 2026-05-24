package io.github.lystrosaurus.admin.dashboard.service;

import io.github.lystrosaurus.admin.dashboard.vo.DashboardVO;

/**
 * 仪表盘服务接口
 *
 * <p>提供系统统计数据查询功能
 */
public interface DashboardService {

  /**
   * 获取系统统计数据
   *
   * @return 统计数据 VO
   */
  DashboardVO getStats();
}
