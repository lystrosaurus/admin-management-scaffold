package io.github.lystrosaurus.admin.system.menu.infrastructure.persistence;

import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.GenericRepositoryImpl;
import io.github.lystrosaurus.admin.system.menu.domain.acl.MenuRepository;
import io.github.lystrosaurus.admin.system.menu.domain.model.Menu;
import io.github.lystrosaurus.admin.system.menu.domain.model.MenuId;
import io.github.lystrosaurus.admin.system.menu.infrastructure.converter.MenuPoConverter;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.mapper.MenuMpMapper;
import io.github.lystrosaurus.admin.system.menu.infrastructure.persistence.po.MenuPO;
import org.springframework.stereotype.Repository;

/** 菜单仓储的 MyBatis-Plus 实现。 */
@Repository
public class MenuRepositoryImpl extends GenericRepositoryImpl<Menu, MenuId, MenuPO, MenuMpMapper>
    implements MenuRepository {

  private final MenuPoConverter converter;

  public MenuRepositoryImpl(
      MenuMpMapper mapper, DomainEventPublisher eventPublisher, MenuPoConverter converter) {
    super(mapper, eventPublisher);
    this.converter = converter;
  }

  @Override
  protected MenuPO toPo(Menu domain) {
    return converter.toPo(domain);
  }

  @Override
  protected Menu toDomain(MenuPO po) {
    return converter.toDomain(po);
  }
}
