package io.github.lystrosaurus.admin.system.dict.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.system.dict.adapter.request.DictCreateRequest;
import io.github.lystrosaurus.admin.system.dict.adapter.request.DictUpdateRequest;
import io.github.lystrosaurus.admin.system.dict.adapter.response.DictResponse;
import io.github.lystrosaurus.admin.system.dict.application.DictApplicationService;
import jakarta.validation.Valid;
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
 * 字典控制器
 *
 * <p>提供数据字典管理相关接口
 */
@RestController
@RequestMapping("/app/system/dict")
@RequiredArgsConstructor
public class DictController {

  private final DictApplicationService dictApplicationService;

  /**
   * 查询字典列表（可按字典类型过滤）
   *
   * @param dictType 字典类型（可选）
   * @return 字典列表
   */
  @RequiresPermission("dict:view")
  @GetMapping
  public ApiResponse<PageResult<DictResponse>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String dictType) {
    PageResult<DictResponse> result = dictApplicationService.list(dictType, page, size);
    return ApiResponse.success(result);
  }

  /**
   * 根据ID获取字典
   *
   * @param id 字典ID
   * @return 字典响应
   */
  @RequiresPermission("dict:view")
  @GetMapping("/{id}")
  public ApiResponse<DictResponse> getById(@PathVariable Long id) {
    DictResponse response = dictApplicationService.getById(id);
    return ApiResponse.success(response);
  }

  /**
   * 创建字典
   *
   * @param request 字典创建请求
   * @return 字典响应
   */
  @RequiresPermission("dict:manage")
  @PostMapping
  public ApiResponse<DictResponse> create(@RequestBody @Valid DictCreateRequest request) {
    DictResponse response = dictApplicationService.create(request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 更新字典
   *
   * @param id 字典ID
   * @param request 字典更新请求
   * @return 字典响应
   */
  @RequiresPermission("dict:manage")
  @PutMapping("/{id}")
  public ApiResponse<DictResponse> update(
      @PathVariable Long id, @RequestBody DictUpdateRequest request) {
    DictResponse response = dictApplicationService.update(id, request.toCommand());
    return ApiResponse.success(response);
  }

  /**
   * 删除字典
   *
   * @param id 字典ID
   * @return 空响应
   */
  @RequiresPermission("dict:manage")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    dictApplicationService.deleteById(id);
    return ApiResponse.success();
  }
}
