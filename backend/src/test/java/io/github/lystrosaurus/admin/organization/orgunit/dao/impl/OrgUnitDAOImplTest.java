package io.github.lystrosaurus.admin.organization.orgunit.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.mapper.SysOrgUnitMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OrgUnitDAOImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrgUnitDAOImpl 测试")
class OrgUnitDAOImplTest {

  @Mock private SysOrgUnitMapper orgUnitMapper;

  @InjectMocks private OrgUnitDAOImpl orgUnitDAO;

  private OrgUnit testOrgUnit;

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
  }

  @Test
  @DisplayName("应该根据ID查找组织单元")
  void should_find_org_unit_by_id() {
    when(orgUnitMapper.selectById(1L)).thenReturn(testOrgUnit);

    OrgUnit result = orgUnitDAO.findById(1L);

    assertNotNull(result);
    assertEquals("HQ", result.getCode());
    assertEquals("总公司", result.getName());
    verify(orgUnitMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该根据编码查找组织单元")
  void should_find_org_unit_by_code() {
    when(orgUnitMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOrgUnit);

    OrgUnit result = orgUnitDAO.findByCode("HQ");

    assertNotNull(result);
    assertEquals("HQ", result.getCode());
    verify(orgUnitMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该保存组织单元")
  void should_save_org_unit() {
    when(orgUnitMapper.insert(any(OrgUnit.class))).thenReturn(1);

    orgUnitDAO.save(testOrgUnit);

    verify(orgUnitMapper).insert(testOrgUnit);
  }

  @Test
  @DisplayName("应该更新组织单元")
  void should_update_org_unit() {
    when(orgUnitMapper.updateById(any(OrgUnit.class))).thenReturn(1);

    orgUnitDAO.update(testOrgUnit);

    verify(orgUnitMapper).updateById(testOrgUnit);
  }

  @Test
  @DisplayName("应该根据ID删除组织单元")
  void should_delete_org_unit_by_id() {
    when(orgUnitMapper.deleteById(1L)).thenReturn(1);

    orgUnitDAO.deleteById(1L);

    verify(orgUnitMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该根据条件查找组织单元列表")
  void should_find_org_units_by_condition() {
    @SuppressWarnings("unchecked")
    Page<OrgUnit> page = mock(Page.class);
    when(page.getRecords()).thenReturn(Arrays.asList(testOrgUnit));
    when(orgUnitMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

    List<OrgUnit> result = orgUnitDAO.findByCondition("总公司", "ENABLED", 0L, 1, 10);

    assertEquals(1, result.size());
    assertEquals("总公司", result.get(0).getName());
    verify(orgUnitMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据条件统计组织单元数量")
  void should_count_org_units_by_condition() {
    when(orgUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

    long count = orgUnitDAO.countByCondition("总公司", "ENABLED", 0L);

    assertEquals(5L, count);
    verify(orgUnitMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该查找所有组织单元")
  void should_find_all_org_units() {
    when(orgUnitMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Arrays.asList(testOrgUnit));

    List<OrgUnit> result = orgUnitDAO.findAll();

    assertEquals(1, result.size());
    verify(orgUnitMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据父节点ID查找子节点")
  void should_find_by_parent_id() {
    when(orgUnitMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Arrays.asList(testOrgUnit));

    List<OrgUnit> result = orgUnitDAO.findByParentId(0L);

    assertEquals(1, result.size());
    verify(orgUnitMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查编码是否存在")
  void should_check_code_exists() {
    when(orgUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    boolean exists = orgUnitDAO.existsByCode("HQ");

    assertTrue(exists);
    verify(orgUnitMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查编码不存在时返回false")
  void should_return_false_when_code_not_exists() {
    when(orgUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    boolean exists = orgUnitDAO.existsByCode("NONEXIST");

    assertFalse(exists);
    verify(orgUnitMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查编码是否存在且排除指定ID")
  void should_check_code_exists_excluding_id() {
    when(orgUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    boolean exists = orgUnitDAO.existsByCodeAndIdNot("HQ", 2L);

    assertTrue(exists);
    verify(orgUnitMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查是否有子节点")
  void should_check_has_children() {
    when(orgUnitMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);

    boolean hasChildren = orgUnitDAO.hasChildren(1L);

    assertTrue(hasChildren);
    verify(orgUnitMapper).selectCount(any(LambdaQueryWrapper.class));
  }
}
