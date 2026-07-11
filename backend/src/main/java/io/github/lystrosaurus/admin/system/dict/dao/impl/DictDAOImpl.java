package io.github.lystrosaurus.admin.system.dict.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.system.dict.dao.DictDAO;
import io.github.lystrosaurus.admin.system.dict.entity.SysDict;
import io.github.lystrosaurus.admin.system.dict.mapper.SysDictMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 字典数据访问对象实现
 *
 * <p>使用 MyBatis-Plus Wrapper 构建查询条件。
 */
@Service
@RequiredArgsConstructor
public class DictDAOImpl implements DictDAO {

  private final SysDictMapper dictMapper;

  @Override
  public SysDict findById(Long id) {
    return dictMapper.selectById(id);
  }

  @Override
  public void save(SysDict dict) {
    dictMapper.insert(dict);
  }

  @Override
  public void update(SysDict dict) {
    dictMapper.updateById(dict);
  }

  @Override
  public void deleteById(Long id) {
    dictMapper.deleteById(id);
  }

  @Override
  public List<SysDict> list(String dictType) {
    LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(dictType)) {
      wrapper.eq(SysDict::getDictType, dictType);
    }
    wrapper.orderByAsc(SysDict::getSortOrder);
    return dictMapper.selectList(wrapper);
  }
}
