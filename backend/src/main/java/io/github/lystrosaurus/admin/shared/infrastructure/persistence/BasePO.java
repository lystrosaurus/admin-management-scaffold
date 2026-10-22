package io.github.lystrosaurus.admin.shared.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 持久化对象基类：仅用于基础设施层映射数据库表，不进入领域层。
 *
 * <p>ID 采用 {@link IdType#INPUT}，由领域层通过 {@code IdGenerator} 端口预生成。
 */
@Data
public abstract class BasePO implements Serializable {

  /** 主键 ID（领域层预生成） */
  @TableId(type = IdType.INPUT)
  private Long id;

  /** 创建时间 */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdAt;

  /** 更新时间 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updatedAt;

  /** 创建人 */
  @TableField(fill = FieldFill.INSERT)
  private String createdBy;

  /** 更新人 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private String updatedBy;

  /** 逻辑删除标志 */
  @TableLogic private Integer deleted;

  /** 版本号（乐观锁） */
  @Version
  @TableField(fill = FieldFill.INSERT)
  private Integer version;
}
