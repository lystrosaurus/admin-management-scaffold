package io.github.lystrosaurus.admin.file.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.file.domain.acl.FileRepository;
import io.github.lystrosaurus.admin.file.domain.model.SysFile;
import io.github.lystrosaurus.admin.file.infrastructure.converter.SysFilePoConverter;
import io.github.lystrosaurus.admin.file.infrastructure.persistence.mapper.SysFileMapper;
import io.github.lystrosaurus.admin.file.infrastructure.persistence.po.SysFilePO;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 文件仓储的 MyBatis-Plus 实现。 */
@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

  private final SysFileMapper mapper;
  private final SysFilePoConverter converter;

  @Override
  public Optional<SysFile> findById(Long id) {
    return Optional.ofNullable(mapper.selectById(id)).map(converter::toDomain);
  }

  @Override
  public Page<SysFile> findPage(String filename, String bizType, PageQuery pageQuery) {
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysFilePO> mpPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
            pageQuery.pageNum(), pageQuery.pageSize());
    LambdaQueryWrapper<SysFilePO> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(filename)) {
      wrapper.like(SysFilePO::getOriginalName, filename);
    }
    if (StringUtils.hasText(bizType)) {
      wrapper.eq(SysFilePO::getBizType, bizType);
    }
    wrapper.orderByDesc(SysFilePO::getCreatedAt);
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysFilePO> result =
        mapper.selectPage(mpPage, wrapper);
    return new Page<>(
        result.getRecords().stream().map(converter::toDomain).toList(),
        result.getTotal(),
        pageQuery.pageNum(),
        pageQuery.pageSize());
  }

  @Override
  public SysFile save(SysFile file) {
    SysFilePO po = converter.toPo(file);
    if (po.getId() == null) {
      mapper.insert(po);
    } else {
      mapper.updateById(po);
    }
    return converter.toDomain(po);
  }

  @Override
  public void logicDelete(Long id) {
    // 依赖 SysFilePO 上的 @TableLogic，deleteById 会执行逻辑删除(置 deleted=1)
    mapper.deleteById(id);
  }

  @Override
  public Optional<SysFile> findByMd5(String md5) {
    if (!StringUtils.hasText(md5)) {
      return Optional.empty();
    }
    return Optional.ofNullable(
            mapper.selectOne(
                new LambdaQueryWrapper<SysFilePO>().eq(SysFilePO::getMd5, md5)))
        .map(converter::toDomain);
  }
}
