package io.github.lystrosaurus.admin.common;

import java.util.List;

/**
 * 分页结果封装类
 *
 * @param items 数据列表
 * @param total 总记录数
 * @param page 当前页码
 * @param size 每页大小
 */
public record PageResult<T>(List<T> items, long total, int page, int size) {}
