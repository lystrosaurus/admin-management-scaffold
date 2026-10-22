package io.github.lystrosaurus.admin.system.menu.infrastructure.acl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.shared.infrastructure.cache.TwoLevelCache;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryRepository;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuSummary;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import io.github.lystrosaurus.admin.system.menu.domain.model.MenuId;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

/** MenuQueryPortImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单查询端口实现测试")
class MenuQueryPortImplTest {

  @Mock private MenuQueryRepository queryRepository;
  @Mock private StringRedisTemplate stringRedisTemplate;

  private MenuQueryPortImpl port;

  @BeforeEach
  void setUp() {
    TwoLevelCache cache = new TwoLevelCache(stringRedisTemplate, new ObjectMapper());
    ReflectionTestUtils.setField(cache, "enabled", false);
    port = new MenuQueryPortImpl(queryRepository, cache);
  }

  private Menu menu() {
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
  @DisplayName("按用户查询菜单摘要")
  void should_find_summaries_by_user_id() {
    when(queryRepository.findByUserId(1L)).thenReturn(List.of(menu()));

    List<MenuSummary> result = port.findByUserId(1L);

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).id());
    assertEquals("系统管理", result.get(0).name());
    assertEquals(0L, result.get(0).parentId());
  }

  @Test
  @DisplayName("按角色查询菜单摘要")
  void should_find_summaries_by_role_id() {
    when(queryRepository.findByRoleId(2L)).thenReturn(List.of(menu()));

    List<MenuSummary> result = port.findByRoleId(2L);

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).id());
  }

  @Test
  @DisplayName("没有菜单时返回空列表")
  void should_return_empty_when_no_menus() {
    when(queryRepository.findByUserId(1L)).thenReturn(List.of());
    when(queryRepository.findByRoleId(2L)).thenReturn(List.of());

    assertTrue(port.findByUserId(1L).isEmpty());
    assertTrue(port.findByRoleId(2L).isEmpty());
  }
}
