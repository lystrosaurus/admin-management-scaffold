package io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.PersistentObject;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 外部身份源持久化对象（ext_source）。
 *
 * <p>与建表脚本精确对齐：该表无 version 列，故不继承 {@code BasePO}。
 */
@Getter
@Setter
@TableName("ext_source")
public class ExtSourcePO implements PersistentObject, Serializable {

  /** 主键 ID（领域层预生成） */
  @TableId(type = IdType.INPUT)
  private Long id;

  /** 系统编码(BEISEN/LARK/WECOM) */
  private String code;

  /** 系统名称 */
  private String name;

  /** 来源类型(HR/IM/OA) */
  private String sourceType;

  /** 租户标识 */
  private String tenantKey;

  /** 状态(ENABLED/DISABLED) */
  private String status;

  /** 优先级(越高越优先) */
  private Integer priority;

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
