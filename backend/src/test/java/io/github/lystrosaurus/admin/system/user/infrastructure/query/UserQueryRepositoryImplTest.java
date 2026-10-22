package io.github.lystrosaurus.admin.system.user.infrastructure.query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.role.mapper.SysRoleMapper;
import io.github.lystrosaurus.admin.system.user.domain.acl.UserRoleInfo;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.entity.SysUserRole;
import io.github.lystrosaurus.admin.system.user.infrastructure.converter.UserPoConverter;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.mapper.UserMpMapper;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.po.UserPO;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserQueryRepositoryImpl 单元测试
 *
 * <p>使用 Mockito mock Mapper，测试查询端口的业务逻辑。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryRepositoryImpl 测试")
class UserQueryRepositoryImplTest {

  @Mock private UserMpMapper userMpMapper;
  @Mock private SysUserRoleMapper userRoleMapper;
  @Mock private SysRoleMapper roleMapper;

  private final UserPoConverter converter = new UserPoConverter();

  private UserQueryRepositoryImpl userQueryRepository;

  private UserPO testPo;
  private SysRole testRole;

  @BeforeAll
  static void initTableInfo() {
    // 初始化 MyBatis-Plus Lambda 缓存
    MybatisConfiguration configuration = new MybatisConfiguration();
    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
    TableInfoHelper.initTableInfo(assistant, UserPO.class);
    TableInfoHelper.initTableInfo(assistant, SysRole.class);
    TableInfoHelper.initTableInfo(assistant, SysUserRole.class);
  }

  @BeforeEach
  void setUp() {
    userQueryRepository = new UserQueryRepositoryImpl(userMpMapper, userRoleMapper, roleMapper, converter);

    testPo = new UserPO();
    testPo.setId(1L);
    testPo.setUsername("admin");
    testPo.setPasswordHash("$2a$10$testhash");
    testPo.setNickname("管理员");
    testPo.setStatus("ENABLED");
    testPo.setTokenVersion(1);
    testPo.setCreatedAt(LocalDateTime.now());
    testPo.setUpdatedAt(LocalDateTime.now());

    testRole = new SysRole();
    testRole.setId(1L);
    testRole.setCode("ADMIN");
    testRole.setName("管理员");
    testRole.setStatus("ENABLED");
  }

  @Test
  @DisplayName("应该根据条件分页查找用户")
  void should_findPage_when_conditionsProvided() {
    // Given
    List<UserPO> expectedPos = Arrays.asList(testPo);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserPO> mockPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
    mockPage.setRecords(expectedPos);
    mockPage.setTotal(1L);
    when(userMpMapper.selectPage(
            any(com.baomidou.mybatisplus.extension.plugins.pagination.Page.class),
            any(LambdaQueryWrapper.class)))
        .thenReturn(mockPage);

    // When
    Page<User> result = userQueryRepository.findPage("admin", "ENABLED", PageQuery.of(1, 10));

    // Then
    assertNotNull(result);
    assertEquals(1, result.records().size());
    assertEquals(1L, result.total());
    assertEquals(1, result.pageNum());
    assertEquals(10, result.pageSize());
    verify(userMpMapper)
        .selectPage(
            any(com.baomidou.mybatisplus.extension.plugins.pagination.Page.class),
            any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据条件统计用户数量")
  void should_countByCondition_when_conditionsProvided() {
    // Given
    when(userMpMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

    // When
    long result = userQueryRepository.countByCondition("admin", "ENABLED");

    // Then
    assertEquals(5L, result);
    verify(userMpMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该查找用户关联的角色")
  void should_findRolesByUserId_when_userHasRoles() {
    // Given
    SysUserRole userRole = new SysUserRole();
    userRole.setId(1L);
    userRole.setUserId(1L);
    userRole.setRoleId(1L);
    when(userRoleMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Arrays.asList(userRole));
    when(roleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testRole));

    // When
    List<UserRoleInfo> result = userQueryRepository.findRolesByUserId(UserId.of(1L));

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("ADMIN", result.get(0).code());
    assertEquals("管理员", result.get(0).name());
    verify(userRoleMapper).selectList(any(LambdaQueryWrapper.class));
    verify(roleMapper).selectList(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空列表当用户没有角色时")
  void should_returnEmptyList_when_userHasNoRoles() {
    // Given
    when(userRoleMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<UserRoleInfo> result = userQueryRepository.findRolesByUserId(UserId.of(1L));

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRoleMapper).selectList(any(LambdaQueryWrapper.class));
    verify(roleMapper, never()).selectList(any(LambdaQueryWrapper.class));
  }
}
