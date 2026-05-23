package io.github.lystrosaurus.admin.system.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 角色-部门关联实体（数据权限 CUSTOM 范围） */
@Getter
@Setter
@TableName("sys_role_org")
public class SysRoleOrg {

  /** 主键ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 角色ID */
  private Long roleId;

  /** 部门ID */
  private Long orgId;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 创建人 */
  private String createdBy;
}
