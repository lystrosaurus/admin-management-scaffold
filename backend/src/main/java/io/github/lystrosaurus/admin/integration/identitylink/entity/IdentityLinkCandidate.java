package io.github.lystrosaurus.admin.integration.identitylink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 身份匹配候选实体
 *
 * <p>对应 identity_link_candidate 表。此表没有 deleted 字段（物理删除），没有 updated_at/updated_by。
 */
@Getter
@Setter
@TableName("identity_link_candidate")
public class IdentityLinkCandidate implements Serializable {

  /** 主键 ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 关联的外部主体ID */
  private Long sourcePrincipalId;

  /** 候选类型（USER/EMPLOYEE） */
  private String candidateType;

  /** 候选目标ID */
  private Long candidateId;

  /** 匹配分数 */
  private Integer score;

  /** 匹配原因 */
  private String reason;

  /** 状态（PENDING/CONFIRMED/REJECTED） */
  private String status;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 处理人 */
  private String handledBy;

  /** 处理时间 */
  private LocalDateTime handledAt;
}
