package io.github.lystrosaurus.admin.file.application.command;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * 文件上传命令。
 *
 * @param originalName 原始文件名
 * @param contentType MIME类型
 * @param size 文件大小(字节)
 * @param content 文件内容提供者（可多次获取流）
 */
public record FileUploadCommand(
    String originalName, String contentType, long size, Supplier<InputStream> content) {}
