package io.github.lystrosaurus.admin.system.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 角色-组织关联实体（物理删除，不做逻辑删除） */
@Getter
@Setter
@TableName("sys_role_org")
public class SysRoleOrg implements Serializable {

  /** 主键ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 角色ID */
  private Long roleId;

  /** 组织ID */
  private Long orgId;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 创建人 */
  private String createdBy;
}
