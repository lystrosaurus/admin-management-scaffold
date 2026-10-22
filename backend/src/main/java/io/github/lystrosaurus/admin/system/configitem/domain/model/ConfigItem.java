package io.github.lystrosaurus.admin.system.configitem.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.system.configitem.domain.event.ConfigItemCreated;
import io.github.lystrosaurus.admin.system.configitem.domain.event.ConfigItemUpdated;
import java.time.LocalDateTime;

/** 配置项聚合根：守卫配置项的不变量，行为入口即业务规则落点。 */
public class ConfigItem extends AggregateRoot<ConfigItemId> {

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

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 版本号（乐观锁） */
  private Integer version;

  private ConfigItem(ConfigItemId id) {
    super(id);
  }

  /**
   * 创建新配置项：配置类型默认 STRING，状态默认 ENABLED，并登记 {@link ConfigItemCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param configKey 配置键
   * @param configValue 配置值
   * @param configType 配置类型
   * @param description 描述
   * @param status 状态
   * @return 新配置项聚合
   */
  public static ConfigItem create(
      ConfigItemId id,
      String configKey,
      String configValue,
      String configType,
      String description,
      String status) {
    ConfigItem configItem = new ConfigItem(id);
    configItem.configKey = configKey;
    configItem.configValue = configValue;
    configItem.configType = hasText(configType) ? configType : "STRING";
    configItem.description = description;
    configItem.status = hasText(status) ? status : "ENABLED";
    configItem.registerEvent(ConfigItemCreated.of(id.value(), configKey));
    return configItem;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 配置项聚合
   */
  public static ConfigItem restore(
      ConfigItemId id,
      String configKey,
      String configValue,
      String configType,
      String description,
      String status,
      LocalDateTime createdAt,
      Integer version) {
    ConfigItem configItem = new ConfigItem(id);
    configItem.configKey = configKey;
    configItem.configValue = configValue;
    configItem.configType = configType;
    configItem.description = description;
    configItem.status = status;
    configItem.createdAt = createdAt;
    configItem.version = version;
    return configItem;
  }

  /** 更新配置项资料：仅更新非空文本字段，登记 {@link ConfigItemUpdated} 事件。 */
  public void update(
      String configKey, String configValue, String configType, String description, String status) {
    if (hasText(configKey)) {
      this.configKey = configKey;
    }
    if (hasText(configValue)) {
      this.configValue = configValue;
    }
    if (hasText(configType)) {
      this.configType = configType;
    }
    if (hasText(description)) {
      this.description = description;
    }
    if (hasText(status)) {
      this.status = status;
    }
    registerEvent(ConfigItemUpdated.of(id.value()));
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public String configKey() {
    return configKey;
  }

  public String configValue() {
    return configValue;
  }

  public String configType() {
    return configType;
  }

  public String description() {
    return description;
  }

  public String status() {
    return status;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Integer version() {
    return version;
  }
}
