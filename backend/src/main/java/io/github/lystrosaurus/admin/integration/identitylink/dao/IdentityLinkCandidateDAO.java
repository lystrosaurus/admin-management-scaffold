package io.github.lystrosaurus.admin.integration.identitylink.dao;

import io.github.lystrosaurus.admin.integration.identitylink.entity.IdentityLinkCandidate;
import java.util.List;

/** 身份匹配候选数据访问对象接口 */
public interface IdentityLinkCandidateDAO {

  /**
   * 保存候选记录
   *
   * @param candidate 候选实体
   */
  void save(IdentityLinkCandidate candidate);

  /**
   * 根据ID查找候选记录
   *
   * @param id 候选ID
   * @return 候选实体，不存在时返回null
   */
  IdentityLinkCandidate findById(Long id);

  /**
   * 更新候选记录
   *
   * @param candidate 候选实体
   */
  void updateById(IdentityLinkCandidate candidate);

  /**
   * 查询待处理的候选记录列表
   *
   * @return 待处理候选列表
   */
  List<IdentityLinkCandidate> listPending();

  /**
   * 根据外部主体ID查询候选记录列表
   *
   * @param principalId 外部主体ID
   * @return 候选列表
   */
  List<IdentityLinkCandidate> listByPrincipalId(Long principalId);
}
