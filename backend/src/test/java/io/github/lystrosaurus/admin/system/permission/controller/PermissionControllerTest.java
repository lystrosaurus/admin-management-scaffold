package io.github.lystrosaurus.admin.system.permission.controller;

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
import io.github.lystrosaurus.admin.system.permission.dto.PermissionCreateDTO;
import io.github.lystrosaurus.admin.system.permission.dto.PermissionUpdateDTO;
import io.github.lystrosaurus.admin.system.permission.service.PermissionService;
import io.github.lystrosaurus.admin.system.permission.vo.PermissionVO;
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
 * PermissionController 测试
 *
 * <p>测试权限控制器的各个接口
 */
@DisplayName("PermissionController 测试")
class PermissionControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private PermissionService permissionService;

  private PermissionVO permissionVO;

  @BeforeEach
  void setUp() {
    permissionVO =
        new PermissionVO(1L, "user:create", "创建用户", "BUTTON", "system", "user", "create");
  }

  @Test
  @DisplayName("应该成功创建权限")
  void should_create_permission_successfully() throws Exception {
    PermissionCreateDTO createDTO =
        new PermissionCreateDTO("user:create", "创建用户", "BUTTON", "system", "user", "create");
    when(permissionService.create(any(PermissionCreateDTO.class))).thenReturn(permissionVO);

    mockMvc
        .perform(
            post("/app/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data.code").value("user:create"))
        .andExpect(jsonPath("$.data.name").value("创建用户"));
  }

  @Test
  @DisplayName("应该成功更新权限")
  void should_update_permission_successfully() throws Exception {
    PermissionUpdateDTO updateDTO =
        new PermissionUpdateDTO("新名称", "BUTTON", "system", "user", "create", "ENABLED");
    PermissionVO updatedPermissionVO =
        new PermissionVO(1L, "user:create", "新名称", "BUTTON", "system", "user", "create");
    when(permissionService.update(eq(1L), any(PermissionUpdateDTO.class)))
        .thenReturn(updatedPermissionVO);

    mockMvc
        .perform(
            put("/app/permissions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.name").value("新名称"));
  }

  @Test
  @DisplayName("应该成功删除权限")
  void should_delete_permission_successfully() throws Exception {
    doNothing().when(permissionService).deleteById(1L);

    mockMvc
        .perform(delete("/app/permissions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功按角色查询权限")
  void should_find_permissions_by_role_id() throws Exception {
    when(permissionService.findByRoleId(1L)).thenReturn(List.of(permissionVO));

    mockMvc
        .perform(get("/app/permissions/role/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].code").value("user:create"));
  }
}
