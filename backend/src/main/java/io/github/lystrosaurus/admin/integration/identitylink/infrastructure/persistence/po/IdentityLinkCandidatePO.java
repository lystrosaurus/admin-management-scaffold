package io.github.lystrosaurus.admin.integration.identitylink.infrastructure.persistence.po;

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
 * 身份匹配候选持久化对象（identity_link_candidate）。
 *
 * <p>与建表脚本精确对齐：该表无 updated_at/updated_by/deleted/version 列，故不继承 {@code BasePO}。
 */
@Getter
@Setter
@TableName("identity_link_candidate")
public class IdentityLinkCandidatePO implements PersistentObject, Serializable {

  /** 主键 ID（领域层预生成） */
  @TableId(type = IdType.INPUT)
  private Long id;

  /** 关联的外部主体ID */
  private Long sourcePrincipalId;

  /** 候选类型(EMPLOYEE/ORG_UNIT) */
  private String candidateType;

  /** 候选目标ID */
  private Long candidateId;

  /** 匹配分数(0-100) */
  private Integer score;

  /** 匹配原因 */
  private String reason;

  /** 状态(PENDING/CONFIRMED/REJECTED) */
  private String status;

  /** 创建时间 */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdAt;

  /** 处理人 */
  private String handledBy;

  /** 处理时间 */
  private LocalDateTime handledAt;
}
