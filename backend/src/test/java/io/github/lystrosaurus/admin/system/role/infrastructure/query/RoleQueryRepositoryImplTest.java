package io.github.lystrosaurus.admin.system.role.infrastructure.query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.role.domain.model.Role;
import io.github.lystrosaurus.admin.system.role.infrastructure.converter.RolePoConverter;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.RoleMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.mapper.UserRoleMpMapper;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.RolePO;
import io.github.lystrosaurus.admin.system.role.infrastructure.persistence.po.UserRolePO;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** RoleQueryRepositoryImpl 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("角色查询仓储实现测试")
class RoleQueryRepositoryImplTest {

  @Mock private RoleMpMapper roleMpMapper;
  @Mock private UserRoleMpMapper userRoleMpMapper;

  private RoleQueryRepositoryImpl queryRepository;

  @BeforeEach
  void setUp() {
    queryRepository =
        new RoleQueryRepositoryImpl(roleMpMapper, userRoleMpMapper, new RolePoConverter());
  }

  private RolePO samplePo() {
    RolePO po = new RolePO();
    po.setId(1L);
    po.setCode("ADMIN");
    po.setName("管理员");
    po.setSortOrder(1);
    po.setStatus("ENABLED");
    po.setDataScopeType("ALL");
    po.setCreatedAt(LocalDateTime.now());
    po.setVersion(1);
    return po;
  }

  @Test
  @DisplayName("分页查询角色应该返回领域分页结果")
  void should_find_page() {
    Page<RolePO> mpPage = new Page<>(1, 10);
    mpPage.setRecords(List.of(samplePo()));
    mpPage.setTotal(1L);
    when(roleMpMapper.selectPage(any(), any())).thenReturn(mpPage);

    io.github.lystrosaurus.admin.shared.domain.acl.Page<Role> result =
        queryRepository.findPage("ADMIN", "ENABLED", PageQuery.of(1, 10));

    assertEquals(1, result.records().size());
    assertEquals(1L, result.total());
    assertEquals(1, result.pageNum());
    assertEquals(10, result.pageSize());
    assertEquals("ADMIN", result.records().get(0).code());
  }

  @Test
  @DisplayName("按条件统计角色数量")
  void should_count_by_condition() {
    when(roleMpMapper.selectCount(any())).thenReturn(3L);

    assertEquals(3L, queryRepository.countByCondition("管理员", "ENABLED"));
  }

  @Test
  @DisplayName("查询所有角色应该按排序号升序返回")
  void should_find_all_roles() {
    when(roleMpMapper.selectList(any())).thenReturn(List.of(samplePo()));

    List<Role> result = queryRepository.findAll();

    assertEquals(1, result.size());
    assertEquals("ADMIN", result.get(0).code());
  }

  @Test
  @DisplayName("根据用户 ID 查询角色")
  void should_find_roles_by_user_id() {
    UserRolePO userRole = new UserRolePO();
    userRole.setUserId(1L);
    userRole.setRoleId(2L);
    RolePO rolePo = samplePo();
    rolePo.setId(2L);
    when(userRoleMpMapper.selectList(any())).thenReturn(List.of(userRole));
    when(roleMpMapper.selectList(any())).thenReturn(List.of(rolePo));

    List<Role> result = queryRepository.findByUserId(1L);

    assertEquals(1, result.size());
    assertEquals(2L, result.get(0).id().value());
  }

  @Test
  @DisplayName("用户没有角色时应该返回空列表且不再查询角色表")
  void should_return_empty_when_user_has_no_roles() {
    when(userRoleMpMapper.selectList(any())).thenReturn(List.of());

    List<Role> result = queryRepository.findByUserId(1L);

    assertTrue(result.isEmpty());
    verify(roleMpMapper, never()).selectList(any());
  }
}
