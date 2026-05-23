package io.github.lystrosaurus.admin.integration.principal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipalIdentifier;
import org.apache.ibatis.annotations.Mapper;

/**
 * 外部主体标识符 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper，提供基本的 CRUD 操作。
 */
@Mapper
public interface ExtPrincipalIdentifierMapper extends BaseMapper<ExtPrincipalIdentifier> {}
