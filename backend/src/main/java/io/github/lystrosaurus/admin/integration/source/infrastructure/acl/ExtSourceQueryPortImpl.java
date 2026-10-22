package io.github.lystrosaurus.admin.integration.source.infrastructure.acl;

import io.github.lystrosaurus.admin.integration.source.domain.acl.ExtSourceQueryPort;
import io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.mapper.ExtSourceMpMapper;
import io.github.lystrosaurus.admin.integration.source.infrastructure.persistence.po.ExtSourcePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 外部身份源查询端口的防腐层实现：向其它上下文暴露最小只读契约。 */
@Component
@RequiredArgsConstructor
public class ExtSourceQueryPortImpl implements ExtSourceQueryPort {

  private final ExtSourceMpMapper extSourceMpMapper;

  @Override
  public String findNameById(Long sourceId) {
    ExtSourcePO source = extSourceMpMapper.selectById(sourceId);
    return source == null ? null : source.getName();
  }
}
