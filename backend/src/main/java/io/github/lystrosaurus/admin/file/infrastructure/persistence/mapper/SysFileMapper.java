package io.github.lystrosaurus.admin.file.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.file.infrastructure.persistence.po.SysFilePO;
import org.apache.ibatis.annotations.Mapper;

/** 系统文件 MyBatis-Plus Mapper */
@Mapper
public interface SysFileMapper extends BaseMapper<SysFilePO> {}
