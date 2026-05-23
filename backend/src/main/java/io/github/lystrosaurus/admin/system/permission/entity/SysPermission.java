package io.github.lystrosaurus.admin.system.permission.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 权限实体 */
@Getter
@Setter
public class SysPermission extends BaseEntity {

  /** 权限编码(唯一) */
  private String code;

  /** 权限名称 */
  private String name;

  /** 描述 */
  private String description;

  /** 类型(MENU/BUTTON/API) */
  private String type;

  /** 所属模块 */
  private String module;

  /** 资源标识 */
  private String resource;

  /** 操作类型 */
  private String action;

  /** 状态 */
  private String status;

  /** 排序号 */
  private Integer sortOrder;
}
