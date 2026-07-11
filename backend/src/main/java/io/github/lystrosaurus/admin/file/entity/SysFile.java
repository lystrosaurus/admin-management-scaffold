package io.github.lystrosaurus.admin.file.entity;

import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 文件实体 */
@Getter
@Setter
public class SysFile extends BaseEntity {

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
}
