package io.github.lystrosaurus.admin.integration.source.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.integration.source.dao.ExtSourceDAO;
import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import io.github.lystrosaurus.admin.integration.source.mapper.ExtSourceMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 外部身份源数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class ExtSourceDAOImpl implements ExtSourceDAO {

  private final ExtSourceMapper extSourceMapper;

  @Override
  public ExtSource findById(Long id) {
    return extSourceMapper.selectById(id);
  }

  @Override
  public ExtSource findByCode(String code) {
    return extSourceMapper.selectOne(
        new LambdaQueryWrapper<ExtSource>().eq(ExtSource::getCode, code));
  }

  @Override
  public void save(ExtSource entity) {
    extSourceMapper.insert(entity);
  }

  @Override
  public void update(ExtSource entity) {
    extSourceMapper.updateById(entity);
  }

  @Override
  public void deleteById(Long id) {
    extSourceMapper.deleteById(id);
  }

  @Override
  public List<ExtSource> findAll() {
    return extSourceMapper.selectList(
        new LambdaQueryWrapper<ExtSource>().orderByDesc(ExtSource::getPriority));
  }

  @Override
  public boolean existsByCode(String code) {
    return extSourceMapper.selectCount(
            new LambdaQueryWrapper<ExtSource>().eq(ExtSource::getCode, code))
        > 0;
  }
}
