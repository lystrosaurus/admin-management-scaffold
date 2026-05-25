package io.github.lystrosaurus.admin.integration.source.service;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.source.dao.ExtSourceDAO;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceCreateDTO;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceUpdateDTO;
import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import io.github.lystrosaurus.admin.integration.source.mapstruct.ExtSourceMapStruct;
import io.github.lystrosaurus.admin.integration.source.vo.ExtSourceVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 外部身份源服务实现 */
@Service
@RequiredArgsConstructor
public class ExtSourceService {

  private final ExtSourceDAO extSourceDAO;
  private final ExtSourceMapStruct extSourceMapStruct;

  @Transactional(rollbackFor = Exception.class)
  public ExtSourceVO create(ExtSourceCreateDTO dto) {
    // 检查编码唯一性
    if (extSourceDAO.existsByCode(dto.code())) {
      throw new BusinessException(ErrorCode.SOURCE_ALREADY_EXISTS);
    }

    // 转换并保存
    ExtSource entity = extSourceMapStruct.toEntity(dto);
    entity.setStatus("ENABLED");
    extSourceDAO.save(entity);

    return extSourceMapStruct.toVO(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public ExtSourceVO update(Long id, ExtSourceUpdateDTO dto) {
    // 查找外部身份源
    ExtSource entity = extSourceDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
    }

    // 合并更新
    extSourceMapStruct.updateEntity(dto, entity);
    extSourceDAO.update(entity);

    return extSourceMapStruct.toVO(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    // 检查外部身份源是否存在
    ExtSource entity = extSourceDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
    }

    // 逻辑删除
    extSourceDAO.deleteById(id);
  }

  public ExtSourceVO getById(Long id) {
    ExtSource entity = extSourceDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.SOURCE_NOT_FOUND);
    }

    return extSourceMapStruct.toVO(entity);
  }

  public List<ExtSourceVO> list() {
    return extSourceDAO.findAll().stream()
        .map(extSourceMapStruct::toVO)
        .toList();
  }
}
