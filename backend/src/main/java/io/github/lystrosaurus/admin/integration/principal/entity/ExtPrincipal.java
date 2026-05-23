package io.github.lystrosaurus.admin.integration.principal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 外部主体实体（最小版本）
 *
 * <p>仅包含 IdentityLinkCandidateService.confirm() 所需的字段。 完整版本将由 Agent A 提供。
 */
@Getter
@Setter
@TableName("ext_principal")
public class ExtPrincipal implements Serializable {

  /** 主键 ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 链接状态 */
  private String linkStatus;

  /** 规范类型 */
  private String canonicalType;

  /** 规范ID */
  private String canonicalId;
}
