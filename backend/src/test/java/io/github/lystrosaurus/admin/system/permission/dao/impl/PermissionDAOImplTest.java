package io.github.lystrosaurus.admin.system.permission.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import io.github.lystrosaurus.admin.system.permission.mapper.SysPermissionMapper;
import io.github.lystrosaurus.admin.system.role.entity.SysRolePermission;
import io.github.lystrosaurus.admin.system.role.mapper.SysRolePermissionMapper;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
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
 * PermissionDAOImpl 单元测试
 *
 * <p>使用 Mockito mock Mapper，测试 DAO 层的业务逻辑。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionDAOImpl 测试")
class PermissionDAOImplTest {

  @Mock private SysPermissionMapper permissionMapper;
  @Mock private SysRolePermissionMapper rolePermissionMapper;
  @Mock private SysUserRoleMapper userRoleMapper;

  @InjectMocks private PermissionDAOImpl permissionDAO;

  private SysPermission testPermission;

  @BeforeAll
  static void initTableInfo() {
    // 初始化 MyBatis-Plus Lambda 缓存
    MybatisConfiguration configuration = new MybatisConfiguration();
    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
    TableInfoHelper.initTableInfo(assistant, SysPermission.class);
    TableInfoHelper.initTableInfo(assistant, SysRolePermission.class);
    TableInfoHelper.initTableInfo(assistant, SysUserRole.class);
  }

  @BeforeEach
  void setUp() {
    testPermission = new SysPermission();
    testPermission.setId(1L);
    testPermission.setCode("system:user:view");
    testPermission.setName("查看用户");
    testPermission.setType("API");
    testPermission.setModule("system");
    testPermission.setResource("user");
    testPermission.setAction("view");
    testPermission.setStatus("ENABLED");
    testPermission.setSortOrder(1);
    testPermission.setCreatedAt(LocalDateTime.now());
    testPermission.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  @DisplayName("应该根据ID查找权限")
  void should_findPermissionById_when_idExists() {
    // Given
    when(permissionMapper.selectById(1L)).thenReturn(testPermission);

    // When
    SysPermission result = permissionDAO.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals("system:user:view", result.getCode());
    verify(permissionMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该返回null当ID不存在时")
  void should_returnNull_when_idNotExists() {
    // Given
    when(permissionMapper.selectById(999L)).thenReturn(null);

    // When
    SysPermission result = permissionDAO.findById(999L);

    // Then
    assertNull(result);
    verify(permissionMapper).selectById(999L);
  }

  @Test
  @DisplayName("应该根据权限编码查找权限")
  void should_findPermissionByCode_when_codeExists() {
    // Given
    when(permissionMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPermission);

    // When
    SysPermission result = permissionDAO.findByCode("system:user:view");

    // Then
    assertNotNull(result);
    assertEquals("system:user:view", result.getCode());
    verify(permissionMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回null当权限编码不存在时")
  void should_returnNull_when_codeNotExists() {
    // Given
    when(permissionMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

    // When
    SysPermission result = permissionDAO.findByCode("nonexistent:code");

    // Then
    assertNull(result);
    verify(permissionMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该保存权限")
  void should_savePermission_when_validPermission() {
    // Given
    when(permissionMapper.insert(any(SysPermission.class))).thenReturn(1);

    // When
    permissionDAO.save(testPermission);

    // Then
    verify(permissionMapper).insert(testPermission);
  }

  @Test
  @DisplayName("应该更新权限")
  void should_updatePermission_when_validPermission() {
    // Given
    when(permissionMapper.updateById(any(SysPermission.class))).thenReturn(1);

    // When
    permissionDAO.update(testPermission);

    // Then
    verify(permissionMapper).updateById(testPermission);
  }

  @Test
  @DisplayName("应该删除权限")
  void should_deletePermission_when_idExists() {
    // Given
    when(permissionMapper.deleteById(1L)).thenReturn(1);

    // When
    permissionDAO.deleteById(1L);

    // Then
    verify(permissionMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该根据条件查找权限列表")
  void should_findPermissionsByCondition_when_conditionsProvided() {
    // Given
    List<SysPermission> expectedPermissions = Arrays.asList(testPermission);
    Page<SysPermission> mockPage = new Page<>();
    mockPage.setRecords(expectedPermissions);
    when(permissionMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
        .thenReturn(mockPage);

    // When
    List<SysPermission> result = permissionDAO.findByCondition("system", "API", 1, 10);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(permissionMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据条件统计权限数量")
  void should_countPermissionsByCondition_when_conditionsProvided() {
    // Given
    when(permissionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

    // When
    long result = permissionDAO.countByCondition("system", "API");

    // Then
    assertEquals(5L, result);
    verify(permissionMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据模块查找权限列表")
  void should_findPermissionsByModule_when_moduleExists() {
    // Given
    List<SysPermission> expectedPermissions = Arrays.asList(testPermission);
    when(permissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(expectedPermissions);

    // When
    List<SysPermission> result = permissionDAO.findByModule("system");

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("system:user:view", result.get(0).getCode());
    verify(permissionMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据角色ID查找权限列表")
  void should_findPermissionsByRoleId_when_roleHasPermissions() {
    // Given
    // 先 mock 角色权限关联查询
    SysRolePermission rolePermission = new SysRolePermission();
    rolePermission.setId(1L);
    rolePermission.setRoleId(1L);
    rolePermission.setPermissionId(1L);
    List<SysRolePermission> rolePermissions = Arrays.asList(rolePermission);
    when(rolePermissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(rolePermissions);

    // 再 mock 权限查询
    List<SysPermission> expectedPermissions = Arrays.asList(testPermission);
    when(permissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(expectedPermissions);

    // When
    List<SysPermission> result = permissionDAO.findByRoleId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("system:user:view", result.get(0).getCode());
    verify(rolePermissionMapper).selectList(any(LambdaQueryWrapper.class));
    verify(permissionMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据用户ID查找权限列表")
  void should_findPermissionsByUserId_when_userHasPermissions() {
    // Given
    // 先 mock 用户角色关联查询
    SysUserRole userRole = new SysUserRole();
    userRole.setId(1L);
    userRole.setUserId(1L);
    userRole.setRoleId(1L);
    List<SysUserRole> userRoles = Arrays.asList(userRole);
    when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(userRoles);

    // 再 mock 角色权限关联查询
    SysRolePermission rolePermission = new SysRolePermission();
    rolePermission.setId(1L);
    rolePermission.setRoleId(1L);
    rolePermission.setPermissionId(1L);
    List<SysRolePermission> rolePermissions = Arrays.asList(rolePermission);
    when(rolePermissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(rolePermissions);

    // 最后 mock 权限查询
    List<SysPermission> expectedPermissions = Arrays.asList(testPermission);
    when(permissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(expectedPermissions);

    // When
    List<SysPermission> result = permissionDAO.findByUserId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("system:user:view", result.get(0).getCode());
    verify(userRoleMapper).selectList(any(LambdaQueryWrapper.class));
    verify(rolePermissionMapper).selectList(any(LambdaQueryWrapper.class));
    verify(permissionMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查权限编码是否存在")
  void should_returnTrue_when_codeExists() {
    // Given
    when(permissionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = permissionDAO.existsByCode("system:user:view");

    // Then
    assertTrue(result);
    verify(permissionMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查权限编码不存在")
  void should_returnFalse_when_codeNotExists() {
    // Given
    when(permissionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = permissionDAO.existsByCode("nonexistent:code");

    // Then
    assertFalse(result);
    verify(permissionMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查权限编码是否存在且排除指定ID")
  void should_returnTrue_when_codeExistsAndIdNotExcluded() {
    // Given
    when(permissionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = permissionDAO.existsByCodeAndIdNot("system:user:view", 2L);

    // Then
    assertTrue(result);
    verify(permissionMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查权限编码不存在且排除指定ID")
  void should_returnFalse_when_codeNotExistsAndIdExcluded() {
    // Given
    when(permissionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = permissionDAO.existsByCodeAndIdNot("system:user:view", 1L);

    // Then
    assertFalse(result);
    verify(permissionMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当模块没有权限时")
  void should_returnEmptyList_when_moduleHasNoPermissions() {
    // Given
    when(permissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<SysPermission> result = permissionDAO.findByModule("nonexistent");

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(permissionMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当角色没有权限时")
  void should_returnEmptyList_when_roleHasNoPermissions() {
    // Given
    when(rolePermissionMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<SysPermission> result = permissionDAO.findByRoleId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(rolePermissionMapper).selectList(any(LambdaQueryWrapper.class));
    verify(permissionMapper, never()).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当用户没有权限时")
  void should_returnEmptyList_when_userHasNoPermissions() {
    // Given
    when(userRoleMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<SysPermission> result = permissionDAO.findByUserId(999L);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRoleMapper).selectList(any(LambdaQueryWrapper.class));
    verify(rolePermissionMapper, never()).selectList(any(LambdaQueryWrapper.class));
    verify(permissionMapper, never()).selectList(any(LambdaQueryWrapper.class));
  }
}
