package io.github.lystrosaurus.admin.system.configitem.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.configitem.domain.model.ConfigItem;

/** 配置项查询端口：读模型侧查询（分页、列表）。 */
public interface ConfigItemQueryRepository {

  /** 按配置键模糊分页查询（配置键为空时查询全部，按主键倒序） */
  Page<ConfigItem> findPage(String configKey, PageQuery pageQuery);
}
