package io.github.lystrosaurus.admin.integration.principal.domain.model;

import io.github.lystrosaurus.admin.integration.principal.domain.event.ExtPrincipalCreated;
import io.github.lystrosaurus.admin.integration.principal.domain.event.ExtPrincipalUpdated;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import java.time.LocalDateTime;
import java.util.List;

/** 外部主体聚合根：守卫外部主体及其标识符的不变量。 */
public class ExtPrincipal extends AggregateRoot<ExtPrincipalId> {

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
  private String rawPayloadJson;

  /** 最后同步时间 */
  private LocalDateTime lastSyncAt;

  /** 映射目标类型(EMPLOYEE/ORG_UNIT) */
  private String canonicalType;

  /** 映射目标ID */
  private Long canonicalId;

  /** 关联状态(UNLINKED/AUTO_LINKED/MANUAL_LINKED/CONFLICT) */
  private String linkStatus;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 更新时间 */
  private LocalDateTime updatedAt;

  /** 标识符列表 */
  private List<ExtPrincipalIdentifier> identifiers;

  private ExtPrincipal(ExtPrincipalId id) {
    super(id);
  }

  /**
   * 创建外部主体：状态默认 ACTIVE、关联状态默认 UNLINKED，并登记 {@link ExtPrincipalCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param sourceId 外部系统ID
   * @param principalType 主体类型
   * @param externalKey 外部系统主键
   * @param displayName 显示名称
   * @param rawPayloadJson 原始数据JSON
   * @param identifiers 标识符列表
   * @return 新外部主体聚合
   */
  public static ExtPrincipal create(
      ExtPrincipalId id,
      Long sourceId,
      String principalType,
      String externalKey,
      String displayName,
      String rawPayloadJson,
      List<ExtPrincipalIdentifier> identifiers) {
    ExtPrincipal principal = new ExtPrincipal(id);
    principal.sourceId = sourceId;
    principal.principalType = principalType;
    principal.externalKey = externalKey;
    principal.displayName = displayName;
    principal.status = "ACTIVE";
    principal.rawPayloadJson = rawPayloadJson;
    principal.linkStatus = "UNLINKED";
    principal.identifiers = identifiers == null ? List.of() : List.copyOf(identifiers);
    principal.registerEvent(ExtPrincipalCreated.of(id.value(), sourceId, externalKey));
    return principal;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 外部主体聚合
   */
  public static ExtPrincipal restore(
      ExtPrincipalId id,
      Long sourceId,
      String principalType,
      String externalKey,
      String displayName,
      String status,
      String rawPayloadJson,
      LocalDateTime lastSyncAt,
      String canonicalType,
      Long canonicalId,
      String linkStatus,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<ExtPrincipalIdentifier> identifiers) {
    ExtPrincipal principal = new ExtPrincipal(id);
    principal.sourceId = sourceId;
    principal.principalType = principalType;
    principal.externalKey = externalKey;
    principal.displayName = displayName;
    principal.status = status;
    principal.rawPayloadJson = rawPayloadJson;
    principal.lastSyncAt = lastSyncAt;
    principal.canonicalType = canonicalType;
    principal.canonicalId = canonicalId;
    principal.linkStatus = linkStatus;
    principal.createdAt = createdAt;
    principal.updatedAt = updatedAt;
    principal.identifiers = identifiers == null ? List.of() : List.copyOf(identifiers);
    return principal;
  }

  /** 更新外部主体：仅更新非空字段，登记 {@link ExtPrincipalUpdated} 事件。 */
  public void update(
      String displayName,
      String status,
      String canonicalType,
      Long canonicalId,
      String linkStatus) {
    if (displayName != null) {
      this.displayName = displayName;
    }
    if (status != null) {
      this.status = status;
    }
    if (canonicalType != null) {
      this.canonicalType = canonicalType;
    }
    if (canonicalId != null) {
      this.canonicalId = canonicalId;
    }
    if (linkStatus != null) {
      this.linkStatus = linkStatus;
    }
    registerEvent(ExtPrincipalUpdated.of(id().value()));
  }

  /** 标记为人工关联到规范目标，登记 {@link ExtPrincipalUpdated} 事件。 */
  public void markManualLinked(String canonicalType, Long canonicalId) {
    this.canonicalType = canonicalType;
    this.canonicalId = canonicalId;
    this.linkStatus = "MANUAL_LINKED";
    registerEvent(ExtPrincipalUpdated.of(id().value()));
  }

  public Long sourceId() {
    return sourceId;
  }

  public String principalType() {
    return principalType;
  }

  public String externalKey() {
    return externalKey;
  }

  public String displayName() {
    return displayName;
  }

  public String status() {
    return status;
  }

  public String rawPayloadJson() {
    return rawPayloadJson;
  }

  public LocalDateTime lastSyncAt() {
    return lastSyncAt;
  }

  public String canonicalType() {
    return canonicalType;
  }

  public Long canonicalId() {
    return canonicalId;
  }

  public String linkStatus() {
    return linkStatus;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public LocalDateTime updatedAt() {
    return updatedAt;
  }

  public List<ExtPrincipalIdentifier> identifiers() {
    return identifiers;
  }
}
