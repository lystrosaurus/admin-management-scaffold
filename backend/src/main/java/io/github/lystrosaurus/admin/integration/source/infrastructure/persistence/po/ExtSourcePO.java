package io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 外部身份源持久化对象（ext_source）。 */
@Getter
@Setter
@TableName("ext_source")
public class ExtSourcePO extends BasePO {

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
}
