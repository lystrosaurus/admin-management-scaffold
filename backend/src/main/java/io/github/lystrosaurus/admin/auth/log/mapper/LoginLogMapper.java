package io.github.lystrosaurus.admin.auth.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.auth.log.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/** 登录日志Mapper */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {}
