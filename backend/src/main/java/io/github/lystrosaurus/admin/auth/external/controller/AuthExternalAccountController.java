package io.github.lystrosaurus.admin.auth.external.controller;

import io.github.lystrosaurus.admin.auth.external.dto.ExternalAccountBindDTO;
import io.github.lystrosaurus.admin.auth.external.service.ExternalAccountService;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.common.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 三方账号控制器
 *
 * <p>提供三方账号绑定、解绑和查询接口
 */
@RestController
@RequestMapping("/app/auth/external-accounts")
@RequiredArgsConstructor
public class AuthExternalAccountController {

  private final ExternalAccountService externalAccountService;

  /**
   * 绑定三方账号
   *
   * @param dto 绑定DTO
   * @return 三方账号VO
   */
  @PostMapping("/bind")
  public ApiResponse<ExternalAccountVO> bind(@RequestBody ExternalAccountBindDTO dto) {
    ExternalAccountVO vo = externalAccountService.bind(dto);
    return ApiResponse.success(vo);
  }

  /**
   * 解绑三方账号
   *
   * @param id 三方账号ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> unbind(@PathVariable Long id) {
    externalAccountService.unbind(id);
    return ApiResponse.success();
  }

  /**
   * 根据ID获取三方账号
   *
   * @param id 三方账号ID
   * @return 三方账号VO
   */
  @GetMapping("/{id}")
  public ApiResponse<ExternalAccountVO> findById(@PathVariable Long id) {
    ExternalAccountVO vo = externalAccountService.getById(id);
    return ApiResponse.success(vo);
  }

  /**
   * 根据用户ID查询三方账号列表
   *
   * @param userId 用户ID
   * @return 三方账号列表
   */
  @GetMapping("/by-user/{userId}")
  public ApiResponse<List<ExternalAccountVO>> listByUserId(@PathVariable Long userId) {
    List<ExternalAccountVO> accounts = externalAccountService.listByUserId(userId);
    return ApiResponse.success(accounts);
  }

  /**
   * 根据员工ID查询三方账号列表
   *
   * @param employeeId 员工ID
   * @return 三方账号列表
   */
  @GetMapping("/by-employee/{employeeId}")
  public ApiResponse<List<ExternalAccountVO>> listByEmployeeId(@PathVariable Long employeeId) {
    List<ExternalAccountVO> accounts = externalAccountService.listByEmployeeId(employeeId);
    return ApiResponse.success(accounts);
  }
}
