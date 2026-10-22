package io.github.lystrosaurus.admin.system.configitem.adapter.response;

/**
 * 配置项响应（字段集合与原 ConfigItemVO 完全一致，保证 API 契约不变）。
 *
 * @param id 主键ID
 * @param configKey 配置键
 * @param configValue 配置值
 * @param configType 配置类型(STRING/NUMBER/BOOLEAN/JSON)
 * @param description 描述
 * @param status 状态
 */
public record ConfigItemResponse(
    Long id,
    String configKey,
    String configValue,
    String configType,
    String description,
    String status) {}
