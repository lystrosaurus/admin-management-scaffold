package io.github.lystrosaurus.admin.system.dict.infrastructure.converter;

import io.github.lystrosaurus.admin.system.dict.domain.model.Dict;
import io.github.lystrosaurus.admin.system.dict.domain.model.DictId;
import io.github.lystrosaurus.admin.system.dict.infrastructure.persistence.po.DictPO;
import org.springframework.stereotype.Component;

/** 字典聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class DictPoConverter {

  /** 领域对象转持久化对象 */
  public DictPO toPo(Dict dict) {
    DictPO po = new DictPO();
    po.setId(dict.id() == null ? null : dict.id().value());
    po.setDictType(dict.dictType());
    po.setDictLabel(dict.dictLabel());
    po.setDictValue(dict.dictValue());
    po.setSortOrder(dict.sortOrder());
    po.setStatus(dict.status());
    po.setRemark(dict.remark());
    po.setVersion(dict.version());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public Dict toDomain(DictPO po) {
    return Dict.restore(
        DictId.of(po.getId()),
        po.getDictType(),
        po.getDictLabel(),
        po.getDictValue(),
        po.getSortOrder(),
        po.getStatus(),
        po.getRemark(),
        po.getCreatedAt(),
        po.getVersion());
  }
}
