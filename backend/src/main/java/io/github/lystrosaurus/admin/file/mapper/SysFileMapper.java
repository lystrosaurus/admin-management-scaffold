package io.github.lystrosaurus.admin.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.file.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

/** 系统文件 Mapper */
@Mapper
public interface SysFileMapper extends BaseMapper<SysFile> {}
