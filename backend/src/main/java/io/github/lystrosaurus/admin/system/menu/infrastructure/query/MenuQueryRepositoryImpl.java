package io.github.lystrosaurus.admin.system.menu.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuQueryRepository;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import io.github.lystrosaurus.admin.system.menu.infrastructure.converter.MenuPoConverter;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.mapper.MenuMpMapper;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.po.MenuPO;
import io.github.lystrosaurus.admin.system.role.domain.acl.RoleAssociationPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** 菜单查询端口的 MyBatis-Plus 实现（读模型侧）。 */
@Repository
@RequiredArgsConstructor
public class MenuQueryRepositoryImpl implements MenuQueryRepository {

  private final MenuMpMapper menuMpMapper;
  private final RoleAssociationPort roleAssociationPort;
  private final MenuPoConverter converter;

  @Override
  public List<Menu> findAll() {
    return menuMpMapper
        .selectList(new LambdaQueryWrapper<MenuPO>().orderByAsc(MenuPO::getSortOrder))
        .stream()
        .map(converter::toDomain)
        .toList();
  }

  @Override
  public List<Menu> findByRoleId(Long roleId) {
    List<Long> menuIds = roleAssociationPort.findMenuIdsByRoleId(roleId);
    return findByIds(menuIds);
  }

  @Override
  public List<Menu> findByUserId(Long userId) {
    List<Long> menuIds = roleAssociationPort.findMenuIdsByUserId(userId);
    return findByIds(menuIds);
  }

  private List<Menu> findByIds(List<Long> menuIds) {
    if (menuIds.isEmpty()) {
      return List.of();
    }
    return menuMpMapper.selectBatchIds(menuIds).stream().map(converter::toDomain).toList();
  }
}
