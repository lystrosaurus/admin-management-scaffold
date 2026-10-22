package io.github.lystrosaurus.admin.file.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.PersistentObject;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件持久化对象（sys_file）。
 *
 * <p>与建表脚本精确对齐；主键沿用 ASSIGN_ID，审计字段由 MyBatis-Plus 自动填充。
 */
@Getter
@Setter
@TableName("sys_file")
public class SysFilePO implements PersistentObject, Serializable {

  /** 主键 ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 原始文件名 */
  private String originalName;

  /** 存储文件名(uuid) */
  private String storedName;

  /** 存储相对路径 */
  private String path;

  /** 扩展名(不含点) */
  private String ext;

  /** MIME类型 */
  private String mimeType;

  /** 文件大小(字节) */
  private Long sizeBytes;

  /** 文件MD5 */
  private String md5;

  /** 上传用户ID */
  private Long uploaderId;

  /** 业务类型 */
  private String bizType;

  /** 状态(NORMAL/DELETED) */
  private String status;

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

  /** 版本号 */
  @TableField(fill = FieldFill.INSERT)
  private Integer version;
}
