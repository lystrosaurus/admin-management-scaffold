package io.github.lystrosaurus.admin.auth.infrastructure.converter;

import io.github.lystrosaurus.admin.auth.domain.model.AuthProvider;
import io.github.lystrosaurus.admin.auth.infrastructure.persistence.po.AuthProviderPO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** 认证源领域对象 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthProviderPoConverter {

  /** 领域对象转持久化对象 */
  AuthProviderPO toPo(AuthProvider provider);

  /** 持久化对象转领域对象 */
  AuthProvider toDomain(AuthProviderPO po);
}
