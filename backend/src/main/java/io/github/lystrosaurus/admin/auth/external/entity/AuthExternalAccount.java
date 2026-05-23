package io.github.lystrosaurus.admin.auth.external.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 三方账号实体
 *
 * <p>对应 auth_external_account 表。
 */
@Getter
@Setter
@TableName("auth_external_account")
public class AuthExternalAccount implements Serializable {

  /** 主键 ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 认证源ID */
  private Long providerId;

  /** 第三方平台用户ID */
  private String providerUserId;

  /** 关联的本地用户ID */
  private Long userId;

  /** 关联的员工ID */
  private Long employeeId;

  /** 标识信息JSON */
  private String identifierJson;

  /** 第三方昵称 */
  private String nickname;

  /** 第三方头像URL */
  private String avatarUrl;

  /** 绑定状态（BOUND/UNBOUND/CONFLICT） */
  private String bindStatus;

  /** 最后登录时间 */
  private LocalDateTime lastLoginAt;

  /** 创建时间 */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdAt;

  /** 创建人 */
  @TableField(fill = FieldFill.INSERT)
  private String createdBy;

  /** 更新时间 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updatedAt;

  /** 更新人 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private String updatedBy;

  /** 逻辑删除标志 */
  @TableLogic private Integer deleted;
}
