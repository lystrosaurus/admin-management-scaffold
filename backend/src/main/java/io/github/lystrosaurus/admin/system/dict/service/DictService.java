package io.github.lystrosaurus.admin.system.dict.service;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.system.dict.dao.DictDAO;
import io.github.lystrosaurus.admin.system.dict.dto.DictCreateDTO;
import io.github.lystrosaurus.admin.system.dict.dto.DictUpdateDTO;
import io.github.lystrosaurus.admin.system.dict.entity.SysDict;
import io.github.lystrosaurus.admin.system.dict.mapstruct.DictMapper;
import io.github.lystrosaurus.admin.system.dict.vo.DictVO;
import io.github.lystrosaurus.admin.common.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 字典服务实现 */
@Service
@RequiredArgsConstructor
public class DictService {

  private final DictDAO dictDAO;
  private final DictMapper dictMapper;

  @Transactional(rollbackFor = Exception.class)
  public DictVO create(DictCreateDTO dto) {
    // 转换并保存字典
    SysDict dict = new SysDict();
    dict.setDictType(dto.dictType());
    dict.setDictLabel(dto.dictLabel());
    dict.setDictValue(dto.dictValue());
    dict.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
    dict.setStatus(StringUtils.hasText(dto.status()) ? dto.status() : "ENABLED");
    dict.setRemark(dto.remark());
    dictDAO.save(dict);

    return dictMapper.toDictVO(dict);
  }

  @Transactional(rollbackFor = Exception.class)
  public DictVO update(Long id, DictUpdateDTO dto) {
    // 查找字典
    SysDict dict = dictDAO.findById(id);
    if (dict == null) {
      throw new BusinessException(ErrorCode.SYSTEM_404);
    }

    // 更新字段
    if (StringUtils.hasText(dto.dictType())) {
      dict.setDictType(dto.dictType());
    }
    if (StringUtils.hasText(dto.dictLabel())) {
      dict.setDictLabel(dto.dictLabel());
    }
    if (StringUtils.hasText(dto.dictValue())) {
      dict.setDictValue(dto.dictValue());
    }
    if (dto.sortOrder() != null) {
      dict.setSortOrder(dto.sortOrder());
    }
    if (StringUtils.hasText(dto.status())) {
      dict.setStatus(dto.status());
    }
    if (StringUtils.hasText(dto.remark())) {
      dict.setRemark(dto.remark());
    }

    dictDAO.update(dict);
    return dictMapper.toDictVO(dict);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    dictDAO.deleteById(id);
  }

  public PageResult<DictVO> list(String dictType, int page, int size) {
    Page<SysDict> pageParam = new Page<>(page, size);
    LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(dictType)) {
      wrapper.eq(SysDict::getDictType, dictType);
    }
    wrapper.orderByAsc(SysDict::getSortOrder);
    Page<SysDict> result = dictDAO.findPage(pageParam, wrapper);
    List<DictVO> vos = result.getRecords().stream().map(dictMapper::toDictVO).toList();
    return new PageResult<>(vos, result.getTotal(), page, size);
  }

  public DictVO getById(Long id) {
    SysDict dict = dictDAO.findById(id);
    if (dict == null) {
      throw new BusinessException(ErrorCode.SYSTEM_404);
    }
    return dictMapper.toDictVO(dict);
  }
}
