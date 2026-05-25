package io.github.lystrosaurus.admin.organization.orgunit.service;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeOrgDAO;
import io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitCreateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitQueryDTO;
import io.github.lystrosaurus.admin.organization.orgunit.dto.OrgUnitUpdateDTO;
import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.mapstruct.OrgUnitMapper;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitTreeVO;
import io.github.lystrosaurus.admin.organization.orgunit.vo.OrgUnitVO;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 组织单元服务实现 */
@Service
@RequiredArgsConstructor
public class OrgUnitService {

  private final OrgUnitDAO orgUnitDAO;
  private final EmployeeOrgDAO employeeOrgDAO;
  private final OrgUnitMapper orgUnitMapper;

  @Transactional(rollbackFor = Exception.class)
  public OrgUnitVO create(OrgUnitCreateDTO dto) {
    // 检查编码唯一性
    if (orgUnitDAO.existsByCode(dto.code())) {
      throw new BusinessException(ErrorCode.ORG_UNIT_ALREADY_EXISTS);
    }

    // 转换并保存组织单元
    OrgUnit orgUnit = orgUnitMapper.toEntity(dto);
    orgUnit.setSourceType("MANUAL");
    orgUnit.setStatus("ENABLED");

    // 根据 parentId 计算 level 和 fullPath
    if (dto.parentId() == null || dto.parentId() == 0) {
      orgUnit.setParentId(0L);
      orgUnit.setLevel(1);
      orgUnit.setFullPath("/"); // 根节点的 fullPath 会在保存后更新
    } else {
      OrgUnit parent = orgUnitDAO.findById(dto.parentId());
      if (parent == null) {
        throw new BusinessException(ErrorCode.ORG_UNIT_NOT_FOUND);
      }
      orgUnit.setParentId(parent.getId());
      orgUnit.setLevel(parent.getLevel() + 1);
    }

    orgUnitDAO.save(orgUnit);

    // 更新 fullPath（需要 id 才能生成完整路径）
    if (orgUnit.getParentId() == 0) {
      orgUnit.setFullPath("/" + orgUnit.getId() + "/");
    } else {
      OrgUnit parent = orgUnitDAO.findById(orgUnit.getParentId());
      orgUnit.setFullPath(parent.getFullPath() + orgUnit.getId() + "/");
    }
    orgUnitDAO.update(orgUnit);

    return orgUnitMapper.toVO(orgUnit);
  }

  @Transactional(rollbackFor = Exception.class)
  public OrgUnitVO update(Long id, OrgUnitUpdateDTO dto) {
    // 查找组织单元
    OrgUnit orgUnit = orgUnitDAO.findById(id);
    if (orgUnit == null) {
      throw new BusinessException(ErrorCode.ORG_UNIT_NOT_FOUND);
    }

    // 部分更新字段
    if (StringUtils.hasText(dto.name())) {
      orgUnit.setName(dto.name());
    }
    if (dto.parentId() != null) {
      orgUnit.setParentId(dto.parentId());
    }
    if (dto.managerEmployeeId() != null) {
      orgUnit.setManagerEmployeeId(dto.managerEmployeeId());
    }
    if (dto.sortOrder() != null) {
      orgUnit.setSortOrder(dto.sortOrder());
    }
    if (StringUtils.hasText(dto.status())) {
      orgUnit.setStatus(dto.status());
    }

    orgUnitDAO.update(orgUnit);
    return orgUnitMapper.toVO(orgUnit);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    // 检查组织单元是否存在
    OrgUnit orgUnit = orgUnitDAO.findById(id);
    if (orgUnit == null) {
      throw new BusinessException(ErrorCode.ORG_UNIT_NOT_FOUND);
    }

    // 检查是否有子节点
    if (orgUnitDAO.hasChildren(id)) {
      throw new BusinessException(ErrorCode.ORG_UNIT_HAS_CHILDREN);
    }

    // 删除组织单元
    orgUnitDAO.deleteById(id);
  }

  public OrgUnitVO findById(Long id) {
    OrgUnit orgUnit = orgUnitDAO.findById(id);
    if (orgUnit == null) {
      throw new BusinessException(ErrorCode.ORG_UNIT_NOT_FOUND);
    }
    return orgUnitMapper.toVO(orgUnit);
  }

  public List<OrgUnitVO> findAll() {
    List<OrgUnit> orgUnits = orgUnitDAO.findAll();
    return orgUnits.stream().map(orgUnitMapper::toVO).toList();
  }

  public List<OrgUnitTreeVO> findTree() {
    // 查找所有组织单元
    List<OrgUnit> allOrgUnits = orgUnitDAO.findAll();

    // 转换为 TreeVO
    List<OrgUnitTreeVO> allTreeVOs =
        allOrgUnits.stream().map(orgUnitMapper::toTreeVO).toList();

    // 按 parentId 分组，避免递归时全量扫描
    Map<Long, List<OrgUnitTreeVO>> parentChildMap =
        allTreeVOs.stream().collect(Collectors.groupingBy(OrgUnitTreeVO::parentId));

    // 递归构建树
    return buildTree(parentChildMap, 0L);
  }

  /**
   * 递归构建树形结构
   *
   * @param parentChildMap 按 parentId 分组的子节点映射
   * @param parentId 父节点ID
   * @return 子节点列表
   */
  private List<OrgUnitTreeVO> buildTree(
      Map<Long, List<OrgUnitTreeVO>> parentChildMap, Long parentId) {
    List<OrgUnitTreeVO> children = parentChildMap.getOrDefault(parentId, List.of());
    return children.stream()
        .map(
            vo ->
                new OrgUnitTreeVO(
                    vo.id(),
                    vo.code(),
                    vo.name(),
                    vo.parentId(),
                    vo.level(),
                    vo.managerEmployeeId(),
                    vo.sortOrder(),
                    vo.status(),
                    buildTree(parentChildMap, vo.id())))
        .toList();
  }

  public PageResult<OrgUnitVO> findPage(OrgUnitQueryDTO dto, int page, int size) {
    // 查询组织单元列表
    List<OrgUnit> orgUnits =
        orgUnitDAO.findByCondition(dto.keyword(), dto.status(), dto.parentId(), page, size);
    long total = orgUnitDAO.countByCondition(dto.keyword(), dto.status(), dto.parentId());

    // 转换为VO列表
    List<OrgUnitVO> orgUnitVOs =
        orgUnits.stream().map(orgUnitMapper::toVO).toList();

    return new PageResult<>(orgUnitVOs, total, page, size);
  }
}
