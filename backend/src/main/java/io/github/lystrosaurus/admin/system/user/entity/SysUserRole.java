package io.github.lystrosaurus.admin.system.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户角色关联实体
 *
 * <p>对应数据库表 sys_user_role
 */
@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

  /** 主键ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 用户ID */
  private Long userId;

  /** 角色ID */
  private Long roleId;
}
