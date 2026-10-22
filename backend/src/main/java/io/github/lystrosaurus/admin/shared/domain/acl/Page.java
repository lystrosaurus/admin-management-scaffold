package io.github.lystrosaurus.admin.shared.domain.acl;

import java.util.List;

/**
 * 领域分页结果。
 *
 * @param records 当前页记录
 * @param total 总记录数
 * @param pageNum 页码（从 1 开始）
 * @param pageSize 每页大小
 * @param <T> 记录类型
 */
public record Page<T>(List<T> records, long total, int pageNum, int pageSize) {}
