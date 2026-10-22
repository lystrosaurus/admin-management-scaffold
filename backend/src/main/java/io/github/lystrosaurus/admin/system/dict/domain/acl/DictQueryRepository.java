package io.github.lystrosaurus.admin.system.dict.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.dict.domain.model.Dict;

/** 字典查询端口：读模型侧查询（分页、列表）。 */
public interface DictQueryRepository {

  /** 按字典类型分页查询（类型为空时查询全部，按排序号升序） */
  Page<Dict> findPage(String dictType, PageQuery pageQuery);
}
