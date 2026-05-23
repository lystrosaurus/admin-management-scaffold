package io.github.lystrosaurus.admin.integration.principal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 外部主体实体 */
@Getter
@Setter
@TableName("ext_principal")
public class ExtPrincipal {

  /** 主键ID */
  @TableId(type = IdType.AUTO)
  private Long id;

  /** 外部系统ID */
  private Long sourceId;

  /** 主体类型(USER/ORG) */
  private String principalType;

  /** 外部系统主键 */
  private String externalKey;

  /** 显示名称 */
  private String displayName;

  /** 状态(ACTIVE/INACTIVE) */
  private String status;

  /** 原始数据JSON */
  @TableField("raw_payload_json")
  private String rawPayloadJson;

  /** 最后同步时间 */
  private LocalDateTime lastSyncAt;

  /** 映射目标类型(EMPLOYEE/ORG_UNIT) */
  private String canonicalType;

  /** 映射目标ID(hr_employee.id或org_unit.id) */
  private Long canonicalId;

  /** 关联状态(UNLINKED/AUTO_LINKED/MANUAL_LINKED/CONFLICT) */
  private String linkStatus;

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
