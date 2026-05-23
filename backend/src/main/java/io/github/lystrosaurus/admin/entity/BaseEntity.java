package io.github.lystrosaurus.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/** 基础实体类 */
@Data
public abstract class BaseEntity implements Serializable {

  /** 主键 ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 创建时间 */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /** 更新时间 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  /** 创建人 */
  @TableField(fill = FieldFill.INSERT)
  private String createBy;

  /** 更新人 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private String updateBy;

  /** 逻辑删除标志 */
  @TableLogic private Integer deleted;

  /** 版本号（乐观锁） */
  @TableField(fill = FieldFill.INSERT)
  private Integer version;
}
