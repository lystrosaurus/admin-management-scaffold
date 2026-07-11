package io.github.lystrosaurus.admin.auth.log.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.auth.log.dao.LoginLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import io.github.lystrosaurus.admin.auth.log.mapper.LoginLogMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 登录日志数据访问对象实现 */
@Repository
@RequiredArgsConstructor
public class LoginLogDAOImpl implements LoginLogDAO {

  private final LoginLogMapper loginLogMapper;

  @Override
  public void save(LoginLog log) {
    loginLogMapper.insert(log);
  }

  @Override
  public List<LoginLog> listByUserId(Long userId, int limit) {
    return loginLogMapper.selectList(
        new LambdaQueryWrapper<LoginLog>()
            .eq(LoginLog::getUserId, userId)
            .orderByDesc(LoginLog::getLoginAt)
            .last("LIMIT " + limit));
  }

  @Override
  public Page<LoginLog> findPage(
      Long userId,
      String ipAddress,
      String status,
      LocalDateTime start,
      LocalDateTime end,
      int page,
      int size) {
    LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
    if (userId != null) {
      wrapper.eq(LoginLog::getUserId, userId);
    }
    if (StringUtils.hasText(ipAddress)) {
      wrapper.like(LoginLog::getIpAddress, ipAddress);
    }
    if (StringUtils.hasText(status)) {
      wrapper.eq(LoginLog::getStatus, status);
    }
    if (start != null) {
      wrapper.ge(LoginLog::getLoginAt, start);
    }
    if (end != null) {
      wrapper.le(LoginLog::getLoginAt, end);
    }
    wrapper.orderByDesc(LoginLog::getLoginAt);
    return loginLogMapper.selectPage(new Page<>(page, size), wrapper);
  }
}
