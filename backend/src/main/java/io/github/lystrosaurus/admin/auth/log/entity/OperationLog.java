package io.github.lystrosaurus.admin.auth.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 操作日志实体 */
@Getter
@Setter
@TableName("sys_operation_log")
public class OperationLog implements Serializable {

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
  private LocalDateTime createdAt;
}
