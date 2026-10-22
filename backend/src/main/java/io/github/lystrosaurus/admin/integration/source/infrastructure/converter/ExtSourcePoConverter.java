package io.github.lystrosaurus.admin.integration.source.infrastructure.converter;

import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSource;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSourceId;
import io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.po.ExtSourcePO;
import org.springframework.stereotype.Component;

/** 外部身份源聚合 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Component
public class ExtSourcePoConverter {

  /** 领域对象转持久化对象 */
  public ExtSourcePO toPo(ExtSource source) {
    ExtSourcePO po = new ExtSourcePO();
    po.setId(source.id() == null ? null : source.id().value());
    po.setCode(source.code());
    po.setName(source.name());
    po.setSourceType(source.sourceType());
    po.setTenantKey(source.tenantKey());
    po.setStatus(source.status());
    po.setPriority(source.priority());
    po.setConfigJson(source.configJson());
    return po;
  }

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  public ExtSource toDomain(ExtSourcePO po) {
    return ExtSource.restore(
        ExtSourceId.of(po.getId()),
        po.getCode(),
        po.getName(),
        po.getSourceType(),
        po.getTenantKey(),
        po.getStatus(),
        po.getPriority(),
        po.getConfigJson(),
        po.getCreatedAt(),
        po.getUpdatedAt());
  }
}
