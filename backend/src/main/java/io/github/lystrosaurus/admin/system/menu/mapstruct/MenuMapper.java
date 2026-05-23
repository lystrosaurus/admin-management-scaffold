package io.github.lystrosaurus.admin.system.menu.mapstruct;

import io.github.lystrosaurus.admin.system.menu.entity.SysMenu;
import io.github.lystrosaurus.admin.system.menu.vo.MenuVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/** 菜单 MapStruct 映射器 */
@Mapper(componentModel = "spring")
public interface MenuMapper {

  MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);

  /**
   * 将 SysMenu 转换为 MenuVO
   *
   * @param sysMenu 菜单实体
   * @return 菜单VO
   */
  @Mapping(target = "children", ignore = true) // 子菜单需要单独组装
  MenuVO toMenuVO(SysMenu sysMenu);

  /**
   * 将 SysMenu 列表转换为 MenuVO 列表
   *
   * @param sysMenus 菜单实体列表
   * @return 菜单VO列表
   */
  List<MenuVO> toMenuVOList(List<SysMenu> sysMenus);
}
