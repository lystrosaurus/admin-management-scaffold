package io.github.lystrosaurus.admin.auth.log.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 登录日志查询DTO
 *
 * @param username 用户名(模糊，可选)
 * @param ipAddress IP地址(模糊，可选)
 * @param status 登录状态(可选)
 * @param start 起始时间(可选)
 * @param end 结束时间(可选)
 * @param page 页码
 * @param size 每页大小
 */
public record LoginLogQueryDTO(
    String username,
    String ipAddress,
    String status,
    LocalDateTime start,
    LocalDateTime end,
    @Min(1) int page,
    @Min(1) @Max(100) int size) {}
