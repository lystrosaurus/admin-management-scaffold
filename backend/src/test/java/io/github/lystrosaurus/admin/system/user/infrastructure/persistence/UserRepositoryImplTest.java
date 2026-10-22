package io.github.lystrosaurus.admin.system.user.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleAssociationPort;
import io.github.lystrosaurus.admin.system.user.domain.model.User;
import io.github.lystrosaurus.admin.system.user.domain.model.UserId;
import io.github.lystrosaurus.admin.system.user.infrastructure.converter.UserPoConverter;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.mapper.UserMpMapper;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.po.UserPO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRepositoryImpl 单元测试
 *
 * <p>使用 Mockito mock Mapper，测试仓储实现的业务逻辑。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryImpl 测试")
class UserRepositoryImplTest {

  @Mock private UserMpMapper userMpMapper;
  @Mock private DomainEventPublisher eventPublisher;
  @Mock private RoleAssociationPort roleAssociationPort;

  private final UserPoConverter converter = new UserPoConverter();

  private UserRepositoryImpl userRepository;

  private UserPO testPo;
  private User testUser;

  @BeforeAll
  static void initTableInfo() {
    // 初始化 MyBatis-Plus Lambda 缓存
    MybatisConfiguration configuration = new MybatisConfiguration();
    MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
    TableInfoHelper.initTableInfo(assistant, UserPO.class);
  }

  @BeforeEach
  void setUp() {
    userRepository =
        new UserRepositoryImpl(userMpMapper, eventPublisher, converter, roleAssociationPort);

    testPo = new UserPO();
    testPo.setId(1L);
    testPo.setUsername("admin");
    testPo.setPasswordHash("$2a$10$testhash");
    testPo.setNickname("管理员");
    testPo.setStatus("ENABLED");
    testPo.setTokenVersion(1);
    testPo.setCreatedAt(LocalDateTime.now());
    testPo.setUpdatedAt(LocalDateTime.now());

    testUser = converter.toDomain(testPo);
  }

  @Test
  @DisplayName("应该根据ID查找用户")
  void should_findUserById_when_idExists() {
    // Given
    when(userMpMapper.selectById(1L)).thenReturn(testPo);

    // When
    Optional<User> result = userRepository.findById(UserId.of(1L));

    // Then
    assertTrue(result.isPresent());
    assertEquals("admin", result.get().username());
    verify(userMpMapper).selectById(1L);
  }

  @Test
  @DisplayName("应该返回空当ID不存在时")
  void should_returnEmpty_when_idNotExists() {
    // Given
    when(userMpMapper.selectById(999L)).thenReturn(null);

    // When
    Optional<User> result = userRepository.findById(UserId.of(999L));

    // Then
    assertTrue(result.isEmpty());
    verify(userMpMapper).selectById(999L);
  }

  @Test
  @DisplayName("应该根据用户名查找用户")
  void should_findUserByUsername_when_usernameExists() {
    // Given
    when(userMpMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPo);

    // When
    Optional<User> result = userRepository.findByUsername("admin");

    // Then
    assertTrue(result.isPresent());
    assertEquals("admin", result.get().username());
    verify(userMpMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该返回空当用户名不存在时")
  void should_returnEmpty_when_usernameNotExists() {
    // Given
    when(userMpMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

    // When
    Optional<User> result = userRepository.findByUsername("nonexistent");

    // Then
    assertTrue(result.isEmpty());
    verify(userMpMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该插入新用户")
  void should_insertUser_when_userNotExists() {
    // Given
    when(userMpMapper.exists(any(QueryWrapper.class))).thenReturn(false);
    when(userMpMapper.insert(any(UserPO.class))).thenReturn(1);

    // When
    userRepository.save(testUser);

    // Then
    verify(userMpMapper).insert(any(UserPO.class));
    verify(userMpMapper, never()).updateById(any(UserPO.class));
    verify(eventPublisher).publishAll(testUser);
  }

  @Test
  @DisplayName("应该更新已存在用户")
  void should_updateUser_when_userExists() {
    // Given
    when(userMpMapper.exists(any(QueryWrapper.class))).thenReturn(true);
    when(userMpMapper.updateById(any(UserPO.class))).thenReturn(1);

    // When
    userRepository.save(testUser);

    // Then
    verify(userMpMapper).updateById(any(UserPO.class));
    verify(userMpMapper, never()).insert(any(UserPO.class));
    verify(eventPublisher).publishAll(testUser);
  }

  @Test
  @DisplayName("应该删除用户")
  void should_deleteUser_when_idExists() {
    // Given
    when(userMpMapper.deleteById(1L)).thenReturn(1);

    // When
    userRepository.deleteById(UserId.of(1L));

    // Then
    verify(userMpMapper).deleteById(1L);
  }

  @Test
  @DisplayName("应该检查用户名是否存在")
  void should_returnTrue_when_usernameExists() {
    // Given
    when(userMpMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = userRepository.existsByUsername("admin");

    // Then
    assertTrue(result);
    verify(userMpMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查用户名不存在")
  void should_returnFalse_when_usernameNotExists() {
    // Given
    when(userMpMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = userRepository.existsByUsername("nonexistent");

    // Then
    assertFalse(result);
    verify(userMpMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查用户名是否存在且排除指定ID")
  void should_returnTrue_when_usernameExistsAndIdNotExcluded() {
    // Given
    when(userMpMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

    // When
    boolean result = userRepository.existsByUsernameAndIdNot("admin", UserId.of(2L));

    // Then
    assertTrue(result);
    verify(userMpMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该检查用户名不存在且排除指定ID")
  void should_returnFalse_when_usernameNotExistsAndIdExcluded() {
    // Given
    when(userMpMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

    // When
    boolean result = userRepository.existsByUsernameAndIdNot("admin", UserId.of(1L));

    // Then
    assertFalse(result);
    verify(userMpMapper).selectCount(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据员工ID查找用户")
  void should_findUserByEmployeeId_when_employeeIdExists() {
    // Given
    when(userMpMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPo);

    // When
    Optional<User> result = userRepository.findByEmployeeId(100L);

    // Then
    assertTrue(result.isPresent());
    verify(userMpMapper).selectOne(any(LambdaQueryWrapper.class));
  }

  @Test
  @DisplayName("应该根据ID集合查找用户")
  void should_findUsersByIds_when_idsProvided() {
    // Given
    when(userMpMapper.selectBatchIds(anyCollection())).thenReturn(Arrays.asList(testPo));

    // When
    List<User> result = userRepository.findByIds(Arrays.asList(1L));

    // Then
    assertEquals(1, result.size());
    assertEquals("admin", result.get(0).username());
    verify(userMpMapper).selectBatchIds(anyCollection());
  }

  @Test
  @DisplayName("应该返回空列表当ID集合为空时")
  void should_returnEmptyList_when_idsEmpty() {
    // When
    List<User> result = userRepository.findByIds(Collections.emptyList());

    // Then
    assertTrue(result.isEmpty());
    verify(userMpMapper, never()).selectBatchIds(anyCollection());
  }

  @Test
  @DisplayName("应该分配角色给用户")
  void should_assignRoles_when_validUserIdAndRoleIds() {
    // Given
    List<Long> roleIds = Arrays.asList(1L, 2L);

    // When
    userRepository.assignRoles(1L, roleIds);

    // Then
    verify(roleAssociationPort).assignRoles(1L, roleIds);
  }

  @Test
  @DisplayName("应该移除用户的所有角色")
  void should_removeRoles_when_userIdProvided() {
    // When
    userRepository.removeRoles(1L);

    // Then
    verify(roleAssociationPort).removeRoles(1L);
  }

  @Test
  @DisplayName("应该处理空角色ID列表")
  void should_handleEmptyRoleIds_when_assigningRoles() {
    // Given
    List<Long> emptyRoleIds = Collections.emptyList();

    // When
    userRepository.assignRoles(1L, emptyRoleIds);

    // Then
    verify(roleAssociationPort).assignRoles(1L, emptyRoleIds);
  }
}
