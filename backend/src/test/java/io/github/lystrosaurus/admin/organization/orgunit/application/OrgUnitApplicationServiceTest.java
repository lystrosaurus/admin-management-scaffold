package io.github.lystrosaurus.admin.organization.orgunit.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.organization.employee.domain.acl.EmployeeQueryPort;
import io.github.lystrosaurus.admin.organization.orgunit.adapter.response.OrgUnitResponse;
import io.github.lystrosaurus.admin.organization.orgunit.application.assembler.OrgUnitAssembler;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitQueryRepository;
import io.github.lystrosaurus.admin.organization.orgunit.domain.acl.OrgUnitRepository;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitErrorCode;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitId;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/** OrgUnitApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("组织单元应用服务测试")
class OrgUnitApplicationServiceTest {

  @Mock private OrgUnitRepository orgUnitRepository;

  @Mock private OrgUnitQueryRepository orgUnitQueryRepository;

  @Mock private EmployeeQueryPort employeeQueryPort;

  @Mock private IdGenerator idGenerator;

  @Spy private OrgUnitAssembler assembler = new OrgUnitAssembler();

  private OrgUnitApplicationService orgUnitApplicationService;

  private OrgUnit testOrgUnit;

  @BeforeEach
  void setUp() {
    orgUnitApplicationService =
        new OrgUnitApplicationService(
            orgUnitRepository, orgUnitQueryRepository, employeeQueryPort, idGenerator, assembler);
    testOrgUnit =
        OrgUnit.restore(
            OrgUnitId.of(1L),
            0L,
            "DEPT001",
            "技术部",
            "/1/",
            1,
            null,
            0,
            "ENABLED",
            "MANUAL",
            null,
            null);
  }

  @Test
  @DisplayName("应该成功删除组织单元")
  void should_delete_org_unit() {
    when(orgUnitRepository.findById(OrgUnitId.of(1L))).thenReturn(Optional.of(testOrgUnit));
    when(orgUnitRepository.hasChildren(1L)).thenReturn(false);

    orgUnitApplicationService.deleteById(1L);

    verify(orgUnitRepository).deleteById(OrgUnitId.of(1L));
  }

  @Test
  @DisplayName("删除有子节点的单元应该抛出异常")
  void should_throw_exception_when_org_unit_has_children() {
    when(orgUnitRepository.findById(OrgUnitId.of(1L))).thenReturn(Optional.of(testOrgUnit));
    when(orgUnitRepository.hasChildren(1L)).thenReturn(true);

    DomainException exception =
        assertThrows(DomainException.class, () -> orgUnitApplicationService.deleteById(1L));
    assertEquals(OrgUnitErrorCode.ORG_UNIT_HAS_CHILDREN.code(), exception.code());
  }

  @Test
  @DisplayName("删除不存在的组织单元应该抛出异常")
  void should_throw_exception_when_org_unit_not_found() {
    when(orgUnitRepository.findById(OrgUnitId.of(999L))).thenReturn(Optional.empty());

    DomainException exception =
        assertThrows(DomainException.class, () -> orgUnitApplicationService.deleteById(999L));
    assertEquals(OrgUnitErrorCode.ORG_UNIT_NOT_FOUND.code(), exception.code());
  }

  @Test
  @DisplayName("findById 应该解析负责人姓名")
  void should_resolve_leader_name() {
    OrgUnit orgUnit =
        OrgUnit.restore(
            OrgUnitId.of(1L),
            0L,
            "C",
            "技术部",
            "/1/",
            1,
            5L,
            0,
            "ENABLED",
            "MANUAL",
            null,
            null);
    when(orgUnitRepository.findById(OrgUnitId.of(1L))).thenReturn(Optional.of(orgUnit));
    when(employeeQueryPort.findNamesByIds(List.of(5L))).thenReturn(Map.of(5L, "张三"));

    OrgUnitResponse result = orgUnitApplicationService.findById(1L);

    assertEquals("张三", result.leaderName());
  }
}
