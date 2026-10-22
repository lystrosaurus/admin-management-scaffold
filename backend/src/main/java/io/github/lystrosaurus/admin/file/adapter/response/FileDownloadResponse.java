package io.github.lystrosaurus.admin.file.adapter.response;

import java.nio.file.Path;

/**
 * 文件下载载体
 *
 * <p>应用服务返回此对象，由 Controller 构建 {@code ResponseEntity<InputStreamResource>} 输出文件流。
 *
 * @param path 物理文件绝对路径
 * @param originalName 下载时的文件名
 * @param mimeType MIME类型
 * @param sizeBytes 文件大小(字节)
 */
public record FileDownloadResponse(
    Path path, String originalName, String mimeType, long sizeBytes) {}
