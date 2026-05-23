package io.github.lystrosaurus.admin.system.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色菜单关联实体
 *
 * <p>对应数据库表 sys_role_menu
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {

  /** 主键ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 角色ID */
  private Long roleId;

  /** 菜单ID */
  private Long menuId;
}
