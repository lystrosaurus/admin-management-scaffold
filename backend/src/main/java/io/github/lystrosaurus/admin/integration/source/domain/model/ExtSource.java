package io.github.lystrosaurus.admin.integration.source.domain.model;

import io.github.lystrosaurus.admin.integration.source.domain.event.ExtSourceCreated;
import io.github.lystrosaurus.admin.integration.source.domain.event.ExtSourceUpdated;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import java.time.LocalDateTime;

/** 外部身份源聚合根：守卫外部系统实例的不变量。 */
public class ExtSource extends AggregateRoot<ExtSourceId> {

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

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 更新时间 */
  private LocalDateTime updatedAt;

  private ExtSource(ExtSourceId id) {
    super(id);
  }

  /**
   * 创建外部身份源：状态默认 ENABLED，并登记 {@link ExtSourceCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param code 系统编码
   * @param name 系统名称
   * @param sourceType 来源类型
   * @param tenantKey 租户标识
   * @param priority 优先级
   * @param configJson 扩展配置JSON
   * @return 新外部身份源聚合
   */
  public static ExtSource create(
      ExtSourceId id,
      String code,
      String name,
      String sourceType,
      String tenantKey,
      Integer priority,
      String configJson) {
    ExtSource source = new ExtSource(id);
    source.code = code;
    source.name = name;
    source.sourceType = sourceType;
    source.tenantKey = tenantKey;
    source.status = "ENABLED";
    source.priority = priority;
    source.configJson = configJson;
    source.registerEvent(ExtSourceCreated.of(id.value(), code));
    return source;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 外部身份源聚合
   */
  public static ExtSource restore(
      ExtSourceId id,
      String code,
      String name,
      String sourceType,
      String tenantKey,
      String status,
      Integer priority,
      String configJson,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    ExtSource source = new ExtSource(id);
    source.code = code;
    source.name = name;
    source.sourceType = sourceType;
    source.tenantKey = tenantKey;
    source.status = status;
    source.priority = priority;
    source.configJson = configJson;
    source.createdAt = createdAt;
    source.updatedAt = updatedAt;
    return source;
  }

  /** 更新外部身份源：仅更新非空字段，登记 {@link ExtSourceUpdated} 事件。 */
  public void update(
      String name,
      String sourceType,
      String tenantKey,
      String status,
      Integer priority,
      String configJson) {
    if (name != null) {
      this.name = name;
    }
    if (sourceType != null) {
      this.sourceType = sourceType;
    }
    if (tenantKey != null) {
      this.tenantKey = tenantKey;
    }
    if (status != null) {
      this.status = status;
    }
    if (priority != null) {
      this.priority = priority;
    }
    if (configJson != null) {
      this.configJson = configJson;
    }
    registerEvent(ExtSourceUpdated.of(id().value()));
  }

  public String code() {
    return code;
  }

  public String name() {
    return name;
  }

  public String sourceType() {
    return sourceType;
  }

  public String tenantKey() {
    return tenantKey;
  }

  public String status() {
    return status;
  }

  public Integer priority() {
    return priority;
  }

  public String configJson() {
    return configJson;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public LocalDateTime updatedAt() {
    return updatedAt;
  }
}
