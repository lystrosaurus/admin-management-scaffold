package io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 菜单持久化对象：仅用于 infrastructure 层映射 sys_menu 表。 */
@Getter
@Setter
@TableName("sys_menu")
public class MenuPO extends BasePO {

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
}
