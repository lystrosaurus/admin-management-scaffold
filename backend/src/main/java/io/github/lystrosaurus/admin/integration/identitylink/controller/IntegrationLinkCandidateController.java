package io.github.lystrosaurus.admin.integration.identitylink.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.integration.identitylink.service.IdentityLinkCandidateService;
import io.github.lystrosaurus.admin.integration.identitylink.vo.IdentityLinkCandidateVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 身份匹配候选控制器
 *
 * <p>提供身份匹配候选的查询、确认和拒绝接口
 */
@RestController
@RequestMapping("/app/integration/link-candidates")
@RequiredArgsConstructor
public class IntegrationLinkCandidateController {

  private final IdentityLinkCandidateService candidateService;

  /**
   * 查询待处理的候选记录列表
   *
   * @return 待处理候选列表
   */
  @GetMapping("/pending")
  public ApiResponse<List<IdentityLinkCandidateVO>> listPending() {
    List<IdentityLinkCandidateVO> candidates = candidateService.listPending();
    return ApiResponse.success(candidates);
  }

  /**
   * 根据外部主体ID查询候选记录列表
   *
   * @param principalId 外部主体ID
   * @return 候选列表
   */
  @GetMapping("/by-principal/{principalId}")
  public ApiResponse<List<IdentityLinkCandidateVO>> listByPrincipalId(
      @PathVariable Long principalId) {
    List<IdentityLinkCandidateVO> candidates = candidateService.listByPrincipalId(principalId);
    return ApiResponse.success(candidates);
  }

  /**
   * 确认候选记录
   *
   * @param id 候选ID
   * @return 空响应
   */
  @PostMapping("/{id}/confirm")
  public ApiResponse<Void> confirm(@PathVariable Long id) {
    String handledBy = StpUtil.getLoginIdAsString();
    candidateService.confirm(id, handledBy);
    return ApiResponse.success();
  }

  /**
   * 拒绝候选记录
   *
   * @param id 候选ID
   * @return 空响应
   */
  @PostMapping("/{id}/reject")
  public ApiResponse<Void> reject(@PathVariable Long id) {
    String handledBy = StpUtil.getLoginIdAsString();
    candidateService.reject(id, handledBy);
    return ApiResponse.success();
  }
}
