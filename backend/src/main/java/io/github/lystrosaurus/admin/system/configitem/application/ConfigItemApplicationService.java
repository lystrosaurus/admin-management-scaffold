package io.github.lystrosaurus.admin.system.configitem.application;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.configitem.adapter.response.ConfigItemResponse;
import io.github.lystrosaurus.admin.system.configitem.application.assembler.ConfigItemAssembler;
import io.github.lystrosaurus.admin.system.configitem.application.command.ConfigItemCreateCommand;
import io.github.lystrosaurus.admin.system.configitem.application.command.ConfigItemUpdateCommand;
import io.github.lystrosaurus.admin.system.configitem.domain.acl.ConfigItemQueryRepository;
import io.github.lystrosaurus.admin.system.configitem.domain.acl.ConfigItemRepository;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItem;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItemErrorCode;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItemId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 配置项应用服务：编排配置项聚合的创建、更新、删除与查询。 */
@Service
@RequiredArgsConstructor
public class ConfigItemApplicationService {

  private final ConfigItemRepository configItemRepository;
  private final ConfigItemQueryRepository configItemQueryRepository;
  private final IdGenerator idGenerator;
  private final ConfigItemAssembler assembler;

  /** 创建配置项 */
  @Transactional(rollbackFor = Exception.class)
  public ConfigItemResponse create(ConfigItemCreateCommand command) {
    if (configItemRepository.existsByConfigKey(command.configKey())) {
      throw new DomainException(ConfigItemErrorCode.CONFIG_ITEM_DUPLICATE_KEY);
    }
    ConfigItem configItem =
        ConfigItem.create(
            ConfigItemId.of(idGenerator.nextId()),
            command.configKey(),
            command.configValue(),
            command.configType(),
            command.description(),
            command.status());
    configItemRepository.save(configItem);
    return assembler.toResponse(configItem);
  }

  /** 更新配置项 */
  @Transactional(rollbackFor = Exception.class)
  public ConfigItemResponse update(Long id, ConfigItemUpdateCommand command) {
    ConfigItem configItem = findConfigItem(id);
    if (StringUtils.hasText(command.configKey())
        && !command.configKey().equals(configItem.configKey())
        && configItemRepository.existsByConfigKeyAndIdNot(
            command.configKey(), ConfigItemId.of(id))) {
      throw new DomainException(ConfigItemErrorCode.CONFIG_ITEM_DUPLICATE_KEY);
    }
    configItem.update(
        command.configKey(),
        command.configValue(),
        command.configType(),
        command.description(),
        command.status());
    configItemRepository.save(configItem);
    return assembler.toResponse(configItem);
  }

  /** 删除配置项 */
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    configItemRepository.deleteById(ConfigItemId.of(id));
  }

  /** 分页查询配置项（可按配置键模糊过滤） */
  public PageResult<ConfigItemResponse> list(String configKey, int page, int size) {
    Page<ConfigItem> result =
        configItemQueryRepository.findPage(configKey, PageQuery.of(page, size));
    List<ConfigItemResponse> responses =
        result.records().stream().map(assembler::toResponse).toList();
    return new PageResult<>(responses, result.total(), result.pageNum(), result.pageSize());
  }

  /** 查询配置项详情 */
  public ConfigItemResponse getById(Long id) {
    return assembler.toResponse(findConfigItem(id));
  }

  private ConfigItem findConfigItem(Long id) {
    return configItemRepository
        .findById(ConfigItemId.of(id))
        .orElseThrow(() -> new DomainException(ConfigItemErrorCode.CONFIG_ITEM_NOT_FOUND));
  }
}
