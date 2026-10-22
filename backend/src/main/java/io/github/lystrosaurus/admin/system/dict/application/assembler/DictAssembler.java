package io.github.lystrosaurus.admin.system.dict.application.assembler;

import io.github.lystrosaurus.admin.system.dict.adapter.response.DictResponse;
import io.github.lystrosaurus.admin.system.dict.domain.model.Dict;
import org.springframework.stereotype.Component;

/** 字典装配器：聚合 → 响应模型。 */
@Component
public class DictAssembler {

  /** 聚合转字典响应 */
  public DictResponse toResponse(Dict dict) {
    return new DictResponse(
        dict.id().value(),
        dict.dictType(),
        dict.dictLabel(),
        dict.dictValue(),
        dict.sortOrder(),
        dict.status(),
        dict.remark());
  }
}
