package io.github.lystrosaurus.admin.organization.orgunit.domain.model;

import io.github.lystrosaurus.admin.organization.orgunit.domain.event.OrgUnitCreated;
import io.github.lystrosaurus.admin.organization.orgunit.domain.event.OrgUnitUpdated;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import java.time.LocalDateTime;

/** 组织单元聚合根：守卫组织树的不变量，行为入口即业务规则落点。 */
public class OrgUnit extends AggregateRoot<OrgUnitId> {

  /** 父节点ID(0表示根节点) */
  private Long parentId;

  /** 组织编码 */
  private String code;

  /** 组织名称 */
  private String name;

  /** 完整路径(如/1/5/12/) */
  private String fullPath;

  /** 层级(从1开始) */
  private Integer level;

  /** 负责人员工ID */
  private Long managerEmployeeId;

  /** 排序号 */
  private Integer sortOrder;

  /** 状态(ENABLED/DISABLED) */
  private String status;

  /** 数据来源(MANUAL/IMPORT/API) */
  private String sourceType;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 版本号（乐观锁） */
  private Integer version;

  private OrgUnit(OrgUnitId id) {
    super(id);
  }

  /**
   * 创建新组织单元：来源默认 MANUAL，状态默认 ENABLED，排序号默认 0，并登记 {@link OrgUnitCreated}
   * 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param parentId 父节点ID
   * @param code 组织编码
   * @param name 组织名称
   * @param fullPath 完整路径
   * @param level 层级
   * @param managerEmployeeId 负责人员工ID
   * @param sortOrder 排序号
   * @return 新组织单元聚合
   */
  public static OrgUnit create(
      OrgUnitId id,
      Long parentId,
      String code,
      String name,
      String fullPath,
      Integer level,
      Long managerEmployeeId,
      Integer sortOrder) {
    OrgUnit orgUnit = new OrgUnit(id);
    orgUnit.parentId = parentId;
    orgUnit.code = code;
    orgUnit.name = name;
    orgUnit.fullPath = fullPath;
    orgUnit.level = level;
    orgUnit.managerEmployeeId = managerEmployeeId;
    orgUnit.sortOrder = sortOrder != null ? sortOrder : 0;
    orgUnit.status = "ENABLED";
    orgUnit.sourceType = "MANUAL";
    orgUnit.registerEvent(OrgUnitCreated.of(id.value(), code));
    return orgUnit;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 组织单元聚合
   */
  public static OrgUnit restore(
      OrgUnitId id,
      Long parentId,
      String code,
      String name,
      String fullPath,
      Integer level,
      Long managerEmployeeId,
      Integer sortOrder,
      String status,
      String sourceType,
      LocalDateTime createdAt,
      Integer version) {
    OrgUnit orgUnit = new OrgUnit(id);
    orgUnit.parentId = parentId;
    orgUnit.code = code;
    orgUnit.name = name;
    orgUnit.fullPath = fullPath;
    orgUnit.level = level;
    orgUnit.managerEmployeeId = managerEmployeeId;
    orgUnit.sortOrder = sortOrder;
    orgUnit.status = status;
    orgUnit.sourceType = sourceType;
    orgUnit.createdAt = createdAt;
    orgUnit.version = version;
    return orgUnit;
  }

  /** 更新组织单元资料：仅更新非空文本字段与非空字段，登记 {@link OrgUnitUpdated} 事件。 */
  public void update(
      String name, Long parentId, Long managerEmployeeId, Integer sortOrder, String status) {
    if (hasText(name)) {
      this.name = name;
    }
    if (parentId != null) {
      this.parentId = parentId;
    }
    if (managerEmployeeId != null) {
      this.managerEmployeeId = managerEmployeeId;
    }
    if (sortOrder != null) {
      this.sortOrder = sortOrder;
    }
    if (hasText(status)) {
      this.status = status;
    }
    registerEvent(OrgUnitUpdated.of(id.value()));
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public Long parentId() {
    return parentId;
  }

  public String code() {
    return code;
  }

  public String name() {
    return name;
  }

  public String fullPath() {
    return fullPath;
  }

  public Integer level() {
    return level;
  }

  public Long managerEmployeeId() {
    return managerEmployeeId;
  }

  public Integer sortOrder() {
    return sortOrder;
  }

  public String status() {
    return status;
  }

  public String sourceType() {
    return sourceType;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Integer version() {
    return version;
  }
}
