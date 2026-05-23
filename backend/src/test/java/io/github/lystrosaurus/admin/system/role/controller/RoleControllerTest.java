package io.github.lystrosaurus.admin.system.role.controller;

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
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.role.dto.RoleCreateDTO;
import io.github.lystrosaurus.admin.system.role.dto.RoleUpdateDTO;
import io.github.lystrosaurus.admin.system.role.service.RoleService;
import io.github.lystrosaurus.admin.system.role.vo.RoleDetailVO;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * RoleController 测试
 *
 * <p>测试角色控制器的各个接口
 */
@DisplayName("RoleController 测试")
class RoleControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private RoleService roleService;

  private RoleVO roleVO;
  private RoleDetailVO roleDetailVO;

  @BeforeEach
  void setUp() {
    roleVO = new RoleVO(1L, "ADMIN", "管理员", "系统管理员", "ENABLED", "ALL");
    roleDetailVO =
        new RoleDetailVO(
            1L,
            "ADMIN",
            "管理员",
            "系统管理员",
            1,
            "ENABLED",
            "ALL",
            List.of(),
            List.of(),
            LocalDateTime.now());
  }

  @Test
  @DisplayName("应该成功创建角色")
  void should_create_role_successfully() throws Exception {
    RoleCreateDTO createDTO = new RoleCreateDTO("ADMIN", "管理员", "系统管理员", 1, "ALL");
    when(roleService.create(any(RoleCreateDTO.class))).thenReturn(roleVO);

    mockMvc
        .perform(
            post("/app/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.code").value("ADMIN"))
        .andExpect(jsonPath("$.data.name").value("管理员"));
  }

  @Test
  @DisplayName("应该成功获取角色详情")
  void should_get_role_detail_successfully() throws Exception {
    when(roleService.findById(1L)).thenReturn(roleDetailVO);

    mockMvc
        .perform(get("/app/roles/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.code").value("ADMIN"));
  }

  @Test
  @DisplayName("应该返回404当角色不存在")
  void should_return_404_when_role_not_found() throws Exception {
    when(roleService.findById(999L)).thenThrow(new BusinessException(ErrorCode.ROLE_NOT_FOUND));

    mockMvc
        .perform(get("/app/roles/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ErrorCode.ROLE_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("应该成功更新角色")
  void should_update_role_successfully() throws Exception {
    RoleUpdateDTO updateDTO = new RoleUpdateDTO("新名称", "新描述", 2, "ENABLED", "ALL");
    RoleVO updatedRoleVO = new RoleVO(1L, "ADMIN", "新名称", "新描述", "ENABLED", "ALL");
    when(roleService.update(eq(1L), any(RoleUpdateDTO.class))).thenReturn(updatedRoleVO);

    mockMvc
        .perform(
            put("/app/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("新名称"));
  }

  @Test
  @DisplayName("应该成功删除角色")
  void should_delete_role_successfully() throws Exception {
    doNothing().when(roleService).deleteById(1L);

    mockMvc
        .perform(delete("/app/roles/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功查询所有角色")
  void should_find_all_roles_successfully() throws Exception {
    when(roleService.findAll()).thenReturn(List.of(roleVO));

    mockMvc
        .perform(get("/app/roles"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].code").value("ADMIN"));
  }

  @Test
  @DisplayName("应该成功分配权限给角色")
  void should_assign_permissions_successfully() throws Exception {
    doNothing().when(roleService).assignPermissions(eq(1L), any());

    mockMvc
        .perform(
            post("/app/roles/1/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功分配菜单给角色")
  void should_assign_menus_successfully() throws Exception {
    doNothing().when(roleService).assignMenus(eq(1L), any());

    mockMvc
        .perform(
            post("/app/roles/1/menus").contentType(MediaType.APPLICATION_JSON).content("[1, 2, 3]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }
}
