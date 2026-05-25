package io.github.lystrosaurus.admin.organization.orgunit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.organization.employee.dao.EmployeeOrgDAO;
import io.github.lystrosaurus.admin.organization.orgunit.dao.OrgUnitDAO;
import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OrgUnitService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("组织单元服务测试")
class OrgUnitServiceTest {

  @Mock private OrgUnitDAO orgUnitDAO;

  @Mock private EmployeeOrgDAO employeeOrgDAO;

  @Mock
  private io.github.lystrosaurus.admin.organization.orgunit.mapstruct.OrgUnitMapper orgUnitMapper;

  @InjectMocks private OrgUnitService orgUnitService;

  private OrgUnit testOrgUnit;

  @BeforeEach
  void setUp() {
    testOrgUnit = new OrgUnit();
    testOrgUnit.setId(1L);
    testOrgUnit.setCode("DEPT001");
    testOrgUnit.setName("技术部");
    testOrgUnit.setParentId(0L);
    testOrgUnit.setLevel(1);
    testOrgUnit.setFullPath("/1/");
    testOrgUnit.setStatus("ENABLED");
  }

  @Test
  @DisplayName("应该成功删除组织单元")
  void should_delete_org_unit() {
    when(orgUnitDAO.findById(1L)).thenReturn(testOrgUnit);
    when(orgUnitDAO.hasChildren(1L)).thenReturn(false);

    orgUnitService.deleteById(1L);

    verify(orgUnitDAO).deleteById(1L);
  }

  @Test
  @DisplayName("删除有子节点的单元应该抛出异常")
  void should_throw_exception_when_org_unit_has_children() {
    when(orgUnitDAO.findById(1L)).thenReturn(testOrgUnit);
    when(orgUnitDAO.hasChildren(1L)).thenReturn(true);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> orgUnitService.deleteById(1L));
    assertEquals(ErrorCode.ORG_UNIT_HAS_CHILDREN.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("删除不存在的组织单元应该抛出异常")
  void should_throw_exception_when_org_unit_not_found() {
    when(orgUnitDAO.findById(999L)).thenReturn(null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> orgUnitService.deleteById(999L));
    assertEquals(ErrorCode.ORG_UNIT_NOT_FOUND.getCode(), exception.getCode());
  }
}
