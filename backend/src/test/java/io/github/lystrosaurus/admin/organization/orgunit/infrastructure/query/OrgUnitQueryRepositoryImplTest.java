package io.github.lystrosaurus.admin.organization.orgunit.infrastructure.query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitId;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.converter.OrgUnitPoConverter;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.mapper.OrgUnitMpMapper;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.po.OrgUnitPO;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OrgUnitQueryRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrgUnitQueryRepositoryImpl 测试")
class OrgUnitQueryRepositoryImplTest {

  @Mock private OrgUnitMpMapper orgUnitMapper;

  private OrgUnitQueryRepositoryImpl queryRepository;

  private OrgUnitPO testPo;

  @BeforeEach
  void setUp() {
    OrgUnitPoConverter converter = new OrgUnitPoConverter();
    queryRepository = new OrgUnitQueryRepositoryImpl(orgUnitMapper, converter);
    testPo =
        converter.toPo(
            OrgUnit.restore(
                OrgUnitId.of(1L),
                0L,
                "HQ",
                "总公司",
                "/1/",
                1,
                null,
                0,
                "ENABLED",
                "MANUAL",
                null,
                null));
  }

  @Test
  @DisplayName("应该根据编码查找组织单元")
  void should_find_org_unit_by_code() {
    when(orgUnitMapper.selectOne(any())).thenReturn(testPo);

    Optional<OrgUnit> result = queryRepository.findByCode("HQ");

    assertTrue(result.isPresent());
    assertEquals("HQ", result.get().code());
    verify(orgUnitMapper).selectOne(any());
  }

  @Test
  @DisplayName("应该根据条件查找组织单元列表")
  void should_find_org_units_by_condition() {
    Page<OrgUnitPO> page = new Page<>(1, 10);
    page.setRecords(List.of(testPo));
    page.setTotal(1L);
    when(orgUnitMapper.selectPage(any(Page.class), any())).thenReturn(page);

    io.github.lystrosaurus.admin.shared.domain.acl.Page<OrgUnit> result =
        queryRepository.findPage("总公司", "ENABLED", 0L, PageQuery.of(1, 10));

    assertEquals(1, result.records().size());
    assertEquals("总公司", result.records().get(0).name());
    verify(orgUnitMapper).selectPage(any(Page.class), any());
  }

  @Test
  @DisplayName("应该根据条件统计组织单元数量")
  void should_count_org_units_by_condition() {
    when(orgUnitMapper.selectCount(any())).thenReturn(5L);

    long count = queryRepository.countByCondition("总公司", "ENABLED", 0L);

    assertEquals(5L, count);
    verify(orgUnitMapper).selectCount(any());
  }

  @Test
  @DisplayName("应该查找所有组织单元")
  void should_find_all_org_units() {
    when(orgUnitMapper.selectList(any())).thenReturn(List.of(testPo));

    List<OrgUnit> result = queryRepository.findAll();

    assertEquals(1, result.size());
    verify(orgUnitMapper).selectList(any());
  }

  @Test
  @DisplayName("应该根据父节点ID查找子节点")
  void should_find_by_parent_id() {
    when(orgUnitMapper.selectList(any())).thenReturn(List.of(testPo));

    List<OrgUnit> result = queryRepository.findByParentId(0L);

    assertEquals(1, result.size());
    verify(orgUnitMapper).selectList(any());
  }

  @Test
  @DisplayName("应该根据ID集合批量查找组织单元")
  void should_find_org_units_by_ids() {
    when(orgUnitMapper.selectBatchIds(anyCollection())).thenReturn(List.of(testPo));

    List<OrgUnit> result = queryRepository.findByIds(List.of(1L));

    assertEquals(1, result.size());
    assertEquals("HQ", result.get(0).code());
  }
}
