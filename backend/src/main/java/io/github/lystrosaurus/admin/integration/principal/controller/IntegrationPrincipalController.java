package io.github.lystrosaurus.admin.integration.principal.controller;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalCreateDTO;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalUpdateDTO;
import io.github.lystrosaurus.admin.integration.principal.service.ExtPrincipalService;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalVO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部主体控制器
 *
 * <p>提供外部主体管理相关接口
 */
@RestController
@RequestMapping("/app/integration/principals")
@RequiredArgsConstructor
public class IntegrationPrincipalController {

  private final ExtPrincipalService extPrincipalService;

  /**
   * 创建外部主体
   *
   * @param dto 创建DTO
   * @return 外部主体VO
   */
  @PostMapping
  public ApiResponse<ExtPrincipalVO> create(@RequestBody @Valid ExtPrincipalCreateDTO dto) {
    ExtPrincipalVO principalVO = extPrincipalService.create(dto);
    return ApiResponse.success(principalVO);
  }

  /**
   * 获取外部主体详情
   *
   * @param id 外部主体ID
   * @return 外部主体VO
   */
  @GetMapping("/{id}")
  public ApiResponse<ExtPrincipalVO> findById(@PathVariable Long id) {
    ExtPrincipalVO principalVO = extPrincipalService.getById(id);
    return ApiResponse.success(principalVO);
  }

  /**
   * 更新外部主体
   *
   * @param id 外部主体ID
   * @param dto 更新DTO
   * @return 外部主体VO
   */
  @PutMapping("/{id}")
  public ApiResponse<ExtPrincipalVO> update(
      @PathVariable Long id, @RequestBody ExtPrincipalUpdateDTO dto) {
    ExtPrincipalVO principalVO = extPrincipalService.update(id, dto);
    return ApiResponse.success(principalVO);
  }

  /**
   * 删除外部主体
   *
   * @param id 外部主体ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    extPrincipalService.delete(id);
    return ApiResponse.success();
  }

  /**
   * 查询外部主体列表
   *
   * @param sourceId 外部系统ID（可选）
   * @param linkStatus 关联状态（可选）
   * @return 外部主体列表
   */
  @GetMapping
  public ApiResponse<List<ExtPrincipalVO>> list(
      @RequestParam(required = false) Long sourceId,
      @RequestParam(required = false) String linkStatus) {
    List<ExtPrincipalVO> principals = extPrincipalService.list(sourceId, linkStatus);
    return ApiResponse.success(principals);
  }
}
