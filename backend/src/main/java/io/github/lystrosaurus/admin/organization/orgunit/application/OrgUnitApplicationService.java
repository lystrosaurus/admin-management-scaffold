package io.github.lystrosaurus.admin.organization.orgunit.application;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryPort;
import io.github.lystrosaurus.admin.organization.orgunit.adapter.response.OrgUnitResponse;
import io.github.lystrosaurus.admin.organization.orgunit.adapter.response.OrgUnitTreeResponse;
import io.github.lystrosaurus.admin.organization.orgunit.application.assembler.OrgUnitAssembler;
import io.github.lystrosaurus.admin.organization.orgunit.application.command.OrgUnitCreateCommand;
import io.github.lystrosaurus.admin.organization.orgunit.application.command.OrgUnitQuery;
import io.github.lystrosaurus.admin.organization.orgunit.application.command.OrgUnitUpdateCommand;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryRepository;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitRepository;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitErrorCode;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitId;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 组织单元应用服务：编排组织单元聚合的创建、更新、删除与查询。 */
@Service
@RequiredArgsConstructor
public class OrgUnitApplicationService {

  private final OrgUnitRepository orgUnitRepository;
  private final OrgUnitQueryRepository orgUnitQueryRepository;
  private final EmployeeQueryPort employeeQueryPort;
  private final IdGenerator idGenerator;
  private final OrgUnitAssembler assembler;

  /** 创建组织单元 */
  @Transactional(rollbackFor = Exception.class)
  public OrgUnitResponse create(OrgUnitCreateCommand command) {
    if (orgUnitRepository.existsByCode(command.code())) {
      throw new DomainException(OrgUnitErrorCode.ORG_UNIT_ALREADY_EXISTS);
    }

    OrgUnitId id = OrgUnitId.of(idGenerator.nextId());
    boolean root = command.parentId() == null || command.parentId() == 0;
    Long parentId = 0L;
    Integer level = 1;
    String fullPath;
    if (root) {
      fullPath = "/" + id.value() + "/";
    } else {
      OrgUnit parent = findOrThrow(command.parentId());
      parentId = parent.id().value();
      level = parent.level() + 1;
      fullPath = parent.fullPath() + id.value() + "/";
    }

    OrgUnit orgUnit =
        OrgUnit.create(
            id,
            parentId,
            command.code(),
            command.name(),
            fullPath,
            level,
            command.managerEmployeeId(),
            command.sortOrder());
    orgUnitRepository.save(orgUnit);

    // 重新加载以回读数据库填充的审计字段（createdAt 等），保持响应契约与旧实现一致
    OrgUnit saved = orgUnitRepository.findById(orgUnit.id()).orElseThrow();
    return assembler.toResponse(saved, resolveLeaderName(saved));
  }

  /** 更新组织单元（部分更新：仅非空文本字段生效） */
  @Transactional(rollbackFor = Exception.class)
  public OrgUnitResponse update(Long id, OrgUnitUpdateCommand command) {
    OrgUnit orgUnit = findOrThrow(id);
    orgUnit.update(
        command.name(),
        command.parentId(),
        command.managerEmployeeId(),
        command.sortOrder(),
        command.status());
    orgUnitRepository.save(orgUnit);
    return assembler.toResponse(orgUnit, resolveLeaderName(orgUnit));
  }

  /** 删除组织单元 */
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    findOrThrow(id);
    if (orgUnitRepository.hasChildren(id)) {
      throw new DomainException(OrgUnitErrorCode.ORG_UNIT_HAS_CHILDREN);
    }
    orgUnitRepository.deleteById(OrgUnitId.of(id));
  }

  /** 查询组织单元详情 */
  public OrgUnitResponse findById(Long id) {
    OrgUnit orgUnit = findOrThrow(id);
    return assembler.toResponse(orgUnit, resolveLeaderName(orgUnit));
  }

  /** 获取组织架构树 */
  public List<OrgUnitTreeResponse> findTree() {
    List<OrgUnit> allOrgUnits = orgUnitQueryRepository.findAll();
    Map<Long, String> nameMap = buildLeaderNameMap(allOrgUnits);

    Map<Long, List<OrgUnit>> parentChildMap =
        allOrgUnits.stream().collect(Collectors.groupingBy(OrgUnit::parentId));

    return buildTree(parentChildMap, nameMap, 0L);
  }

  /** 分页查询组织单元 */
  public PageResult<OrgUnitResponse> findPage(OrgUnitQuery query, int page, int size) {
    Page<OrgUnit> result =
        orgUnitQueryRepository.findPage(
            query.keyword(), query.status(), query.parentId(), PageQuery.of(page, size));

    Map<Long, String> nameMap = buildLeaderNameMap(result.records());
    List<OrgUnitResponse> responses =
        result.records().stream()
            .map(
                orgUnit ->
                    assembler.toResponse(orgUnit, nameMap.get(orgUnit.managerEmployeeId())))
            .toList();

    return new PageResult<>(responses, result.total(), result.pageNum(), result.pageSize());
  }

  /** 递归构建树形结构 */
  private List<OrgUnitTreeResponse> buildTree(
      Map<Long, List<OrgUnit>> parentChildMap, Map<Long, String> nameMap, Long parentId) {
    return parentChildMap.getOrDefault(parentId, List.of()).stream()
        .map(
            orgUnit ->
                assembler.toTreeResponse(
                    orgUnit,
                    nameMap.get(orgUnit.managerEmployeeId()),
                    buildTree(parentChildMap, nameMap, orgUnit.id().value())))
        .toList();
  }

  /** 批量解析负责人姓名（managerEmployeeId → 姓名） */
  private Map<Long, String> buildLeaderNameMap(List<OrgUnit> orgUnits) {
    List<Long> employeeIds =
        orgUnits.stream()
            .map(OrgUnit::managerEmployeeId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    return employeeQueryPort.findNamesByIds(employeeIds);
  }

  private String resolveLeaderName(OrgUnit orgUnit) {
    if (orgUnit.managerEmployeeId() == null) {
      return null;
    }
    return employeeQueryPort.findNamesByIds(List.of(orgUnit.managerEmployeeId())).get(
        orgUnit.managerEmployeeId());
  }

  private OrgUnit findOrThrow(Long id) {
    return orgUnitRepository
        .findById(OrgUnitId.of(id))
        .orElseThrow(() -> new DomainException(OrgUnitErrorCode.ORG_UNIT_NOT_FOUND));
  }
}
