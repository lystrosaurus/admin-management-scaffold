package io.github.lystrosaurus.admin.system.configitem.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 系统配置项实体 */
@Getter
@Setter
@TableName("sys_config_item")
public class SysConfigItem extends BaseEntity {

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
