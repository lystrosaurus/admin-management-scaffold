package io.github.lystrosaurus.admin.organization.orgunit.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OrgUnitServiceImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrgUnitServiceImpl 测试")
class OrgUnitServiceImplTest {

  @Mock private OrgUnitDAO orgUnitDAO;

  @Mock private EmployeeOrgDAO employeeOrgDAO;

  @Mock private OrgUnitMapper orgUnitMapper;

  @InjectMocks private OrgUnitServiceImpl orgUnitService;

  private OrgUnit testOrgUnit;
  private OrgUnitCreateDTO createDTO;
  private OrgUnitUpdateDTO updateDTO;
  private OrgUnitVO orgUnitVO;

  @BeforeEach
  void setUp() {
    testOrgUnit = new OrgUnit();
    testOrgUnit.setId(1L);
    testOrgUnit.setParentId(0L);
    testOrgUnit.setCode("HQ");
    testOrgUnit.setName("总公司");
    testOrgUnit.setFullPath("/1/");
    testOrgUnit.setLevel(1);
    testOrgUnit.setSortOrder(0);
    testOrgUnit.setStatus("ENABLED");
    testOrgUnit.setSourceType("MANUAL");
    testOrgUnit.setCreatedAt(LocalDateTime.now());

    createDTO = new OrgUnitCreateDTO("HQ", "总公司", 0L, null, 0);
    updateDTO = new OrgUnitUpdateDTO("集团总部", null, null, 1, null);

    orgUnitVO =
        new OrgUnitVO(1L, "HQ", "总公司", 0L, "/1/", 1, null, 0, "ENABLED", LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功创建组织单元（根节点）")
  void should_create_root_org_unit() {
    // Given
    when(orgUnitDAO.existsByCode("HQ")).thenReturn(false);
    when(orgUnitMapper.toEntity(createDTO)).thenReturn(testOrgUnit);
    doAnswer(
            invocation -> {
              OrgUnit saved = invocation.getArgument(0);
              saved.setId(1L);
              return null;
            })
        .when(orgUnitDAO)
        .save(any(OrgUnit.class));
    when(orgUnitMapper.toVO(any(OrgUnit.class))).thenReturn(orgUnitVO);

    // When
    OrgUnitVO result = orgUnitService.create(createDTO);

    // Then
    assertNotNull(result);
    assertEquals("HQ", result.code());
    assertEquals("总公司", result.name());
    verify(orgUnitDAO).existsByCode("HQ");
    verify(orgUnitDAO).save(any(OrgUnit.class));
  }

  @Test
  @DisplayName("创建组织单元时编码已存在应该抛出异常")
  void should_throw_exception_when_code_already_exists() {
    // Given
    when(orgUnitDAO.existsByCode("HQ")).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> orgUnitService.create(createDTO));
    assertEquals(ErrorCode.ORG_UNIT_ALREADY_EXISTS.getCode(), exception.getCode());
    verify(orgUnitDAO).existsByCode("HQ");
    verify(orgUnitDAO, never()).save(any());
  }

  @Test
  @DisplayName("应该成功创建子组织单元")
  void should_create_child_org_unit() {
    // Given
    OrgUnitCreateDTO childDTO = new OrgUnitCreateDTO("TECH", "技术部", 1L, null, 0);
    OrgUnit parentOrg = new OrgUnit();
    parentOrg.setId(1L);
    parentOrg.setParentId(0L);
    parentOrg.setFullPath("/1/");
    parentOrg.setLevel(1);

    OrgUnit childOrg = new OrgUnit();
    childOrg.setCode("TECH");
    childOrg.setName("技术部");
    childOrg.setParentId(1L);

    when(orgUnitDAO.existsByCode("TECH")).thenReturn(false);
    when(orgUnitDAO.findById(1L)).thenReturn(parentOrg);
    when(orgUnitMapper.toEntity(childDTO)).thenReturn(childOrg);
    doAnswer(
            invocation -> {
              OrgUnit saved = invocation.getArgument(0);
              saved.setId(2L);
              return null;
            })
        .when(orgUnitDAO)
        .save(any(OrgUnit.class));

    OrgUnitVO childVO =
        new OrgUnitVO(2L, "TECH", "技术部", 1L, "/1/2/", 2, null, 0, "ENABLED", LocalDateTime.now());
    when(orgUnitMapper.toVO(any(OrgUnit.class))).thenReturn(childVO);

    // When
    OrgUnitVO result = orgUnitService.create(childDTO);

    // Then
    assertNotNull(result);
    assertEquals("TECH", result.code());
    assertEquals(2, result.level());
    assertEquals("/1/2/", result.fullPath());
    verify(orgUnitDAO).save(any(OrgUnit.class));
  }

  @Test
  @DisplayName("应该成功更新组织单元")
  void should_update_org_unit() {
    // Given
    when(orgUnitDAO.findById(1L)).thenReturn(testOrgUnit);
    when(orgUnitMapper.toVO(any(OrgUnit.class))).thenReturn(orgUnitVO);

    // When
    OrgUnitVO result = orgUnitService.update(1L, updateDTO);

    // Then
    assertNotNull(result);
    verify(orgUnitDAO).findById(1L);
    verify(orgUnitDAO).update(any(OrgUnit.class));
    assertEquals("集团总部", testOrgUnit.getName());
    assertEquals(1, testOrgUnit.getSortOrder());
  }

  @Test
  @DisplayName("更新组织单元时组织不存在应该抛出异常")
  void should_throw_exception_when_update_nonexistent_org() {
    // Given
    when(orgUnitDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> orgUnitService.update(999L, updateDTO));
    assertEquals(ErrorCode.ORG_UNIT_NOT_FOUND.getCode(), exception.getCode());
    verify(orgUnitDAO).findById(999L);
    verify(orgUnitDAO, never()).update(any());
  }

  @Test
  @DisplayName("应该成功删除组织单元")
  void should_delete_org_unit_by_id() {
    // Given
    when(orgUnitDAO.findById(1L)).thenReturn(testOrgUnit);
    when(orgUnitDAO.hasChildren(1L)).thenReturn(false);

    // When
    orgUnitService.deleteById(1L);

    // Then
    verify(orgUnitDAO).deleteById(1L);
  }

  @Test
  @DisplayName("删除组织单元时存在子节点应该抛出异常")
  void should_throw_exception_when_delete_org_with_children() {
    // Given
    when(orgUnitDAO.findById(1L)).thenReturn(testOrgUnit);
    when(orgUnitDAO.hasChildren(1L)).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> orgUnitService.deleteById(1L));
    assertEquals(ErrorCode.ORG_UNIT_HAS_CHILDREN.getCode(), exception.getCode());
    verify(orgUnitDAO, never()).deleteById(any());
  }

  @Test
  @DisplayName("删除组织单元时组织不存在应该抛出异常")
  void should_throw_exception_when_delete_nonexistent_org() {
    // Given
    when(orgUnitDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> orgUnitService.deleteById(999L));
    assertEquals(ErrorCode.ORG_UNIT_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功查询组织单元详情")
  void should_find_org_unit_by_id() {
    // Given
    when(orgUnitDAO.findById(1L)).thenReturn(testOrgUnit);
    when(orgUnitMapper.toVO(testOrgUnit)).thenReturn(orgUnitVO);

    // When
    OrgUnitVO result = orgUnitService.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals("HQ", result.code());
    verify(orgUnitDAO).findById(1L);
  }

  @Test
  @DisplayName("查询组织单元详情时不存在应该抛出异常")
  void should_throw_exception_when_find_nonexistent_org() {
    // Given
    when(orgUnitDAO.findById(999L)).thenReturn(null);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> orgUnitService.findById(999L));
    assertEquals(ErrorCode.ORG_UNIT_NOT_FOUND.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该成功分页查询组织单元")
  void should_find_org_units_by_page() {
    // Given
    OrgUnitQueryDTO queryDTO = new OrgUnitQueryDTO("总公司", "ENABLED", 0L);
    List<OrgUnit> orgUnits = Arrays.asList(testOrgUnit);
    when(orgUnitDAO.findByCondition("总公司", "ENABLED", 0L, 1, 10)).thenReturn(orgUnits);
    when(orgUnitDAO.countByCondition("总公司", "ENABLED", 0L)).thenReturn(1L);
    when(orgUnitMapper.toVO(testOrgUnit)).thenReturn(orgUnitVO);

    // When
    PageResult<OrgUnitVO> result = orgUnitService.findPage(queryDTO, 1, 10);

    // Then
    assertNotNull(result);
    assertEquals(1, result.items().size());
    assertEquals(1L, result.total());
  }

  @Test
  @DisplayName("应该成功构建组织架构树")
  void should_build_org_tree() {
    // Given
    OrgUnit root = new OrgUnit();
    root.setId(1L);
    root.setParentId(0L);
    root.setCode("HQ");
    root.setName("总公司");
    root.setLevel(1);
    root.setSortOrder(0);
    root.setStatus("ENABLED");

    OrgUnit child = new OrgUnit();
    child.setId(2L);
    child.setParentId(1L);
    child.setCode("TECH");
    child.setName("技术部");
    child.setLevel(2);
    child.setSortOrder(0);
    child.setStatus("ENABLED");

    when(orgUnitDAO.findAll()).thenReturn(Arrays.asList(root, child));

    OrgUnitTreeVO rootTreeVO =
        new OrgUnitTreeVO(1L, "HQ", "总公司", 0L, 1, null, 0, "ENABLED", List.of());
    OrgUnitTreeVO childTreeVO =
        new OrgUnitTreeVO(2L, "TECH", "技术部", 1L, 2, null, 0, "ENABLED", List.of());
    when(orgUnitMapper.toTreeVO(root)).thenReturn(rootTreeVO);
    when(orgUnitMapper.toTreeVO(child)).thenReturn(childTreeVO);

    // When
    List<OrgUnitTreeVO> tree = orgUnitService.findTree();

    // Then
    assertNotNull(tree);
    assertEquals(1, tree.size()); // 只有一个根节点
    assertEquals("总公司", tree.get(0).name());
    assertEquals(1, tree.get(0).children().size()); // 一个子节点
    assertEquals("技术部", tree.get(0).children().get(0).name());
  }
}
