package io.github.lystrosaurus.admin.system.menu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lystrosaurus.admin.system.menu.dto.MenuCreateDTO;
import io.github.lystrosaurus.admin.system.menu.dto.MenuUpdateDTO;
import io.github.lystrosaurus.admin.system.menu.service.MenuService;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * MenuController 测试
 *
 * <p>测试菜单控制器的各个接口
 */
@DisplayName("MenuController 测试")
class MenuControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private MenuService menuService;

  private MenuVO menuVO;

  @BeforeEach
  void setUp() {
    menuVO =
        new MenuVO(
            1L, null, "系统管理", "/system", "Layout", "setting", 1, (byte) 1, null, (byte) 1, (byte) 1,
            List.of());
  }

  @Test
  @DisplayName("应该成功创建菜单")
  void should_create_menu_successfully() throws Exception {
    MenuCreateDTO createDTO =
        new MenuCreateDTO(
            null, "系统管理", "/system", "Layout", "setting", 1, (byte) 1, null, (byte) 1);
    when(menuService.create(any(MenuCreateDTO.class))).thenReturn(menuVO);

    mockMvc
        .perform(
            post("/app/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.name").value("系统管理"))
        .andExpect(jsonPath("$.data.path").value("/system"));
  }

  @Test
  @DisplayName("应该成功更新菜单")
  void should_update_menu_successfully() throws Exception {
    MenuUpdateDTO updateDTO =
        new MenuUpdateDTO(
            "新名称", "/new-path", "Component", "icon", 2, (byte) 1, "code", (byte) 1, (byte) 1);
    MenuVO updatedMenuVO =
        new MenuVO(
            1L,
            null,
            "新名称",
            "/new-path",
            "Component",
            "icon",
            2,
            (byte) 1,
            "code",
            (byte) 1,
            (byte) 1,
            List.of());
    when(menuService.update(eq(1L), any(MenuUpdateDTO.class))).thenReturn(updatedMenuVO);

    mockMvc
        .perform(
            put("/app/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("新名称"));
  }

  @Test
  @DisplayName("应该成功删除菜单")
  void should_delete_menu_successfully() throws Exception {
    doNothing().when(menuService).deleteById(1L);

    mockMvc
        .perform(delete("/app/menus/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功获取菜单树")
  void should_get_menu_tree_successfully() throws Exception {
    when(menuService.findTree()).thenReturn(List.of(menuVO));

    mockMvc
        .perform(get("/app/menus/tree"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].name").value("系统管理"));
  }

  @Test
  @DisplayName("应该成功按角色查询菜单")
  void should_find_menus_by_role_id() throws Exception {
    when(menuService.findByRoleId(1L)).thenReturn(List.of(menuVO));

    mockMvc
        .perform(get("/app/menus/role/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].name").value("系统管理"));
  }

  @Test
  @DisplayName("应该成功获取当前用户菜单树")
  void should_get_user_menu_tree_successfully() throws Exception {
    when(menuService.findByUserId(1L)).thenReturn(List.of(menuVO));

    mockMvc
        .perform(get("/app/menus/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].name").value("系统管理"));
  }
}
