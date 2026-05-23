package io.github.lystrosaurus.admin.integration.principal.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalIdentifierDAO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipalIdentifier;
import io.github.lystrosaurus.admin.integration.principal.mapper.ExtPrincipalIdentifierMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 外部主体标识符数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class ExtPrincipalIdentifierDAOImpl implements ExtPrincipalIdentifierDAO {

  private final ExtPrincipalIdentifierMapper extPrincipalIdentifierMapper;

  @Override
  public List<ExtPrincipalIdentifier> findByPrincipalId(Long principalId) {
    return extPrincipalIdentifierMapper.selectList(
        new LambdaQueryWrapper<ExtPrincipalIdentifier>()
            .eq(ExtPrincipalIdentifier::getPrincipalId, principalId));
  }

  @Override
  public void save(ExtPrincipalIdentifier entity) {
    extPrincipalIdentifierMapper.insert(entity);
  }

  @Override
  public void saveBatch(List<ExtPrincipalIdentifier> entities) {
    for (ExtPrincipalIdentifier entity : entities) {
      extPrincipalIdentifierMapper.insert(entity);
    }
  }

  @Override
  public void deleteByPrincipalId(Long principalId) {
    extPrincipalIdentifierMapper.delete(
        new LambdaQueryWrapper<ExtPrincipalIdentifier>()
            .eq(ExtPrincipalIdentifier::getPrincipalId, principalId));
  }
}
