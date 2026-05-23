package io.github.lystrosaurus.admin.system.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统权限实体
 *
 * <p>对应数据库表 sys_permission
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

  /** 权限编码 */
  private String code;

  /** 权限名称 */
  private String name;

  /** 权限类型：MENU, BUTTON, API */
  private String type;

  /** 所属模块 */
  private String module;

  /** 资源标识 */
  private String resource;

  /** 操作标识 */
  private String action;

  /** 状态：ENABLED, DISABLED */
  private String status;

  /** 排序顺序 */
  private Integer sortOrder;
}
