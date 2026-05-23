package io.github.lystrosaurus.admin.integration.identitylink.service.impl;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.integration.identitylink.dao.IdentityLinkCandidateDAO;
import io.github.lystrosaurus.admin.integration.identitylink.dto.IdentityLinkCandidateCreateDTO;
import io.github.lystrosaurus.admin.integration.identitylink.entity.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.mapstruct.IdentityLinkCandidateMapStruct;
import io.github.lystrosaurus.admin.integration.identitylink.service.IdentityLinkCandidateService;
import io.github.lystrosaurus.admin.integration.identitylink.vo.IdentityLinkCandidateVO;
import io.github.lystrosaurus.admin.integration.principal.dao.ExtPrincipalDAO;
import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 身份匹配候选服务实现 */
@Service
@RequiredArgsConstructor
public class IdentityLinkCandidateServiceImpl implements IdentityLinkCandidateService {

  private final IdentityLinkCandidateDAO candidateDAO;
  private final ExtPrincipalDAO extPrincipalDAO;
  private final IdentityLinkCandidateMapStruct candidateMapStruct;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public IdentityLinkCandidateVO create(IdentityLinkCandidateCreateDTO dto) {
    IdentityLinkCandidate entity = candidateMapStruct.toEntity(dto);
    entity.setStatus("PENDING");
    entity.setCreatedAt(LocalDateTime.now());
    candidateDAO.save(entity);
    return candidateMapStruct.toVO(entity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void confirm(Long id, String handledBy) {
    IdentityLinkCandidate candidate = candidateDAO.findById(id);
    if (candidate == null) {
      throw new BusinessException(ErrorCode.CANDIDATE_NOT_FOUND);
    }
    if (!"PENDING".equals(candidate.getStatus())) {
      throw new BusinessException(ErrorCode.CANDIDATE_ALREADY_HANDLED);
    }

    // 更新候选记录状态
    candidate.setStatus("CONFIRMED");
    candidate.setHandledBy(handledBy);
    candidate.setHandledAt(LocalDateTime.now());
    candidateDAO.updateById(candidate);

    // 更新外部主体的链接状态
    ExtPrincipal principal = new ExtPrincipal();
    principal.setId(candidate.getSourcePrincipalId());
    principal.setLinkStatus("MANUAL_LINKED");
    principal.setCanonicalType(candidate.getCandidateType());
    principal.setCanonicalId(String.valueOf(candidate.getCandidateId()));
    extPrincipalDAO.updateById(principal);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void reject(Long id, String handledBy) {
    IdentityLinkCandidate candidate = candidateDAO.findById(id);
    if (candidate == null) {
      throw new BusinessException(ErrorCode.CANDIDATE_NOT_FOUND);
    }
    if (!"PENDING".equals(candidate.getStatus())) {
      throw new BusinessException(ErrorCode.CANDIDATE_ALREADY_HANDLED);
    }

    candidate.setStatus("REJECTED");
    candidate.setHandledBy(handledBy);
    candidate.setHandledAt(LocalDateTime.now());
    candidateDAO.updateById(candidate);
  }

  @Override
  public List<IdentityLinkCandidateVO> listPending() {
    return candidateDAO.listPending().stream()
        .map(candidateMapStruct::toVO)
        .collect(Collectors.toList());
  }

  @Override
  public List<IdentityLinkCandidateVO> listByPrincipalId(Long principalId) {
    return candidateDAO.listByPrincipalId(principalId).stream()
        .map(candidateMapStruct::toVO)
        .collect(Collectors.toList());
  }
}
