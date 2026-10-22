package io.github.lystrosaurus.admin.auth.infrastructure.converter;

import io.github.lystrosaurus.admin.auth.domain.model.ExternalAccount;
import io.github.lystrosaurus.admin.auth.infrastructure.persistence.po.AuthExternalAccountPO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** 三方账号领域对象 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExternalAccountPoConverter {

  /** 领域对象转持久化对象 */
  AuthExternalAccountPO toPo(ExternalAccount account);

  /** 持久化对象转领域对象 */
  ExternalAccount toDomain(AuthExternalAccountPO po);
}
