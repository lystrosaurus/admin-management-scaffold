package io.github.lystrosaurus.admin.system.configitem.controller;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.configitem.dto.ConfigItemCreateDTO;
import io.github.lystrosaurus.admin.system.configitem.dto.ConfigItemUpdateDTO;
import io.github.lystrosaurus.admin.system.configitem.service.ConfigItemService;
import io.github.lystrosaurus.admin.system.configitem.vo.ConfigItemVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置项控制器
 *
 * <p>提供系统配置项管理相关接口
 */
@RestController
@RequestMapping("/app/system/config-item")
@RequiredArgsConstructor
public class ConfigItemController {

  private final ConfigItemService configItemService;

  /**
   * 查询配置项列表
   *
   * @return 配置项列表
   */
  @RequiresPermission("config:view")
  @GetMapping
  public ApiResponse<List<ConfigItemVO>> list() {
    List<ConfigItemVO> configItems = configItemService.findAll();
    return ApiResponse.success(configItems);
  }

  /**
   * 根据ID获取配置项
   *
   * @param id 配置项ID
   * @return 配置项VO
   */
  @RequiresPermission("config:view")
  @GetMapping("/{id}")
  public ApiResponse<ConfigItemVO> getById(@PathVariable Long id) {
    ConfigItemVO configItemVO = configItemService.getById(id);
    return ApiResponse.success(configItemVO);
  }

  /**
   * 创建配置项
   *
   * @param dto 配置项创建DTO
   * @return 配置项VO
   */
  @RequiresPermission("config:manage")
  @PostMapping
  public ApiResponse<ConfigItemVO> create(@RequestBody @Valid ConfigItemCreateDTO dto) {
    ConfigItemVO configItemVO = configItemService.create(dto);
    return ApiResponse.success(configItemVO);
  }

  /**
   * 更新配置项
   *
   * @param id 配置项ID
   * @param dto 配置项更新DTO
   * @return 配置项VO
   */
  @RequiresPermission("config:manage")
  @PutMapping("/{id}")
  public ApiResponse<ConfigItemVO> update(
      @PathVariable Long id, @RequestBody ConfigItemUpdateDTO dto) {
    ConfigItemVO configItemVO = configItemService.update(id, dto);
    return ApiResponse.success(configItemVO);
  }

  /**
   * 删除配置项
   *
   * @param id 配置项ID
   * @return 空响应
   */
  @RequiresPermission("config:manage")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    configItemService.deleteById(id);
    return ApiResponse.success();
  }
}
