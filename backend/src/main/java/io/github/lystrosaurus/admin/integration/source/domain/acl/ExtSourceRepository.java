package io.github.lystrosaurus.admin.integration.source.domain.acl;

import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSource;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSourceId;
import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import java.util.List;

/** 外部身份源仓储端口：外部身份源聚合的存取契约。 */
public interface ExtSourceRepository extends GenericRepository<ExtSource, ExtSourceId> {

  /** 判断系统编码是否存在 */
  boolean existsByCode(String code);

  /** 查询全部外部身份源（按优先级降序） */
  List<ExtSource> findAll();
}
