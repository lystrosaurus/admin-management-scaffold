package io.github.lystrosaurus.admin.auth.provider.entity;

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
 * 认证源实体
 *
 * <p>对应 auth_provider 表。
 */
@Getter
@Setter
@TableName("auth_provider")
public class AuthProvider implements Serializable {

  /** 主键 ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 认证源编码（唯一） */
  private String code;

  /** 认证源名称 */
  private String name;

  /** 客户端ID */
  private String clientId;

  /** 客户端密钥（加密存储） */
  private String clientSecretEncrypted;

  /** 回调地址 */
  private String redirectUri;

  /** 授权范围 */
  private String scopes;

  /** 是否启用（0=禁用, 1=启用） */
  private Integer enabled;

  /** 扩展配置JSON */
  private String configJson;

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
