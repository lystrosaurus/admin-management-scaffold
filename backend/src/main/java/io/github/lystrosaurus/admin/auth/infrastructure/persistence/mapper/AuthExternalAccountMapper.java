package io.github.lystrosaurus.admin.auth.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.auth.infrastructure.persistence.po.AuthExternalAccountPO;
import org.apache.ibatis.annotations.Mapper;

/** 三方账号 MyBatis-Plus Mapper。 */
@Mapper
public interface AuthExternalAccountMapper extends BaseMapper<AuthExternalAccountPO> {}
