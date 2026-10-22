package io.github.lystrosaurus.admin.system.dict.domain.acl;

import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import io.github.lystrosaurus.admin.system.dict.domain.model.Dict;
import io.github.lystrosaurus.admin.system.dict.domain.model.DictId;

/** 字典仓储端口：字典聚合的存取契约。 */
public interface DictRepository extends GenericRepository<Dict, DictId> {}
