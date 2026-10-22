package io.github.lystrosaurus.admin.integration.identitylink.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 身份匹配候选持久化对象（identity_link_candidate）。 */
@Getter
@Setter
@TableName("identity_link_candidate")
public class IdentityLinkCandidatePO extends BasePO {

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

  /** 处理人 */
  private String handledBy;

  /** 处理时间 */
  private LocalDateTime handledAt;
}
