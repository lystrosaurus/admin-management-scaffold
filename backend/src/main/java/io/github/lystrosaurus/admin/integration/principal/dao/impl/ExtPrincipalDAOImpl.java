package io.github.lystrosaurus.admin.integration.principal.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalDAO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.mapper.ExtPrincipalMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 外部主体数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class ExtPrincipalDAOImpl implements ExtPrincipalDAO {

  private final ExtPrincipalMapper extPrincipalMapper;

  @Override
  public ExtPrincipal findById(Long id) {
    return extPrincipalMapper.selectById(id);
  }

  @Override
  public void save(ExtPrincipal entity) {
    extPrincipalMapper.insert(entity);
  }

  @Override
  public void update(ExtPrincipal entity) {
    extPrincipalMapper.updateById(entity);
  }

  @Override
  public void deleteById(Long id) {
    extPrincipalMapper.deleteById(id);
  }

  @Override
  public List<ExtPrincipal> findByCondition(Long sourceId, String linkStatus) {
    LambdaQueryWrapper<ExtPrincipal> wrapper = new LambdaQueryWrapper<>();

    if (sourceId != null) {
      wrapper.eq(ExtPrincipal::getSourceId, sourceId);
    }

    if (StringUtils.hasText(linkStatus)) {
      wrapper.eq(ExtPrincipal::getLinkStatus, linkStatus);
    }

    wrapper.orderByDesc(ExtPrincipal::getCreatedAt);
    return extPrincipalMapper.selectList(wrapper);
  }

  @Override
  public boolean existsBySourcePrincipalExternalKey(
      Long sourceId, String principalType, String externalKey) {
    return extPrincipalMapper.selectCount(
            new LambdaQueryWrapper<ExtPrincipal>()
                .eq(ExtPrincipal::getSourceId, sourceId)
                .eq(ExtPrincipal::getPrincipalType, principalType)
                .eq(ExtPrincipal::getExternalKey, externalKey))
        > 0;
  }
}
