package io.github.lystrosaurus.admin.shared.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 轻量关联表持久化对象基类：仅用于无审计/逻辑删除/乐观锁列的关联表（如 sys_role_org）。
 *
 * <p>字段与旧 {@code SysRoleOrg} 精确对齐：ID 由 MyBatis-Plus 雪花算法分配，createdAt/createdBy 不做自动填充，
 * 保持与历史写入行为一致。
 */
@Data
public abstract class BaseAssociationPO implements PersistentObject, Serializable {

  /** 主键 ID（雪花分配） */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 创建人 */
  private String createdBy;
}
