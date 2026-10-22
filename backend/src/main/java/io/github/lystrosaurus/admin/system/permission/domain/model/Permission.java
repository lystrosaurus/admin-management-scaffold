package io.github.lystrosaurus.admin.system.permission.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.system.permission.domain.event.PermissionCreated;
import io.github.lystrosaurus.admin.system.permission.domain.event.PermissionUpdated;
import java.time.LocalDateTime;

/** 权限聚合根：守卫权限编码与资料更新规则。 */
public class Permission extends AggregateRoot<PermissionId> {

  /** 权限编码(唯一) */
  private String code;

  /** 权限名称 */
  private String name;

  /** 类型 */
  private String type;

  /** 所属模块 */
  private String module;

  /** 资源标识 */
  private String resource;

  /** 操作类型 */
  private String action;

  /** 描述 */
  private String description;

  /** 排序号 */
  private Integer sortOrder;

  /** 状态(ENABLED/DISABLED) */
  private String status;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 版本号（乐观锁） */
  private Integer version;

  private Permission(PermissionId id) {
    super(id);
  }

  /**
   * 创建新权限：初始状态 ENABLED，并登记 {@link PermissionCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @return 新权限聚合
   */
  public static Permission create(
      PermissionId id,
      String code,
      String name,
      String type,
      String module,
      String resource,
      String action) {
    Permission permission = new Permission(id);
    permission.code = code;
    permission.name = name;
    permission.type = type;
    permission.module = module;
    permission.resource = resource;
    permission.action = action;
    permission.status = "ENABLED";
    permission.registerEvent(PermissionCreated.of(id.value(), code));
    return permission;
  }

  /** 从持久化数据重建聚合（不登记事件）。 */
  public static Permission restore(
      PermissionId id,
      String code,
      String name,
      String type,
      String module,
      String resource,
      String action,
      String description,
      Integer sortOrder,
      String status,
      LocalDateTime createdAt,
      Integer version) {
    Permission permission = new Permission(id);
    permission.code = code;
    permission.name = name;
    permission.type = type;
    permission.module = module;
    permission.resource = resource;
    permission.action = action;
    permission.description = description;
    permission.sortOrder = sortOrder;
    permission.status = status;
    permission.createdAt = createdAt;
    permission.version = version;
    return permission;
  }

  /** 更新权限资料：仅更新非空文本字段，登记 {@link PermissionUpdated} 事件。 */
  public void update(
      String name, String type, String module, String resource, String action, String status) {
    if (hasText(name)) {
      this.name = name;
    }
    if (hasText(type)) {
      this.type = type;
    }
    if (hasText(module)) {
      this.module = module;
    }
    if (hasText(resource)) {
      this.resource = resource;
    }
    if (hasText(action)) {
      this.action = action;
    }
    if (hasText(status)) {
      this.status = status;
    }
    registerEvent(PermissionUpdated.of(id.value()));
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

  public String type() {
    return type;
  }

  public String module() {
    return module;
  }

  public String resource() {
    return resource;
  }

  public String action() {
    return action;
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

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Integer version() {
    return version;
  }
}
