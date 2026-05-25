package io.github.lystrosaurus.admin.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.lystrosaurus.admin.auth.vo.ProfileVO;
import io.github.lystrosaurus.admin.system.menu.service.MenuService;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import io.github.lystrosaurus.admin.system.role.service.RoleService;
import io.github.lystrosaurus.admin.system.role.vo.RoleVO;
import io.github.lystrosaurus.admin.system.user.dto.ChangePasswordDTO;
import io.github.lystrosaurus.admin.system.user.service.UserService;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import io.github.lystrosaurus.admin.test.SaTokenTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * ProfileController 测试
 *
 * <p>测试用户资料控制器的各个接口
 */
@DisplayName("ProfileController 测试")
class ProfileControllerTest extends SaTokenTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserService userService;

  @MockitoBean private RoleService roleService;

  @MockitoBean private MenuService menuService;

  private UserVO userVO;
  private ProfileVO profileVO;

  @BeforeEach
  void setUp() {
    userVO =
        new UserVO(
            1L,
            "admin",
            "管理员",
            "13800138000",
            "admin@example.com",
            "ENABLED",
            LocalDateTime.now(),
            LocalDateTime.now());

    MenuVO menuVO =
        new MenuVO(
            1L, null, "系统管理", "/system", "Layout", "setting", 1, (byte) 1, null, (byte) 1, (byte) 1,
            List.of());

    profileVO =
        new ProfileVO(
            userVO, List.of("ADMIN"), List.of("user:create", "user:update"), List.of(menuVO));
  }

  @Test
  @DisplayName("应该成功获取当前用户资料")
  void should_get_current_user_profile() throws Exception {
    RoleVO roleVO = new RoleVO(1L, "ADMIN", "管理员", "系统管理员", "ENABLED", "ALL");
    UserDetailVO userDetail =
        new UserDetailVO(
            1L,
            "admin",
            "管理员",
            "13800138000",
            "admin@example.com",
            "ENABLED",
            null,
            1,
            LocalDateTime.now(),
            "127.0.0.1",
            List.of(roleVO),
            LocalDateTime.now());
    when(userService.findById(1L)).thenReturn(userDetail);
    when(roleService.findByUserId(1L)).thenReturn(List.of(roleVO));
    when(menuService.findByUserId(1L)).thenReturn(List.<MenuVO>of());

    mockMvc
        .perform(get("/app/profile"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该成功修改自己的密码")
  void should_change_own_password_successfully() throws Exception {
    ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("oldPassword", "newPassword");
    doNothing().when(userService).changePassword(eq(1L), any(ChangePasswordDTO.class));

    mockMvc
        .perform(
            put("/app/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
  }

  @Test
  @DisplayName("应该返回400当旧密码为空")
  void should_return_400_when_old_password_is_blank() throws Exception {
    ChangePasswordDTO invalidDTO = new ChangePasswordDTO("", "newPassword");

    mockMvc
        .perform(
            put("/app/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该返回400当新密码为空")
  void should_return_400_when_new_password_is_blank() throws Exception {
    ChangePasswordDTO invalidDTO = new ChangePasswordDTO("oldPassword", "");

    mockMvc
        .perform(
            put("/app/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("应该成功获取当前用户权限码列表")
  void should_get_current_user_permissions() throws Exception {
    mockMvc
        .perform(get("/app/profile/permissions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray());
  }
}
