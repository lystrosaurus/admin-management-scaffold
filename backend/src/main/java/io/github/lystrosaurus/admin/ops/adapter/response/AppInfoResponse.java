package io.github.lystrosaurus.admin.ops.adapter.response;

/**
 * 应用信息响应
 *
 * @param name 应用名称
 * @param version 应用版本
 * @param javaVersion Java 运行时版本
 * @param status 运行状态（UP/DOWN）
 * @param osName 操作系统名称
 * @param osArch 操作系统架构
 * @param profile 运行 profile（dev/prod ...）
 * @param startTime 应用启动时间（ISO 本地时间字符串）
 */
public record AppInfoResponse(
    String name,
    String version,
    String javaVersion,
    String status,
    String osName,
    String osArch,
    String profile,
    String startTime) {}
