package io.github.lystrosaurus.admin.integration.identitylink.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.lystrosaurus.admin.integration.identitylink.dao.IdentityLinkCandidateDAO;
import io.github.lystrosaurus.admin.integration.identitylink.entity.IdentityLinkCandidate;
import io.github.lystrosaurus.admin.integration.identitylink.mapper.IdentityLinkCandidateMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 身份匹配候选数据访问对象实现 */
@Service
@RequiredArgsConstructor
public class IdentityLinkCandidateDAOImpl implements IdentityLinkCandidateDAO {

  private final IdentityLinkCandidateMapper candidateMapper;

  @Override
  public void save(IdentityLinkCandidate candidate) {
    candidateMapper.insert(candidate);
  }

  @Override
  public IdentityLinkCandidate findById(Long id) {
    return candidateMapper.selectById(id);
  }

  @Override
  public void updateById(IdentityLinkCandidate candidate) {
    candidateMapper.updateById(candidate);
  }

  @Override
  public List<IdentityLinkCandidate> listPending() {
    return candidateMapper.selectList(
        new LambdaQueryWrapper<IdentityLinkCandidate>()
            .eq(IdentityLinkCandidate::getStatus, "PENDING")
            .orderByDesc(IdentityLinkCandidate::getCreatedAt));
  }

  @Override
  public List<IdentityLinkCandidate> listByPrincipalId(Long principalId) {
    return candidateMapper.selectList(
        new LambdaQueryWrapper<IdentityLinkCandidate>()
            .eq(IdentityLinkCandidate::getSourcePrincipalId, principalId)
            .orderByDesc(IdentityLinkCandidate::getCreatedAt));
  }
}
