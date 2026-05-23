package io.github.lystrosaurus.admin.auth.log.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.auth.log.dao.LoginLogDAO;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import io.github.lystrosaurus.admin.auth.log.mapper.LoginLogMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
