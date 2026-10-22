package io.github.lystrosaurus.admin.audit.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.audit.domain.acl.LoginLogRepository;
import io.github.lystrosaurus.admin.audit.domain.model.LoginLog;
import io.github.lystrosaurus.admin.audit.infrastructure.converter.LoginLogPoConverter;
import io.github.lystrosaurus.admin.audit.infrastructure.persistence.mapper.LoginLogMapper;
import io.github.lystrosaurus.admin.audit.infrastructure.persistence.po.LoginLogPO;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 登录日志仓储的 MyBatis-Plus 实现。 */
@Repository
@RequiredArgsConstructor
public class LoginLogRepositoryImpl implements LoginLogRepository {

  private final LoginLogMapper loginLogMapper;
  private final LoginLogPoConverter converter;

  @Override
  public void save(LoginLog log) {
    loginLogMapper.insert(converter.toPo(log));
  }

  @Override
  public List<LoginLog> listByUserId(Long userId, int limit) {
    return loginLogMapper
        .selectList(
            new LambdaQueryWrapper<LoginLogPO>()
                .eq(LoginLogPO::getUserId, userId)
                .orderByDesc(LoginLogPO::getLoginAt)
                .last("LIMIT " + limit))
        .stream()
        .map(converter::toDomain)
        .toList();
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
    LambdaQueryWrapper<LoginLogPO> wrapper = new LambdaQueryWrapper<>();
    if (userId != null) {
      wrapper.eq(LoginLogPO::getUserId, userId);
    }
    if (StringUtils.hasText(ipAddress)) {
      wrapper.like(LoginLogPO::getIpAddress, ipAddress);
    }
    if (StringUtils.hasText(status)) {
      wrapper.eq(LoginLogPO::getStatus, status);
    }
    if (start != null) {
      wrapper.ge(LoginLogPO::getLoginAt, start);
    }
    if (end != null) {
      wrapper.le(LoginLogPO::getLoginAt, end);
    }
    wrapper.orderByDesc(LoginLogPO::getLoginAt);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<LoginLogPO> result =
        loginLogMapper.selectPage(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), wrapper);
    List<LoginLog> records = result.getRecords().stream().map(converter::toDomain).toList();
    return new Page<>(records, result.getTotal(), page, size);
  }
}
