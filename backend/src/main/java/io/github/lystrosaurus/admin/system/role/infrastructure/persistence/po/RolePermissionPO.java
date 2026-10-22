package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 角色-权限关联持久化对象：映射 sys_role_permission 表。 */
@Getter
@Setter
@TableName("sys_role_permission")
public class RolePermissionPO extends BasePO {

  /** 角色ID */
  private Long roleId;

  /** 权限ID */
  private Long permissionId;
}
