package io.github.lystrosaurus.admin.file.adapter.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 文件查询请求
 *
 * @param filename 原始文件名(模糊)
 * @param bizType 业务类型
 * @param page 页码
 * @param size 每页大小
 */
public record FileQueryRequest(
    String filename, String bizType, @Min(1) int page, @Min(1) @Max(100) int size) {}
