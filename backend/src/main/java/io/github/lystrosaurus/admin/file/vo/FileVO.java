package io.github.lystrosaurus.admin.file.vo;

import java.time.LocalDateTime;

/**
 * 文件VO
 *
 * @param id 主键ID
 * @param originalName 原始文件名
 * @param storedName 存储文件名
 * @param ext 扩展名
 * @param mimeType MIME类型
 * @param sizeBytes 文件大小(字节)
 * @param md5 文件MD5
 * @param uploaderId 上传用户ID
 * @param bizType 业务类型
 * @param status 状态
 * @param createdAt 创建时间
 */
public record FileVO(
    Long id,
    String originalName,
    String storedName,
    String ext,
    String mimeType,
    Long sizeBytes,
    String md5,
    Long uploaderId,
    String bizType,
    String status,
    LocalDateTime createdAt) {}
