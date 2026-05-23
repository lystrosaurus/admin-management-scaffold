package io.github.lystrosaurus.admin.integration.principal.dao.impl;

import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalDAO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.mapper.ExtPrincipalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 外部主体数据访问对象实现（最小版本）
 *
 * <p>完整版本将由 Agent A 提供。
 */
@Service
@RequiredArgsConstructor
public class ExtPrincipalDAOImpl implements ExtPrincipalDAO {

  private final ExtPrincipalMapper extPrincipalMapper;

  @Override
  public void updateById(ExtPrincipal principal) {
    extPrincipalMapper.updateById(principal);
  }

  @Override
  public ExtPrincipal findById(Long id) {
    return extPrincipalMapper.selectById(id);
  }
}
