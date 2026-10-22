package io.github.lystrosaurus.admin.integration.source.application.assembler;

import io.github.lystrosaurus.admin.integration.source.adapter.response.ExtSourceResponse;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSource;
import org.springframework.stereotype.Component;

/** 外部身份源装配器：聚合 → 响应模型。 */
@Component
public class ExtSourceAssembler {

  /** 聚合转响应 */
  public ExtSourceResponse toResponse(ExtSource source) {
    return new ExtSourceResponse(
        source.id().value(),
        source.code(),
        source.name(),
        source.sourceType(),
        source.tenantKey(),
        source.status(),
        source.priority(),
        source.configJson(),
        source.createdAt(),
        source.updatedAt());
  }
}
