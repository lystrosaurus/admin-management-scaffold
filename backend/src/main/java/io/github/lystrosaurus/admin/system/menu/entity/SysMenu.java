package io.github.lystrosaurus.admin.system.menu.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 菜单实体 */
@Getter
@Setter
public class SysMenu extends BaseEntity {

  /** 父菜单ID(0=顶级) */
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

  /** 类型(1=目录,2=菜单,3=按钮) */
  private Byte type;

  /** 权限码 */
  private String permissionCode;

  /** 可见性(0=隐藏,1=显示) */
  private Byte visible;

  /** 状态(0=禁用,1=启用) */
  private Byte status;

  /** 版本号 */
  private Integer version;
}
