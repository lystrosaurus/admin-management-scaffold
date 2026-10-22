package io.github.lystrosaurus.admin.system.dict.infrastructure.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.system.dict.domain.acl.DictQueryRepository;
import io.github.lystrosaurus.admin.system.dict.domain.model.Dict;
import io.github.lystrosaurus.admin.system.dict.infrastructure.converter.DictPoConverter;
import io.github.lystrosaurus.admin.system.dict.infrastructure.persistence.mapper.DictMpMapper;
import io.github.lystrosaurus.admin.system.dict.infrastructure.persistence.po.DictPO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 字典查询端口的 MyBatis-Plus 实现（读模型侧）。 */
@Repository
@RequiredArgsConstructor
public class DictQueryRepositoryImpl implements DictQueryRepository {

  private final DictMpMapper dictMpMapper;
  private final DictPoConverter converter;

  @Override
  public Page<Dict> findPage(String dictType, PageQuery pageQuery) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<DictPO> mpPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
            pageQuery.pageNum(), pageQuery.pageSize());
    LambdaQueryWrapper<DictPO> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(dictType)) {
      wrapper.eq(DictPO::getDictType, dictType);
    }
    wrapper.orderByAsc(DictPO::getSortOrder);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<DictPO> result =
        dictMpMapper.selectPage(mpPage, wrapper);
    List<Dict> records = result.getRecords().stream().map(converter::toDomain).toList();
    return new Page<>(records, result.getTotal(), pageQuery.pageNum(), pageQuery.pageSize());
  }
}
