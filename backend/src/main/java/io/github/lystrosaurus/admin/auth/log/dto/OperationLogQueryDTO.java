package io.github.lystrosaurus.admin.auth.log.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 操作日志查询DTO
 *
 * @param username 用户名(模糊，可选)
 * @param module 目标类型/模块(可选)
 * @param action 操作类型/动作(可选)
 * @param start 起始时间(可选)
 * @param end 结束时间(可选)
 * @param page 页码
 * @param size 每页大小
 */
public record OperationLogQueryDTO(
    String username,
    String module,
    String action,
    LocalDateTime start,
    LocalDateTime end,
    @Min(1) int page,
    @Min(1) @Max(100) int size) {}
