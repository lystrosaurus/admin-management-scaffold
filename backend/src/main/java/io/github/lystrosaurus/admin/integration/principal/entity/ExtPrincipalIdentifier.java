package io.github.lystrosaurus.admin.integration.principal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 外部主体标识符实体 */
@Getter
@Setter
@TableName("ext_principal_identifier")
public class ExtPrincipalIdentifier {

  /** 主键ID */
  @TableId(type = IdType.AUTO)
  private Long id;

  /** 外部主体ID */
  private Long principalId;

  /** ID类型(lark_open_id/lark_user_id/wecom_userid/beisen_employee_id) */
  private String idType;

  /** ID值 */
  private String idValue;

  /** 是否主ID(0-否，1-是) */
  private Integer isPrimary;

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
}
