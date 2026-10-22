package io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.PersistentObject;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 外部主体标识符持久化对象（ext_principal_identifier）。
 *
 * <p>与建表脚本精确对齐：该表无 deleted/version 列，故不继承 {@code BasePO}。
 */
@Getter
@Setter
@TableName("ext_principal_identifier")
public class ExtPrincipalIdentifierPO implements PersistentObject, Serializable {

  /** 主键 ID（领域层预生成） */
  @TableId(type = IdType.INPUT)
  private Long id;

  /** 外部主体ID */
  private Long principalId;

  /** ID类型 */
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
