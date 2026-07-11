package io.github.lystrosaurus.admin.system.configitem.dto;

/**
 * 系统配置项更新DTO
 *
 * @param configKey 配置键
 * @param configValue 配置值
 * @param configType 配置类型(STRING/NUMBER/BOOLEAN/JSON)
 * @param description 描述
 * @param status 状态
 */
public record ConfigItemUpdateDTO(
    String configKey, String configValue, String configType, String description, String status) {}
