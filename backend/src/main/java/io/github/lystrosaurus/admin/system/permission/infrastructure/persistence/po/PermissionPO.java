package io.github.lystrosaurus.admin.system.permission.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 权限持久化对象：仅用于 infrastructure 层映射 sys_permission 表。 */
@Getter
@Setter
@TableName("sys_permission")
public class PermissionPO extends BasePO {

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
}
