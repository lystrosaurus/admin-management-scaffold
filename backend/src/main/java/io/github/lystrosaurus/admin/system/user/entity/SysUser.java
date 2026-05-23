package io.github.lystrosaurus.admin.system.user.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 用户实体 */
@Getter
@Setter
public class SysUser extends BaseEntity {

  /** 用户名 */
  private String username;

  /** 密码哈希 */
  private String passwordHash;

  /** 昵称 */
  private String nickname;

  /** 头像文件ID */
  private Long avatarFileId;

  /** 手机号 */
  private String phone;

  /** 邮箱 */
  private String email;

  /** 绑定员工ID */
  private Long employeeId;

  /** 状态(ENABLED/DISABLED/LOCKED) */
  private String status;

  /** token版本号 */
  private Integer tokenVersion;

  /** 最后登录时间 */
  private LocalDateTime lastLoginAt;

  /** 最后登录IP */
  private String lastLoginIp;
}
