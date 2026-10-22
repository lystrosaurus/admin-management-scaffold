package io.github.lystrosaurus.admin.file.infrastructure.converter;

import io.github.lystrosaurus.admin.file.domain.model.SysFile;
import io.github.lystrosaurus.admin.file.infrastructure.persistence.po.SysFilePO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** 文件领域对象 ↔ 持久化对象转换器。审计字段由 MyBatis-Plus 自动填充，不在此转换。 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysFilePoConverter {

  /** 领域对象转持久化对象 */
  SysFilePO toPo(SysFile file);

  /** 持久化对象转领域对象 */
  SysFile toDomain(SysFilePO po);
}
