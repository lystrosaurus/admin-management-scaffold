package io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 外部主体持久化对象（ext_principal）。 */
@Getter
@Setter
@TableName("ext_principal")
public class ExtPrincipalPO extends BasePO {

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

  /** 映射目标ID */
  private Long canonicalId;

  /** 关联状态(UNLINKED/AUTO_LINKED/MANUAL_LINKED/CONFLICT) */
  private String linkStatus;
}
