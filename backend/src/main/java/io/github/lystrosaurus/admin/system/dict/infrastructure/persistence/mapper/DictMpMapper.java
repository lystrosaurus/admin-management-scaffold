package io.github.lystrosaurus.admin.system.dict.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.dict.infrastructure.persistence.po.DictPO;
import org.apache.ibatis.annotations.Mapper;

/** 字典 MyBatis-Plus Mapper。 */
@Mapper
public interface DictMpMapper extends BaseMapper<DictPO> {}
