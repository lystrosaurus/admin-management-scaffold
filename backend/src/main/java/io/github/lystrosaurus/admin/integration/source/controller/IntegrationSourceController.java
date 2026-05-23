package io.github.lystrosaurus.admin.integration.source.controller;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceCreateDTO;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceUpdateDTO;
import io.github.lystrosaurus.admin.integration.source.service.ExtSourceService;
import io.github.lystrosaurus.admin.integration.source.vo.ExtSourceVO;
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
 * 外部身份源控制器
 *
 * <p>提供外部身份源管理相关接口
 */
@RestController
@RequestMapping("/app/integration/sources")
@RequiredArgsConstructor
public class IntegrationSourceController {

  private final ExtSourceService extSourceService;

  /**
   * 创建外部身份源
   *
   * @param dto 创建DTO
   * @return 外部身份源VO
   */
  @PostMapping
  public ApiResponse<ExtSourceVO> create(@RequestBody @Valid ExtSourceCreateDTO dto) {
    ExtSourceVO sourceVO = extSourceService.create(dto);
    return ApiResponse.success(sourceVO);
  }

  /**
   * 获取外部身份源详情
   *
   * @param id 外部身份源ID
   * @return 外部身份源VO
   */
  @GetMapping("/{id}")
  public ApiResponse<ExtSourceVO> findById(@PathVariable Long id) {
    ExtSourceVO sourceVO = extSourceService.getById(id);
    return ApiResponse.success(sourceVO);
  }

  /**
   * 更新外部身份源
   *
   * @param id 外部身份源ID
   * @param dto 更新DTO
   * @return 外部身份源VO
   */
  @PutMapping("/{id}")
  public ApiResponse<ExtSourceVO> update(
      @PathVariable Long id, @RequestBody ExtSourceUpdateDTO dto) {
    ExtSourceVO sourceVO = extSourceService.update(id, dto);
    return ApiResponse.success(sourceVO);
  }

  /**
   * 删除外部身份源
   *
   * @param id 外部身份源ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    extSourceService.delete(id);
    return ApiResponse.success();
  }

  /**
   * 查询所有外部身份源
   *
   * @return 外部身份源列表
   */
  @GetMapping
  public ApiResponse<List<ExtSourceVO>> list() {
    List<ExtSourceVO> sources = extSourceService.list();
    return ApiResponse.success(sources);
  }
}
