package io.github.lystrosaurus.admin.auth.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.auth.provider.entity.AuthProvider;
import org.apache.ibatis.annotations.Mapper;

/** 认证源 Mapper */
@Mapper
public interface AuthProviderMapper extends BaseMapper<AuthProvider> {}
