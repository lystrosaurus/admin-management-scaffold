package io.github.lystrosaurus.admin.system.configitem.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 配置项持久化对象：仅用于 infrastructure 层映射 sys_config_item 表。 */
@Getter
@Setter
@TableName("sys_config_item")
public class ConfigItemPO extends BasePO {

  /** 配置键(唯一) */
  private String configKey;

  /** 配置值 */
  private String configValue;

  /** 配置类型(STRING/NUMBER/BOOLEAN/JSON) */
  private String configType;

  /** 描述 */
  private String description;

  /** 状态(ENABLED/DISABLED) */
  private String status;
}
