package io.github.lystrosaurus.admin.organization.orgunit.controller;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitCreateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitQueryDTO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitUpdateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.service.OrgUnitService;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitTreeVO;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitVO;
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
 * 组织单元控制器
 *
 * <p>提供组织单元管理相关接口
 */
@RestController
@RequestMapping("/app/org-units")
@RequiredArgsConstructor
public class OrgUnitController {

  private final OrgUnitService orgUnitService;

  /**
   * 创建组织单元
   *
   * @param dto 组织单元创建DTO
   * @return 组织单元VO
   */
  @PostMapping
  public ApiResponse<OrgUnitVO> create(@RequestBody @Valid OrgUnitCreateDTO dto) {
    OrgUnitVO orgUnitVO = orgUnitService.create(dto);
    return ApiResponse.success(orgUnitVO);
  }

  /**
   * 获取组织单元详情
   *
   * @param id 组织单元ID
   * @return 组织单元VO
   */
  @GetMapping("/{id}")
  public ApiResponse<OrgUnitVO> findById(@PathVariable Long id) {
    OrgUnitVO orgUnitVO = orgUnitService.findById(id);
    return ApiResponse.success(orgUnitVO);
  }

  /**
   * 更新组织单元
   *
   * @param id 组织单元ID
   * @param dto 组织单元更新DTO
   * @return 组织单元VO
   */
  @PutMapping("/{id}")
  public ApiResponse<OrgUnitVO> update(@PathVariable Long id, @RequestBody OrgUnitUpdateDTO dto) {
    OrgUnitVO orgUnitVO = orgUnitService.update(id, dto);
    return ApiResponse.success(orgUnitVO);
  }

  /**
   * 删除组织单元
   *
   * @param id 组织单元ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    orgUnitService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 分页查询组织单元
   *
   * @param page 页码
   * @param size 每页大小
   * @param keyword 关键词
   * @param status 状态
   * @param parentId 父节点ID
   * @return 分页结果
   */
  @GetMapping
  public ApiResponse<PageResult<OrgUnitVO>> findPage(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Long parentId) {
    OrgUnitQueryDTO queryDTO = new OrgUnitQueryDTO(keyword, status, parentId);
    PageResult<OrgUnitVO> pageResult = orgUnitService.findPage(queryDTO, page, size);
    return ApiResponse.success(pageResult);
  }

  /**
   * 获取组织架构树
   *
   * @return 组织架构树
   */
  @GetMapping("/tree")
  public ApiResponse<List<OrgUnitTreeVO>> findTree() {
    List<OrgUnitTreeVO> tree = orgUnitService.findTree();
    return ApiResponse.success(tree);
  }
}
