package io.github.lystrosaurus.admin.organization.employee.service;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeDAO;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeOrgDAO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeCreateDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeQueryDTO;
import io.github.lystrosaurus.admin.organization.employee.dto.EmployeeUpdateDTO;
import io.github.lystrosaurus.admin.organization.employee.entity.EmployeeOrg;
import io.github.lystrosaurus.admin.organization.employee.entity.HrEmployee;
import io.github.lystrosaurus.admin.organization.employee.mapstruct.EmployeeMapper;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeDetailVO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeOrgVO;
import io.github.lystrosaurus.admin.organization.employee.vo.EmployeeVO;
import io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO;
import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 员工服务实现 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeDAO employeeDAO;
  private final EmployeeOrgDAO employeeOrgDAO;
  private final OrgUnitDAO orgUnitDAO;
  private final EmployeeMapper employeeMapper;

  @Transactional(rollbackFor = Exception.class)
  public EmployeeVO create(EmployeeCreateDTO dto) {
    // 检查工号唯一性
    if (employeeDAO.existsByEmployeeNo(dto.employeeNo())) {
      throw new BusinessException(ErrorCode.EMPLOYEE_ALREADY_EXISTS);
    }

    // 转换并保存员工
    HrEmployee employee = employeeMapper.toEntity(dto);
    employee.setSourceType("MANUAL");
    employee.setEmploymentStatus("ACTIVE");
    employeeDAO.save(employee);

    return employeeMapper.toVO(employee);
  }

  @Transactional(rollbackFor = Exception.class)
  public EmployeeVO update(Long id, EmployeeUpdateDTO dto) {
    // 查找员工
    HrEmployee employee = employeeDAO.findById(id);
    if (employee == null) {
      throw new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }

    // 部分更新字段
    if (StringUtils.hasText(dto.name())) {
      employee.setName(dto.name());
    }
    if (dto.preferredName() != null) {
      employee.setPreferredName(dto.preferredName());
    }
    if (StringUtils.hasText(dto.mobile())) {
      employee.setMobile(dto.mobile());
    }
    if (dto.email() != null) {
      employee.setEmail(dto.email());
    }
    if (StringUtils.hasText(dto.jobTitle())) {
      employee.setJobTitle(dto.jobTitle());
    }
    if (dto.primaryOrgId() != null) {
      employee.setPrimaryOrgId(dto.primaryOrgId());
    }
    if (StringUtils.hasText(dto.employmentStatus())) {
      employee.setEmploymentStatus(dto.employmentStatus());
    }
    if (dto.entryDate() != null) {
      employee.setEntryDate(dto.entryDate());
    }
    if (dto.leaveDate() != null) {
      employee.setLeaveDate(dto.leaveDate());
    }

    employeeDAO.update(employee);
    return employeeMapper.toVO(employee);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    // 检查员工是否存在
    HrEmployee employee = employeeDAO.findById(id);
    if (employee == null) {
      throw new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }

    // 删除员工
    employeeDAO.deleteById(id);
  }

  public EmployeeDetailVO findById(Long id) {
    // 查询员工
    HrEmployee employee = employeeDAO.findById(id);
    if (employee == null) {
      throw new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }

    // 查询员工组织关联
    List<EmployeeOrg> employeeOrgs = employeeOrgDAO.findByEmployeeId(id);

    // 批量查询组织信息
    List<Long> orgIds =
        employeeOrgs.stream().map(EmployeeOrg::getOrgId).collect(Collectors.toList());
    Map<Long, OrgUnit> orgMap =
        orgUnitDAO.findByIds(orgIds).stream().collect(Collectors.toMap(OrgUnit::getId, org -> org));

    // 构建组织关联VO列表
    List<EmployeeOrgVO> orgVOs =
        employeeOrgs.stream()
            .map(
                eo -> {
                  OrgUnit org = orgMap.get(eo.getOrgId());
                  String orgName = org != null ? org.getName() : null;
                  return new EmployeeOrgVO(
                      eo.getId(),
                      eo.getOrgId(),
                      orgName,
                      eo.getIsPrimary(),
                      eo.getPositionName(),
                      eo.getStartDate(),
                      eo.getEndDate(),
                      eo.getStatus());
                })
            .collect(Collectors.toList());

    // 构建详情VO
    EmployeeDetailVO detailVO = employeeMapper.toDetailVO(employee);
    OrgUnit primaryOrg =
        detailVO.primaryOrgId() != null ? orgMap.get(detailVO.primaryOrgId()) : null;
    String orgUnitName = primaryOrg != null ? primaryOrg.getName() : null;
    return new EmployeeDetailVO(
        detailVO.id(),
        detailVO.employeeNo(),
        detailVO.name(),
        detailVO.preferredName(),
        detailVO.mobile(),
        detailVO.email(),
        detailVO.jobTitle(),
        detailVO.employmentStatus(),
        detailVO.primaryOrgId(),
        orgUnitName,
        detailVO.entryDate(),
        detailVO.leaveDate(),
        detailVO.createdAt(),
        orgVOs);
  }

  public PageResult<EmployeeVO> findPage(EmployeeQueryDTO dto, int page, int size) {
    // 查询员工列表
    List<HrEmployee> employees =
        employeeDAO.findByCondition(
            dto.keyword(), dto.employmentStatus(), dto.primaryOrgId(), page, size);
    long total =
        employeeDAO.countByCondition(dto.keyword(), dto.employmentStatus(), dto.primaryOrgId());

    // 转换为VO列表
    List<EmployeeVO> baseVOs =
        employees.stream().map(employeeMapper::toVO).collect(Collectors.toList());

    // 批量查询组织名称
    List<Long> orgIds =
        baseVOs.stream()
            .map(EmployeeVO::primaryOrgId)
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());
    Map<Long, String> orgNameMap =
        orgIds.isEmpty()
            ? Map.of()
            : orgUnitDAO.findByIds(orgIds).stream()
                .collect(Collectors.toMap(OrgUnit::getId, OrgUnit::getName));

    // 补充 orgUnitName
    List<EmployeeVO> employeeVOs =
        baseVOs.stream()
            .map(
                vo ->
                    new EmployeeVO(
                        vo.id(),
                        vo.employeeNo(),
                        vo.name(),
                        vo.preferredName(),
                        vo.mobile(),
                        vo.email(),
                        vo.jobTitle(),
                        vo.employmentStatus(),
                        vo.primaryOrgId(),
                        orgNameMap.get(vo.primaryOrgId()),
                        vo.entryDate(),
                        vo.leaveDate(),
                        vo.createdAt()))
            .collect(Collectors.toList());

    return new PageResult<>(employeeVOs, total, page, size);
  }
}
