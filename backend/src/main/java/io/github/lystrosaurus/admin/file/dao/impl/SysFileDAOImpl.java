package io.github.lystrosaurus.admin.file.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.file.dao.SysFileDAO;
import io.github.lystrosaurus.admin.file.entity.SysFile;
import io.github.lystrosaurus.admin.file.mapper.SysFileMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 文件数据访问对象实现 */
@Repository
@RequiredArgsConstructor
public class SysFileDAOImpl implements SysFileDAO {

  private final SysFileMapper sysFileMapper;

  @Override
  public Optional<SysFile> findById(Long id) {
    return Optional.ofNullable(sysFileMapper.selectById(id));
  }

  @Override
  public Page<SysFile> findPage(String filename, String bizType, int page, int size) {
    LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(filename)) {
      wrapper.like(SysFile::getOriginalName, filename);
    }
    if (StringUtils.hasText(bizType)) {
      wrapper.eq(SysFile::getBizType, bizType);
    }
    wrapper.orderByDesc(SysFile::getCreatedAt);
    return sysFileMapper.selectPage(new Page<>(page, size), wrapper);
  }

  @Override
  public void save(SysFile file) {
    sysFileMapper.insert(file);
  }

  @Override
  public void logicDelete(Long id) {
    // 依赖 BaseEntity 上的 @TableLogic，deleteById 会执行逻辑删除(置 deleted=1)
    sysFileMapper.deleteById(id);
  }

  @Override
  public Optional<SysFile> findByMd5(String md5) {
    if (!StringUtils.hasText(md5)) {
      return Optional.empty();
    }
    return Optional.ofNullable(
        sysFileMapper.selectOne(new LambdaQueryWrapper<SysFile>().eq(SysFile::getMd5, md5)));
  }
}
