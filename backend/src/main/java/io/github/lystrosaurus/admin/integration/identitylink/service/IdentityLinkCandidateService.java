package io.github.lystrosaurus.admin.integration.identitylink.service;

import io.github.lystrosaurus.admin.integration.identitylink.dto.IdentityLinkCandidateCreateDTO;
import io.github.lystrosaurus.admin.integration.identitylink.vo.IdentityLinkCandidateVO;
import java.util.List;

/** 身份匹配候选服务接口 */
public interface IdentityLinkCandidateService {

  /**
   * 创建候选记录
   *
   * @param dto 创建DTO
   * @return 候选VO
   */
  IdentityLinkCandidateVO create(IdentityLinkCandidateCreateDTO dto);

  /**
   * 确认候选记录
   *
   * <p>更新候选状态为 CONFIRMED，同时更新对应外部主体的链接状态。
   *
   * @param id 候选ID
   * @param handledBy 处理人
   */
  void confirm(Long id, String handledBy);

  /**
   * 拒绝候选记录
   *
   * @param id 候选ID
   * @param handledBy 处理人
   */
  void reject(Long id, String handledBy);

  /**
   * 查询待处理的候选记录列表
   *
   * @return 待处理候选列表
   */
  List<IdentityLinkCandidateVO> listPending();

  /**
   * 根据外部主体ID查询候选记录列表
   *
   * @param principalId 外部主体ID
   * @return 候选列表
   */
  List<IdentityLinkCandidateVO> listByPrincipalId(Long principalId);
}
