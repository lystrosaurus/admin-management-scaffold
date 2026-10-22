package io.github.lystrosaurus.admin.system.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.system.user.infrastructure.persistence.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 MyBatis-Plus Mapper。
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface UserMpMapper extends BaseMapper<UserPO> {}
