package io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnit;
import io.github.lystrosaurus.admin.organization.orgunit.domain.model.OrgUnitId;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.converter.OrgUnitPoConverter;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.mapper.OrgUnitMpMapper;
import io.github.lystrosaurus.admin.organization.orgunit.infrastructure.persistence.po.OrgUnitPO;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** OrgUnitRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrgUnitRepositoryImpl 测试")
class OrgUnitRepositoryImplTest {

  @Mock private OrgUnitMpMapper orgUnitMapper;

  @Mock private DomainEventPublisher eventPublisher;

  private OrgUnitRepositoryImpl orgUnitRepository;

  private OrgUnit testOrgUnit;

  private OrgUnitPO testPo;

  @BeforeEach
  void setUp() {
    OrgUnitPoConverter converter = new OrgUnitPoConverter();
    orgUnitRepository = new OrgUnitRepositoryImpl(orgUnitMapper, eventPublisher, converter);
    testOrgUnit =
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
            null);
    testPo = converter.toPo(testOrgUnit);
  }

  @Test
  @DisplayName("应该根据ID查找组织单元")
  void should_find_org_unit_by_id() {
    when(orgUnitMapper.selectById(1L)).thenReturn(testPo);

    Optional<OrgUnit> result = orgUnitRepository.findById(OrgUnitId.of(1L));

    assertTrue(result.isPresent());
    assertEquals("HQ", result.get().code());
    assertEquals("总公司", result.get().name());
    verify(orgUnitMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该保存组织单元")
  void should_save_org_unit() {
    when(orgUnitMapper.exists(any(QueryWrapper.class))).thenReturn(false);
    when(orgUnitMapper.insert(any(OrgUnitPO.class))).thenReturn(1);

    orgUnitRepository.save(testOrgUnit);

    verify(orgUnitMapper).insert(any(OrgUnitPO.class));
    verify(eventPublisher).publishAll(testOrgUnit);
  }

  @Test
  @DisplayName("应该更新组织单元")
  void should_update_org_unit() {
    when(orgUnitMapper.exists(any(QueryWrapper.class))).thenReturn(true);
    when(orgUnitMapper.updateById(any(OrgUnitPO.class))).thenReturn(1);

    orgUnitRepository.save(testOrgUnit);

    verify(orgUnitMapper).updateById(any(OrgUnitPO.class));
  }

  @Test
  @DisplayName("应该根据ID删除组织单元")
  void should_delete_org_unit_by_id() {
    when(orgUnitMapper.deleteById(1L)).thenReturn(1);

    orgUnitRepository.deleteById(OrgUnitId.of(1L));

    verify(orgUnitMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该检查编码是否存在")
  void should_check_code_exists() {
    when(orgUnitMapper.selectCount(any())).thenReturn(1L);

    assertTrue(orgUnitRepository.existsByCode("HQ"));
  }

  @Test
  @DisplayName("应该检查编码不存在时返回false")
  void should_return_false_when_code_not_exists() {
    when(orgUnitMapper.selectCount(any())).thenReturn(0L);

    assertFalse(orgUnitRepository.existsByCode("NONEXIST"));
  }

  @Test
  @DisplayName("应该检查编码是否存在且排除指定ID")
  void should_check_code_exists_excluding_id() {
    when(orgUnitMapper.selectCount(any())).thenReturn(1L);

    assertTrue(orgUnitRepository.existsByCodeAndIdNot("HQ", OrgUnitId.of(2L)));
  }

  @Test
  @DisplayName("应该检查是否有子节点")
  void should_check_has_children() {
    when(orgUnitMapper.selectCount(any())).thenReturn(2L);

    assertTrue(orgUnitRepository.hasChildren(1L));
  }
}
