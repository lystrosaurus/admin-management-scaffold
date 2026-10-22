package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 角色持久化对象：仅用于 infrastructure 层映射 sys_role 表。 */
@Getter
@Setter
@TableName("sys_role")
public class RolePO extends BasePO {

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

  /** 数据权限范围 */
  private String dataScopeType;
}
