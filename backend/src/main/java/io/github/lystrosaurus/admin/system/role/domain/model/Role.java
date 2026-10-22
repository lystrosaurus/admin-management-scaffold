package io.github.lystrosaurus.admin.system.role.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.system.role.domain.event.RoleCreated;
import io.github.lystrosaurus.admin.system.role.domain.event.RoleMenuChanged;
import io.github.lystrosaurus.admin.system.role.domain.event.RolePermissionChanged;
import io.github.lystrosaurus.admin.system.role.domain.event.RoleUpdated;
import java.time.LocalDateTime;
import java.util.List;

/** 角色聚合根：守卫角色的不变量，行为入口即业务规则落点。 */
public class Role extends AggregateRoot<RoleId> {

  /** 角色编码(唯一) */
  private String code;

  /** 角色名称 */
  private String name;

  /** 描述 */
  private String description;

  /** 排序号 */
  private Integer sortOrder;

  /** 状态(ENABLED/DISABLED) */
  private String status;

  /** 数据权限范围(ALL/ORG_TREE/ORG_ONLY/SELF/CUSTOM) */
  private String dataScopeType;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 版本号（乐观锁） */
  private Integer version;

  private Role(RoleId id) {
    super(id);
  }

  /**
   * 创建新角色：初始状态 ENABLED，并登记 {@link RoleCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param code 角色编码
   * @param name 角色名称
   * @param description 描述
   * @param sortOrder 排序号
   * @param dataScopeType 数据权限范围
   * @return 新角色聚合
   */
  public static Role create(
      RoleId id, String code, String name, String description, Integer sortOrder,
      String dataScopeType) {
    Role role = new Role(id);
    role.code = code;
    role.name = name;
    role.description = description;
    role.sortOrder = sortOrder;
    role.status = "ENABLED";
    role.dataScopeType = dataScopeType;
    role.registerEvent(RoleCreated.of(id.value(), code));
    return role;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 角色聚合
   */
  public static Role restore(
      RoleId id,
      String code,
      String name,
      String description,
      Integer sortOrder,
      String status,
      String dataScopeType,
      LocalDateTime createdAt,
      Integer version) {
    Role role = new Role(id);
    role.code = code;
    role.name = name;
    role.description = description;
    role.sortOrder = sortOrder;
    role.status = status;
    role.dataScopeType = dataScopeType;
    role.createdAt = createdAt;
    role.version = version;
    return role;
  }

  /** 更新角色资料：仅更新非空文本字段与非空排序号，登记 {@link RoleUpdated} 事件。 */
  public void update(
      String name, String description, Integer sortOrder, String status, String dataScopeType) {
    if (hasText(name)) {
      this.name = name;
    }
    if (hasText(description)) {
      this.description = description;
    }
    if (sortOrder != null) {
      this.sortOrder = sortOrder;
    }
    if (hasText(status)) {
      this.status = status;
    }
    if (hasText(dataScopeType)) {
      this.dataScopeType = dataScopeType;
    }
    registerEvent(RoleUpdated.of(id.value()));
  }

  /** 变更角色权限（关联写入由仓储端口完成），登记 {@link RolePermissionChanged} 事件。 */
  public void changePermissions(List<Long> permissionIds) {
    registerEvent(RolePermissionChanged.of(id.value(), permissionIds));
  }

  /** 变更角色菜单（关联写入由仓储端口完成），登记 {@link RoleMenuChanged} 事件。 */
  public void changeMenus(List<Long> menuIds) {
    registerEvent(RoleMenuChanged.of(id.value(), menuIds));
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public String code() {
    return code;
  }

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  public Integer sortOrder() {
    return sortOrder;
  }

  public String status() {
    return status;
  }

  public String dataScopeType() {
    return dataScopeType;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Integer version() {
    return version;
  }
}
