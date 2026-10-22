package io.github.lystrosaurus.admin.auth.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.auth.infrastructure.persistence.po.AuthProviderPO;
import org.apache.ibatis.annotations.Mapper;

/** 认证源 MyBatis-Plus Mapper。 */
@Mapper
public interface AuthProviderMapper extends BaseMapper<AuthProviderPO> {}
