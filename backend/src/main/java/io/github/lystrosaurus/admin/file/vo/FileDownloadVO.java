package io.github.lystrosaurus.admin.file.vo;

import java.nio.file.Path;

/**
 * 文件下载载体
 *
 * <p>Service 返回此对象，由 Controller 构建 {@code ResponseEntity<InputStreamResource>} 输出文件流。 不返回
 * Entity，符合架构约束。
 *
 * @param path 物理文件绝对路径
 * @param originalName 下载时的文件名
 * @param mimeType MIME类型
 * @param sizeBytes 文件大小(字节)
 */
public record FileDownloadVO(Path path, String originalName, String mimeType, long sizeBytes) {}
