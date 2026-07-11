package io.github.lystrosaurus.admin.file.mapstruct;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.lystrosaurus.admin.file.entity.SysFile;
import io.github.lystrosaurus.admin.file.vo.FileVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** 文件 MapStruct 映射器 */
@Mapper(componentModel = SPRING)
public interface FileMapper {

  /** 将 SysFile 转换为 FileVO */
  @Mapping(target = "createdAt", source = "createdAt")
  FileVO toVO(SysFile sysFile);
}
