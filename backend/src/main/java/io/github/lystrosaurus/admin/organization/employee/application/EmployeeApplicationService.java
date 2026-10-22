package io.github.lystrosaurus.admin.organization.employee.application;

import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeDetailResponse;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeOrgResponse;
import io.github.lystrosaurus.admin.organization.employee.adapter.response.EmployeeResponse;
import io.github.lystrosaurus.admin.organization.employee.application.assembler.EmployeeAssembler;
import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeCreateCommand;
import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeQuery;
import io.github.lystrosaurus.admin.organization.employee.application.command.EmployeeUpdateCommand;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryRepository;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeRepository;
import io.github.lystrosaurus.admin.organization.employee.domain.model.Employee;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeErrorCode;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeId;
import io.github.lystrosaurus.admin.organization.employee.domain.model.EmployeeOrgAssignment;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryPort;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRepository;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 员工应用服务：编排员工聚合的创建、更新、删除与查询。 */
@Service
@RequiredArgsConstructor
public class EmployeeApplicationService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeQueryRepository employeeQueryRepository;
  private final OrgUnitQueryPort orgUnitQueryPort;
  private final UserRepository userRepository;
  private final IdGenerator idGenerator;
  private final EmployeeAssembler assembler;

  /** 创建员工 */
  @Transactional(rollbackFor = Exception.class)
  public EmployeeResponse create(EmployeeCreateCommand command) {
    if (employeeRepository.existsByEmployeeNo(command.employeeNo())) {
      throw new DomainException(EmployeeErrorCode.EMPLOYEE_ALREADY_EXISTS);
    }

    Employee employee =
        Employee.create(
            EmployeeId.of(idGenerator.nextId()),
            command.employeeNo(),
            command.name(),
            command.mobile(),
            command.email(),
            command.jobTitle(),
            command.primaryOrgId(),
            command.entryDate(),
            command.userId());
    employeeRepository.save(employee);

    // 重新加载以回读数据库填充的审计字段（createdAt 等），保持响应契约与旧实现一致
    Employee saved = employeeRepository.findById(employee.id()).orElseThrow();
    return assembler.toResponse(saved, null, resolveUsername(saved.userId()));
  }

  /** 更新员工（部分更新：仅非空文本字段生效） */
  @Transactional(rollbackFor = Exception.class)
  public EmployeeResponse update(Long id, EmployeeUpdateCommand command) {
    Employee employee = findOrThrow(id);
    employee.update(
        command.name(),
        command.preferredName(),
        command.mobile(),
        command.email(),
        command.jobTitle(),
        command.primaryOrgId(),
        command.employmentStatus(),
        command.entryDate(),
        command.leaveDate(),
        command.userId());
    employeeRepository.save(employee);
    return assembler.toResponse(employee, null, resolveUsername(employee.userId()));
  }

  /** 删除员工 */
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    findOrThrow(id);
    employeeRepository.deleteById(EmployeeId.of(id));
  }

  /** 查询员工详情（含组织关联） */
  public EmployeeDetailResponse findById(Long id) {
    Employee employee = findOrThrow(id);
    List<EmployeeOrgAssignment> assignments =
        employeeQueryRepository.findOrgsByEmployeeId(employee.id().value());

    List<Long> orgIds =
        assignments.stream().map(EmployeeOrgAssignment::orgId).distinct().toList();
    Map<Long, String> orgNameMap = orgUnitQueryPort.findNamesByIds(orgIds);

    List<EmployeeOrgResponse> orgs =
        assignments.stream()
            .map(
                assignment ->
                    new EmployeeOrgResponse(
                        assignment.id(),
                        assignment.orgId(),
                        orgNameMap.get(assignment.orgId()),
                        assignment.isPrimary(),
                        assignment.positionName(),
                        assignment.startDate(),
                        assignment.endDate(),
                        assignment.status()))
            .toList();

    String orgUnitName =
        employee.primaryOrgId() != null ? orgNameMap.get(employee.primaryOrgId()) : null;
    return assembler.toDetailResponse(
        employee, orgUnitName, orgs, resolveUsername(employee.userId()));
  }

  /** 分页查询员工 */
  public PageResult<EmployeeResponse> findPage(EmployeeQuery query, int page, int size) {
    Page<Employee> result =
        employeeQueryRepository.findPage(
            query.keyword(), query.employmentStatus(), query.primaryOrgId(), PageQuery.of(page, size));

    List<Long> orgIds =
        result.records().stream()
            .map(Employee::primaryOrgId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    Map<Long, String> orgNameMap = orgUnitQueryPort.findNamesByIds(orgIds);

    List<Long> userIds =
        result.records().stream()
            .map(Employee::userId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    Map<Long, String> usernameMap =
        userIds.isEmpty()
            ? Map.of()
            : userRepository.findByIds(userIds).stream()
                .collect(Collectors.toMap(user -> user.id().value(), User::username));

    List<EmployeeResponse> responses =
        result.records().stream()
            .map(
                employee ->
                    assembler.toResponse(
                        employee,
                        orgNameMap.get(employee.primaryOrgId()),
                        usernameMap.get(employee.userId())))
            .toList();

    return new PageResult<>(responses, result.total(), result.pageNum(), result.pageSize());
  }

  /** 根据 userId 解析关联用户名，userId 为空或用户不存在时返回 null */
  private String resolveUsername(Long userId) {
    if (userId == null) {
      return null;
    }
    return userRepository.findById(UserId.of(userId)).map(User::username).orElse(null);
  }

  private Employee findOrThrow(Long id) {
    return employeeRepository
        .findById(EmployeeId.of(id))
        .orElseThrow(() -> new DomainException(EmployeeErrorCode.EMPLOYEE_NOT_FOUND));
  }
}
