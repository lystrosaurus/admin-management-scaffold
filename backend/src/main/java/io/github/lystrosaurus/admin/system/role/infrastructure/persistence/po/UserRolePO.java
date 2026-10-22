package io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 用户-角色关联持久化对象：映射 sys_user_role 表。 */
@Getter
@Setter
@TableName("sys_user_role")
public class UserRolePO extends BasePO {

  /** 用户ID */
  private Long userId;

  /** 角色ID */
  private Long roleId;
}
