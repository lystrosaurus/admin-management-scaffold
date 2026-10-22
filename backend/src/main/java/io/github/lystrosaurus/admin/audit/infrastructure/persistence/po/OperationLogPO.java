package io.github.lystrosaurus.admin.audit.infrastructure.persistence.po;

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
 * 操作日志持久化对象（sys_operation_log）。
 *
 * <p>与建表脚本精确对齐：该表无 version 列，故不继承 {@code BasePO}。
 */
@Getter
@Setter
@TableName("sys_operation_log")
public class OperationLogPO implements PersistentObject, Serializable {

  /** 主键ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 用户ID */
  private Long userId;

  /** 操作类型 */
  private String operationType;

  /** 目标类型 */
  private String targetType;

  /** 目标ID */
  private Long targetId;

  /** 操作详情JSON */
  private String detailJson;

  /** IP地址 */
  private String ipAddress;

  /** 创建时间 */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdAt;
}
