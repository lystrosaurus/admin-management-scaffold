package io.github.lystrosaurus.admin.file.domain.model;

import java.time.LocalDateTime;

/**
 * 文件元数据领域对象。
 *
 * <p>对应 sys_file 表；零框架注解，字段与持久化对象逐字段一致。
 *
 * @param id 主键ID
 * @param originalName 原始文件名
 * @param storedName 存储文件名(uuid)
 * @param path 存储相对路径
 * @param ext 扩展名(不含点)
 * @param mimeType MIME类型
 * @param sizeBytes 文件大小(字节)
 * @param md5 文件MD5
 * @param uploaderId 上传用户ID
 * @param bizType 业务类型
 * @param status 状态(NORMAL/DELETED)
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record SysFile(
    Long id,
    String originalName,
    String storedName,
    String path,
    String ext,
    String mimeType,
    Long sizeBytes,
    String md5,
    Long uploaderId,
    String bizType,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
