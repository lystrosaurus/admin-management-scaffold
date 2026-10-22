package io.github.lystrosaurus.admin.audit.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.PersistentObject;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录日志持久化对象（sys_login_log）。
 *
 * <p>与建表脚本精确对齐：该表无 version / 审计列，故不继承 {@code BasePO}。
 */
@Getter
@Setter
@TableName("sys_login_log")
public class LoginLogPO implements PersistentObject, Serializable {

  /** 主键ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 用户ID */
  private Long userId;

  /** 登录类型: PASSWORD/OAUTH */
  private String loginType;

  /** OAuth提供方编码: LARK/WECOM/WECHAT */
  private String providerCode;

  /** IP地址 */
  private String ipAddress;

  /** User-Agent */
  private String userAgent;

  /** 登录状态: SUCCESS/FAILED */
  private String status;

  /** 失败原因 */
  private String failureReason;

  /** 登录时间 */
  private LocalDateTime loginAt;
}
