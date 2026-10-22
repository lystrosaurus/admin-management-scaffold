package io.github.lystrosaurus.admin.system.menu.infrastructure.query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import io.github.lystrosaurus.admin.system.menu.infrastructure.converter.MenuPoConverter;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.mapper.MenuMpMapper;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.po.MenuPO;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleAssociationPort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** MenuQueryRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单查询仓储实现测试")
class MenuQueryRepositoryImplTest {

  @Mock private MenuMpMapper menuMpMapper;
  @Mock private RoleAssociationPort roleAssociationPort;

  private MenuQueryRepositoryImpl queryRepository;

  @BeforeEach
  void setUp() {
    queryRepository =
        new MenuQueryRepositoryImpl(menuMpMapper, roleAssociationPort, new MenuPoConverter());
  }

  private MenuPO samplePo() {
    MenuPO po = new MenuPO();
    po.setId(10L);
    po.setParentId(0L);
    po.setName("系统管理");
    po.setPath("/system");
    po.setSortOrder(1);
    po.setType((byte) 1);
    po.setVisible((byte) 1);
    po.setStatus((byte) 1);
    po.setCreatedAt(LocalDateTime.now());
    po.setVersion(1);
    return po;
  }

  @Test
  @DisplayName("查询全部菜单")
  void should_find_all() {
    when(menuMpMapper.selectList(any())).thenReturn(List.of(samplePo()));

    List<Menu> result = queryRepository.findAll();

    assertEquals(1, result.size());
    assertEquals("系统管理", result.get(0).name());
  }

  @Test
  @DisplayName("按角色查询菜单")
  void should_find_by_role_id() {
    when(roleAssociationPort.findMenuIdsByRoleId(2L)).thenReturn(List.of(10L));
    when(menuMpMapper.selectBatchIds(List.of(10L))).thenReturn(List.of(samplePo()));

    List<Menu> result = queryRepository.findByRoleId(2L);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("按用户查询菜单")
  void should_find_by_user_id() {
    when(roleAssociationPort.findMenuIdsByUserId(3L)).thenReturn(List.of(10L));
    when(menuMpMapper.selectBatchIds(List.of(10L))).thenReturn(List.of(samplePo()));

    List<Menu> result = queryRepository.findByUserId(3L);

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("角色没有菜单时应该返回空且不查询菜单表")
  void should_return_empty_when_role_has_no_menus() {
    when(roleAssociationPort.findMenuIdsByRoleId(2L)).thenReturn(List.of());

    assertTrue(queryRepository.findByRoleId(2L).isEmpty());
    verify(menuMpMapper, never()).selectBatchIds(any());
  }

  @Test
  @DisplayName("用户没有菜单时应该返回空且不查询菜单表")
  void should_return_empty_when_user_has_no_menus() {
    when(roleAssociationPort.findMenuIdsByUserId(3L)).thenReturn(List.of());

    assertTrue(queryRepository.findByUserId(3L).isEmpty());
    verify(menuMpMapper, never()).selectBatchIds(any());
  }
}
