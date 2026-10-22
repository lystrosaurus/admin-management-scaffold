package io.github.lystrosaurus.admin.system.menu.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.system.menu.domain.event.MenuCreated;
import io.github.lystrosaurus.admin.system.menu.domain.event.MenuUpdated;
import java.time.LocalDateTime;

/** 菜单聚合根：守卫菜单创建默认值与资料更新规则。 */
public class Menu extends AggregateRoot<MenuId> {

  /** 父菜单ID（0 为顶级） */
  private Long parentId;

  /** 菜单名称 */
  private String name;

  /** 路由路径 */
  private String path;

  /** 前端组件路径 */
  private String component;

  /** 图标 */
  private String icon;

  /** 排序号 */
  private Integer sortOrder;

  /** 类型 */
  private Byte type;

  /** 权限码 */
  private String permissionCode;

  /** 可见性 */
  private Byte visible;

  /** 状态 */
  private Byte status;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 版本号（乐观锁） */
  private Integer version;

  private Menu(MenuId id) {
    super(id);
  }

  /**
   * 创建新菜单：父菜单缺省为 0，可见缺省为 1，状态初始为 1，并登记 {@link MenuCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @return 新菜单聚合
   */
  public static Menu create(
      MenuId id,
      Long parentId,
      String name,
      String path,
      String component,
      String icon,
      Integer sortOrder,
      Byte type,
      String permissionCode,
      Byte visible) {
    Menu menu = new Menu(id);
    menu.parentId = parentId == null ? 0L : parentId;
    menu.name = name;
    menu.path = path;
    menu.component = component;
    menu.icon = icon;
    menu.sortOrder = sortOrder;
    menu.type = type;
    menu.permissionCode = permissionCode;
    menu.visible = visible == null ? (byte) 1 : visible;
    menu.status = (byte) 1;
    menu.registerEvent(MenuCreated.of(id.value(), name));
    return menu;
  }

  /** 从持久化数据重建聚合（不登记事件）。 */
  public static Menu restore(
      MenuId id,
      Long parentId,
      String name,
      String path,
      String component,
      String icon,
      Integer sortOrder,
      Byte type,
      String permissionCode,
      Byte visible,
      Byte status,
      LocalDateTime createdAt,
      Integer version) {
    Menu menu = new Menu(id);
    menu.parentId = parentId;
    menu.name = name;
    menu.path = path;
    menu.component = component;
    menu.icon = icon;
    menu.sortOrder = sortOrder;
    menu.type = type;
    menu.permissionCode = permissionCode;
    menu.visible = visible;
    menu.status = status;
    menu.createdAt = createdAt;
    menu.version = version;
    return menu;
  }

  /** 更新菜单资料：仅更新非空字段，登记 {@link MenuUpdated} 事件。 */
  public void update(
      String name,
      String path,
      String component,
      String icon,
      Integer sortOrder,
      Byte type,
      String permissionCode,
      Byte visible,
      Byte status) {
    if (hasText(name)) {
      this.name = name;
    }
    if (hasText(path)) {
      this.path = path;
    }
    if (hasText(component)) {
      this.component = component;
    }
    if (hasText(icon)) {
      this.icon = icon;
    }
    if (sortOrder != null) {
      this.sortOrder = sortOrder;
    }
    if (type != null) {
      this.type = type;
    }
    if (hasText(permissionCode)) {
      this.permissionCode = permissionCode;
    }
    if (visible != null) {
      this.visible = visible;
    }
    if (status != null) {
      this.status = status;
    }
    registerEvent(MenuUpdated.of(id.value()));
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public Long parentId() {
    return parentId;
  }

  public String name() {
    return name;
  }

  public String path() {
    return path;
  }

  public String component() {
    return component;
  }

  public String icon() {
    return icon;
  }

  public Integer sortOrder() {
    return sortOrder;
  }

  public Byte type() {
    return type;
  }

  public String permissionCode() {
    return permissionCode;
  }

  public Byte visible() {
    return visible;
  }

  public Byte status() {
    return status;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Integer version() {
    return version;
  }
}
