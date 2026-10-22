package io.github.lystrosaurus.admin.system.user.infrastructure.converter;

import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.domain.model.UserStatus;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Component;

/** 用户聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class UserPoConverter {

  /** 领域对象转持久化对象 */
  public UserPO toPo(User user) {
    UserPO po = new UserPO();
    po.setId(user.id() == null ? null : user.id().value());
    po.setUsername(user.username());
    po.setPasswordHash(user.passwordHash());
    po.setNickname(user.nickname());
    po.setAvatarFileId(user.avatarFileId());
    po.setPhone(user.phone() == null ? null : user.phone().value());
    po.setEmail(user.email() == null ? null : user.email().value());
    po.setEmployeeId(user.employeeId());
    po.setStatus(user.status() == null ? null : user.status().name());
    po.setTokenVersion(user.tokenVersion());
    po.setLastLoginAt(user.lastLoginAt());
    po.setLastLoginIp(user.lastLoginIp());
    po.setVersion(user.version());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public User toDomain(UserPO po) {
    return User.restore(
        UserId.of(po.getId()),
        po.getUsername(),
        po.getPasswordHash(),
        po.getNickname(),
        po.getAvatarFileId(),
        po.getPhone(),
        po.getEmail(),
        po.getEmployeeId(),
        po.getStatus() == null ? null : UserStatus.fromString(po.getStatus()),
        po.getTokenVersion(),
        po.getLastLoginAt(),
        po.getLastLoginIp(),
        po.getCreatedAt(),
        po.getVersion());
  }
}
