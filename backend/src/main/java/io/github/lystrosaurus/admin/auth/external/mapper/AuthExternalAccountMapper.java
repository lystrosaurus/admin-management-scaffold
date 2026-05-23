package io.github.lystrosaurus.admin.auth.external.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.auth.external.entity.AuthExternalAccount;
import org.apache.ibatis.annotations.Mapper;

/** 三方账号 Mapper */
@Mapper
public interface AuthExternalAccountMapper extends BaseMapper<AuthExternalAccount> {}
