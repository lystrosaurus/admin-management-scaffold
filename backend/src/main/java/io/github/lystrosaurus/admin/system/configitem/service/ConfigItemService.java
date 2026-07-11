package io.github.lystrosaurus.admin.system.configitem.service;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.configitem.dao.ConfigItemDAO;
import io.github.lystrosaurus.admin.system.configitem.dto.ConfigItemCreateDTO;
import io.github.lystrosaurus.admin.system.configitem.dto.ConfigItemUpdateDTO;
import io.github.lystrosaurus.admin.system.configitem.entity.SysConfigItem;
import io.github.lystrosaurus.admin.system.configitem.mapstruct.ConfigItemMapper;
import io.github.lystrosaurus.admin.system.configitem.vo.ConfigItemVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 系统配置项服务实现 */
@Service
@RequiredArgsConstructor
public class ConfigItemService {

  private final ConfigItemDAO configItemDAO;
  private final ConfigItemMapper configItemMapper;

  @Transactional(rollbackFor = Exception.class)
  public ConfigItemVO create(ConfigItemCreateDTO dto) {
    // 检查配置键唯一性
    if (configItemDAO.existsByConfigKey(dto.configKey())) {
      throw new BusinessException(ErrorCode.DATA_DUPLICATE_KEY);
    }

    // 转换并保存配置项
    SysConfigItem configItem = new SysConfigItem();
    configItem.setConfigKey(dto.configKey());
    configItem.setConfigValue(dto.configValue());
    configItem.setConfigType(StringUtils.hasText(dto.configType()) ? dto.configType() : "STRING");
    configItem.setDescription(dto.description());
    configItem.setStatus(StringUtils.hasText(dto.status()) ? dto.status() : "ENABLED");
    configItemDAO.save(configItem);

    return configItemMapper.toConfigItemVO(configItem);
  }

  @Transactional(rollbackFor = Exception.class)
  public ConfigItemVO update(Long id, ConfigItemUpdateDTO dto) {
    // 查找配置项
    SysConfigItem configItem = configItemDAO.findById(id);
    if (configItem == null) {
      throw new BusinessException(ErrorCode.SYSTEM_404);
    }

    // 检查配置键唯一性（排除自身）
    if (StringUtils.hasText(dto.configKey())
        && !dto.configKey().equals(configItem.getConfigKey())) {
      if (configItemDAO.existsByConfigKeyAndIdNot(dto.configKey(), id)) {
        throw new BusinessException(ErrorCode.DATA_DUPLICATE_KEY);
      }
      configItem.setConfigKey(dto.configKey());
    }

    // 更新字段
    if (StringUtils.hasText(dto.configValue())) {
      configItem.setConfigValue(dto.configValue());
    }
    if (StringUtils.hasText(dto.configType())) {
      configItem.setConfigType(dto.configType());
    }
    if (StringUtils.hasText(dto.description())) {
      configItem.setDescription(dto.description());
    }
    if (StringUtils.hasText(dto.status())) {
      configItem.setStatus(dto.status());
    }

    configItemDAO.update(configItem);
    return configItemMapper.toConfigItemVO(configItem);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    configItemDAO.deleteById(id);
  }

  public List<ConfigItemVO> findAll() {
    List<SysConfigItem> configItems = configItemDAO.findAll();
    return configItems.stream().map(configItemMapper::toConfigItemVO).toList();
  }

  public ConfigItemVO getById(Long id) {
    SysConfigItem configItem = configItemDAO.findById(id);
    if (configItem == null) {
      throw new BusinessException(ErrorCode.SYSTEM_404);
    }
    return configItemMapper.toConfigItemVO(configItem);
  }
}
