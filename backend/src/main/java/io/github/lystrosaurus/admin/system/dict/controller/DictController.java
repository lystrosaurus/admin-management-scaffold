package io.github.lystrosaurus.admin.system.dict.controller;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.system.dict.dto.DictCreateDTO;
import io.github.lystrosaurus.admin.system.dict.dto.DictUpdateDTO;
import io.github.lystrosaurus.admin.system.dict.service.DictService;
import io.github.lystrosaurus.admin.system.dict.vo.DictVO;
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
 * 字典控制器
 *
 * <p>提供数据字典管理相关接口
 */
@RestController
@RequestMapping("/app/system/dict")
@RequiredArgsConstructor
public class DictController {

  private final DictService dictService;

  /**
   * 查询字典列表（可按字典类型过滤）
   *
   * @param dictType 字典类型（可选）
   * @return 字典列表
   */
  @RequiresPermission("dict:view")
  @GetMapping
  public ApiResponse<List<DictVO>> list(@RequestParam(required = false) String dictType) {
    List<DictVO> dicts = dictService.list(dictType);
    return ApiResponse.success(dicts);
  }

  /**
   * 根据ID获取字典
   *
   * @param id 字典ID
   * @return 字典VO
   */
  @RequiresPermission("dict:view")
  @GetMapping("/{id}")
  public ApiResponse<DictVO> getById(@PathVariable Long id) {
    DictVO dictVO = dictService.getById(id);
    return ApiResponse.success(dictVO);
  }

  /**
   * 创建字典
   *
   * @param dto 字典创建DTO
   * @return 字典VO
   */
  @RequiresPermission("dict:manage")
  @PostMapping
  public ApiResponse<DictVO> create(@RequestBody @Valid DictCreateDTO dto) {
    DictVO dictVO = dictService.create(dto);
    return ApiResponse.success(dictVO);
  }

  /**
   * 更新字典
   *
   * @param id 字典ID
   * @param dto 字典更新DTO
   * @return 字典VO
   */
  @RequiresPermission("dict:manage")
  @PutMapping("/{id}")
  public ApiResponse<DictVO> update(@PathVariable Long id, @RequestBody DictUpdateDTO dto) {
    DictVO dictVO = dictService.update(id, dto);
    return ApiResponse.success(dictVO);
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
    dictService.deleteById(id);
    return ApiResponse.success();
  }
}
