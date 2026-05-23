package io.github.lystrosaurus.admin.system.user.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMapper;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserMapper;
import io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserDAOImpl 单元测试
 *
 * <p>使用 Mockito mock Mapper，测试 DAO 层的业务逻辑。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDAOImpl 测试")
class UserDAOImplTest {

  @Mock private SysUserMapper userMapper;
  @Mock private SysUserRoleMapper userRoleMapper;
  @Mock private SysRoleMapper roleMapper;

  @InjectMocks private UserDAOImpl userDAO;

  private SysUser testUser;
  private SysRole testRole;

  @BeforeAll
  static void initTableInfo() {
    // 初始化 MyBatis-Plus Lambda 缓存
    MybatisConfiguration configuration = new MybatisConfiguration();
    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
    TableInfoHelper.initTableInfo(assistant, SysUser.class);
    TableInfoHelper.initTableInfo(assistant, SysRole.class);
    TableInfoHelper.initTableInfo(assistant, SysUserRole.class);
  }

  @BeforeEach
  void setUp() {
    testUser = new SysUser();
    testUser.setId(1L);
    testUser.setUsername("admin");
    testUser.setPasswordHash("$2a$10$testhash");
    testUser.setNickname("管理员");
    testUser.setStatus("ENABLED");
    testUser.setTokenVersion(1);
    testUser.setCreatedAt(LocalDateTime.now());
    testUser.setUpdatedAt(LocalDateTime.now());

    testRole = new SysRole();
    testRole.setId(1L);
    testRole.setCode("ADMIN");
    testRole.setName("管理员");
    testRole.setStatus("ENABLED");
  }

  @Test
  @DisplayName("应该根据ID查找用户")
  void should_findUserById_when_idExists() {
    // Given
    when(userMapper.selectById(1L)).thenReturn(testUser);

    // When
    SysUser result = userDAO.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals("admin", result.getUsername());
    verify(userMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该返回null当ID不存在时")
  void should_returnNull_when_idNotExists() {
    // Given
    when(userMapper.selectById(999L)).thenReturn(null);

    // When
    SysUser result = userDAO.findById(999L);

    // Then
    assertNull(result);
    verify(userMapper).selectById(999L);
  }

  @Test
  @DisplayName("应该根据用户名查找用户")
  void should_findUserByUsername_when_usernameExists() {
    // Given
    when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

    // When
    SysUser result = userDAO.findByUsername("admin");

    // Then
    assertNotNull(result);
    assertEquals("admin", result.getUsername());
    verify(userMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回null当用户名不存在时")
  void should_returnNull_when_usernameNotExists() {
    // Given
    when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

    // When
    SysUser result = userDAO.findByUsername("nonexistent");

    // Then
    assertNull(result);
    verify(userMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该保存用户")
  void should_saveUser_when_validUser() {
    // Given
    when(userMapper.insert(any(SysUser.class))).thenReturn(1);

    // When
    userDAO.save(testUser);

    // Then
    verify(userMapper).insert(testUser);
  }

  @Test
  @DisplayName("应该更新用户")
  void should_updateUser_when_validUser() {
    // Given
    when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

    // When
    userDAO.update(testUser);

    // Then
    verify(userMapper).updateById(testUser);
  }

  @Test
  @DisplayName("应该删除用户")
  void should_deleteUser_when_idExists() {
    // Given
    when(userMapper.deleteById(1L)).thenReturn(1);

    // When
    userDAO.deleteById(1L);

    // Then
    verify(userMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该根据条件查找用户列表")
  void should_findUsersByCondition_when_conditionsProvided() {
    // Given
    List<SysUser> expectedUsers = Arrays.asList(testUser);
    Page<SysUser> mockPage = new Page<>();
    mockPage.setRecords(expectedUsers);
    when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
        .thenReturn(mockPage);

    // When
    List<SysUser> result = userDAO.findByCondition("admin", "ENABLED", 1, 10);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(userMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据条件统计用户数量")
  void should_countUsersByCondition_when_conditionsProvided() {
    // Given
    when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

    // When
    long result = userDAO.countByCondition("admin", "ENABLED");

    // Then
    assertEquals(5L, result);
    verify(userMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查用户名是否存在")
  void should_returnTrue_when_usernameExists() {
    // Given
    when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = userDAO.existsByUsername("admin");

    // Then
    assertTrue(result);
    verify(userMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查用户名不存在")
  void should_returnFalse_when_usernameNotExists() {
    // Given
    when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = userDAO.existsByUsername("nonexistent");

    // Then
    assertFalse(result);
    verify(userMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查用户名是否存在且排除指定ID")
  void should_returnTrue_when_usernameExistsAndIdNotExcluded() {
    // Given
    when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = userDAO.existsByUsernameAndIdNot("admin", 2L);

    // Then
    assertTrue(result);
    verify(userMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查用户名不存在且排除指定ID")
  void should_returnFalse_when_usernameNotExistsAndIdExcluded() {
    // Given
    when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = userDAO.existsByUsernameAndIdNot("admin", 1L);

    // Then
    assertFalse(result);
    verify(userMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该查找用户关联的角色")
  void should_findRolesByUserId_when_userHasRoles() {
    // Given
    // 先 mock 用户角色关联查询
    SysUserRole userRole = new SysUserRole();
    userRole.setId(1L);
    userRole.setUserId(1L);
    userRole.setRoleId(1L);
    List<SysUserRole> userRoles = Arrays.asList(userRole);
    when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(userRoles);

    // 再 mock 角色查询
    List<SysRole> expectedRoles = Arrays.asList(testRole);
    when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(expectedRoles);

    // When
    List<SysRole> result = userDAO.findRolesByUserId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("ADMIN", result.get(0).getCode());
    verify(userRoleMapper).selectList(any(LambdaQueryWrapper.class));
    verify(roleMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该分配角色给用户")
  void should_assignRoles_when_validUserIdAndRoleIds() {
    // Given
    List<Long> roleIds = Arrays.asList(1L, 2L);
    // 先 mock 删除现有角色
    when(userRoleMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);
    // 再 mock 插入新角色
    when(userRoleMapper.insert(any(SysUserRole.class))).thenReturn(1);

    // When
    userDAO.assignRoles(1L, roleIds);

    // Then
    verify(userRoleMapper).delete(any(LambdaQueryWrapper.class));
    verify(userRoleMapper, times(2)).insert(any(SysUserRole.class));
  }

  @Test
  @DisplayName("应该移除用户的所有角色")
  void should_removeRoles_when_userIdProvided() {
    // Given
    when(userRoleMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(2);

    // When
    userDAO.removeRoles(1L);

    // Then
    verify(userRoleMapper).delete(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当用户没有角色时")
  void should_returnEmptyList_when_userHasNoRoles() {
    // Given
    when(userRoleMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<SysRole> result = userDAO.findRolesByUserId(1L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRoleMapper).selectList(any(LambdaQueryWrapper.class));
    verify(roleMapper, never()).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该处理空角色ID列表")
  void should_handleEmptyRoleIds_when_assigningRoles() {
    // Given
    List<Long> emptyRoleIds = Collections.emptyList();

    // When
    userDAO.assignRoles(1L, emptyRoleIds);

    // Then
    verify(userRoleMapper, never()).insert(any(SysUserRole.class));
  }
}
