package io.github.lystrosaurus.admin.system.role.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体
 *
 * <p>对应数据库表 sys_role
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

  /** 角色编码 */
  private String code;

  /** 角色名称 */
  private String name;

  /** 角色描述 */
  private String description;

  /** 状态：ENABLED, DISABLED */
  private String status;

  /** 排序顺序 */
  private Integer sortOrder;
}
