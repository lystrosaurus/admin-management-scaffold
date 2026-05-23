package io.github.lystrosaurus.admin.organization.orgunit.service;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitCreateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitQueryDTO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitUpdateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitTreeVO;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitVO;
import java.util.List;

/** 组织单元服务接口 */
public interface OrgUnitService {

  /**
   * 创建组织单元
   *
   * @param dto 组织单元创建DTO
   * @return 组织单元VO
   */
  OrgUnitVO create(OrgUnitCreateDTO dto);

  /**
   * 更新组织单元
   *
   * @param id 组织单元ID
   * @param dto 组织单元更新DTO
   * @return 组织单元VO
   */
  OrgUnitVO update(Long id, OrgUnitUpdateDTO dto);

  /**
   * 删除组织单元
   *
   * @param id 组织单元ID
   */
  void deleteById(Long id);

  /**
   * 查询组织单元详情
   *
   * @param id 组织单元ID
   * @return 组织单元VO
   */
  OrgUnitVO findById(Long id);

  /**
   * 查询所有组织单元
   *
   * @return 组织单元VO列表
   */
  List<OrgUnitVO> findAll();

  /**
   * 构建组织架构树
   *
   * @return 组织架构树
   */
  List<OrgUnitTreeVO> findTree();

  /**
   * 分页查询组织单元
   *
   * @param dto 查询条件
   * @param page 页码
   * @param size 每页大小
   * @return 分页结果
   */
  PageResult<OrgUnitVO> findPage(OrgUnitQueryDTO dto, int page, int size);
}
