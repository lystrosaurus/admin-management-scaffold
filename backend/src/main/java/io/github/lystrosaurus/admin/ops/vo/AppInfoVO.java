package io.github.lystrosaurus.admin.ops.vo;

/**
 * 应用信息 VO
 *
 * @param appName 应用名称
 * @param version 应用版本
 * @param javaVersion Java 运行时版本
 */
public record AppInfoVO(String appName, String version, String javaVersion) {}
