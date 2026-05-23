package io.github.lystrosaurus.admin.system.menu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单实体
 *
 * <p>对应数据库表 sys_menu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

  /** 父菜单ID */
  private Long parentId;

  /** 菜单标题 */
  private String title;

  /** 路由路径 */
  private String routePath;

  /** 路由名称 */
  private String routeName;

  /** 组件路径 */
  private String componentPath;

  /** 图标 */
  private String icon;

  /** 权限编码 */
  private String permissionCode;

  /** 是否可见：1可见，0不可见 */
  private Integer visible;

  /** 是否缓存：1缓存，0不缓存 */
  private Integer keepAlive;

  /** 排序顺序 */
  private Integer sortOrder;

  /** 状态：ENABLED, DISABLED */
  private String status;
}
