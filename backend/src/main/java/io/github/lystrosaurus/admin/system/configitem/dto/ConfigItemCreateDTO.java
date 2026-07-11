package io.github.lystrosaurus.admin.system.configitem.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 系统配置项创建DTO
 *
 * @param configKey 配置键
 * @param configValue 配置值
 * @param configType 配置类型(STRING/NUMBER/BOOLEAN/JSON)
 * @param description 描述
 * @param status 状态
 */
public record ConfigItemCreateDTO(
    @NotBlank(message = "配置键不能为空") String configKey,
    String configValue,
    String configType,
    String description,
    String status) {}
