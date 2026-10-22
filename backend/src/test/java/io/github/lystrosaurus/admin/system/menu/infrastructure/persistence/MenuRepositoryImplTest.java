package io.github.lystrosaurus.admin.system.menu.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import io.github.lystrosaurus.admin.system.menu.domain.model.MenuId;
import io.github.lystrosaurus.admin.system.menu.infrastructure.converter.MenuPoConverter;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.mapper.MenuMpMapper;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.po.MenuPO;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** MenuRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单仓储实现测试")
class MenuRepositoryImplTest {

  @Mock private MenuMpMapper menuMpMapper;
  @Mock private DomainEventPublisher eventPublisher;

  private MenuRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new MenuRepositoryImpl(menuMpMapper, eventPublisher, new MenuPoConverter());
  }

  private MenuPO samplePo() {
    MenuPO po = new MenuPO();
    po.setId(1L);
    po.setParentId(0L);
    po.setName("系统管理");
    po.setPath("/system");
    po.setComponent("System");
    po.setIcon("setting");
    po.setSortOrder(1);
    po.setType((byte) 1);
    po.setVisible((byte) 1);
    po.setStatus((byte) 1);
    po.setCreatedAt(LocalDateTime.now());
    po.setVersion(1);
    return po;
  }

  private Menu sampleMenu() {
    return Menu.restore(
        MenuId.of(1L),
        0L,
        "系统管理",
        "/system",
        "System",
        "setting",
        1,
        (byte) 1,
        null,
        (byte) 1,
        (byte) 1,
        LocalDateTime.now(),
        1);
  }

  @Test
  @DisplayName("根据 ID 查询菜单应该返回领域对象")
  void should_find_menu_by_id() {
    when(menuMpMapper.selectById(1L)).thenReturn(samplePo());

    Optional<Menu> result = repository.findById(MenuId.of(1L));

    assertTrue(result.isPresent());
    assertEquals("系统管理", result.get().name());
  }

  @Test
  @DisplayName("根据 ID 查询不存在的菜单应该返回空")
  void should_return_empty_when_find_by_id_not_exists() {
    when(menuMpMapper.selectById(999L)).thenReturn(null);

    assertTrue(repository.findById(MenuId.of(999L)).isEmpty());
  }

  @Test
  @DisplayName("保存新菜单应该执行插入并发布事件")
  void should_insert_when_save_new_menu() {
    Menu menu = sampleMenu();
    when(menuMpMapper.exists(any())).thenReturn(false);

    repository.save(menu);

    verify(menuMpMapper).insert(any(MenuPO.class));
    verify(eventPublisher).publishAll(menu);
  }

  @Test
  @DisplayName("保存已存在菜单应该执行更新")
  void should_update_when_save_existing_menu() {
    Menu menu = sampleMenu();
    when(menuMpMapper.exists(any())).thenReturn(true);

    repository.save(menu);

    verify(menuMpMapper).updateById(any(MenuPO.class));
  }

  @Test
  @DisplayName("根据 ID 删除菜单")
  void should_delete_menu_by_id() {
    repository.deleteById(MenuId.of(1L));

    verify(menuMpMapper).deleteById(1L);
  }

  @Test
  @DisplayName("existsById 应该反映记录是否存在")
  void should_check_exists_by_id() {
    when(menuMpMapper.selectById(1L)).thenReturn(samplePo());
    when(menuMpMapper.selectById(2L)).thenReturn(null);

    assertTrue(repository.existsById(MenuId.of(1L)));
    assertFalse(repository.existsById(MenuId.of(2L)));
  }
}
