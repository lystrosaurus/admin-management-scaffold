package io.github.lystrosaurus.admin.integration.principal.service;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalDAO;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalIdentifierDAO;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalCreateDTO;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalUpdateDTO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipalIdentifier;
import io.github.lystrosaurus.admin.integration.principal.mapstruct.ExtPrincipalMapStruct;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalIdentifierVO;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalVO;
import io.github.lystrosaurus.admin.integration.source.dao.ExtSourceDAO;
import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 外部主体服务实现 */
@Service
@RequiredArgsConstructor
public class ExtPrincipalService {

  private final ExtPrincipalDAO extPrincipalDAO;
  private final ExtPrincipalIdentifierDAO extPrincipalIdentifierDAO;
  private final ExtSourceDAO extSourceDAO;
  private final ExtPrincipalMapStruct extPrincipalMapStruct;

  @Transactional(rollbackFor = Exception.class)
  public ExtPrincipalVO create(ExtPrincipalCreateDTO dto) {
    // 检查唯一性
    if (extPrincipalDAO.existsBySourcePrincipalExternalKey(
        dto.sourceId(), dto.principalType(), dto.externalKey())) {
      throw new BusinessException(ErrorCode.PRINCIPAL_ALREADY_EXISTS);
    }

    // 转换并保存主体
    ExtPrincipal entity = extPrincipalMapStruct.toEntity(dto);
    entity.setStatus("ACTIVE");
    entity.setLinkStatus("UNLINKED");
    extPrincipalDAO.save(entity);

    // 保存标识符
    if (dto.identifiers() != null && !dto.identifiers().isEmpty()) {
      List<ExtPrincipalIdentifier> identifiers = new ArrayList<>();
      for (ExtPrincipalCreateDTO.IdentifierItem item : dto.identifiers()) {
        ExtPrincipalIdentifier identifier = new ExtPrincipalIdentifier();
        identifier.setPrincipalId(entity.getId());
        identifier.setIdType(item.idType());
        identifier.setIdValue(item.idValue());
        identifier.setIsPrimary(Boolean.TRUE.equals(item.isPrimary()) ? 1 : 0);
        identifiers.add(identifier);
      }
      extPrincipalIdentifierDAO.saveBatch(identifiers);
    }

    return buildVO(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public ExtPrincipalVO update(Long id, ExtPrincipalUpdateDTO dto) {
    // 查找外部主体
    ExtPrincipal entity = extPrincipalDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.PRINCIPAL_NOT_FOUND);
    }

    // 合并更新
    extPrincipalMapStruct.updateEntity(dto, entity);
    extPrincipalDAO.update(entity);

    return buildVO(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    // 检查外部主体是否存在
    ExtPrincipal entity = extPrincipalDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.PRINCIPAL_NOT_FOUND);
    }

    // 先删标识符
    extPrincipalIdentifierDAO.deleteByPrincipalId(id);

    // 再删主体
    extPrincipalDAO.deleteById(id);
  }

  public ExtPrincipalVO getById(Long id) {
    ExtPrincipal entity = extPrincipalDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.PRINCIPAL_NOT_FOUND);
    }

    return buildVO(entity);
  }

  public List<ExtPrincipalVO> list(Long sourceId, String linkStatus) {
    return extPrincipalDAO.findByCondition(sourceId, linkStatus).stream()
        .map(this::buildVO)
        .toList();
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateLinkStatus(Long id, String canonicalType, Long canonicalId, String linkStatus) {
    // 查找外部主体
    ExtPrincipal entity = extPrincipalDAO.findById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.PRINCIPAL_NOT_FOUND);
    }

    // 更新关联状态
    entity.setCanonicalType(canonicalType);
    entity.setCanonicalId(canonicalId);
    entity.setLinkStatus(linkStatus);
    extPrincipalDAO.update(entity);
  }

  /**
   * 构建VO（含标识符和sourceName）
   *
   * @param entity 外部主体实体
   * @return VO
   */
  private ExtPrincipalVO buildVO(ExtPrincipal entity) {
    // 查询标识符
    List<ExtPrincipalIdentifier> identifiers =
        extPrincipalIdentifierDAO.findByPrincipalId(entity.getId());
    List<ExtPrincipalIdentifierVO> identifierVOs =
        extPrincipalMapStruct.toIdentifierVOList(identifiers);

    // 查询sourceName
    String sourceName = null;
    ExtSource source = extSourceDAO.findById(entity.getSourceId());
    if (source != null) {
      sourceName = source.getName();
    }

    // 构建VO
    ExtPrincipalVO vo = extPrincipalMapStruct.toVO(entity);
    return new ExtPrincipalVO(
        vo.id(),
        vo.sourceId(),
        sourceName,
        vo.principalType(),
        vo.externalKey(),
        vo.displayName(),
        vo.status(),
        vo.lastSyncAt(),
        vo.canonicalType(),
        vo.canonicalId(),
        vo.linkStatus(),
        vo.createdAt(),
        vo.updatedAt(),
        identifierVOs);
  }
}
