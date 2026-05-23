package io.github.lystrosaurus.admin.auth.external.service;

import io.github.lystrosaurus.admin.auth.external.dto.ExternalAccountBindDTO;
import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import java.util.List;

/** 三方账号服务接口 */
public interface ExternalAccountService {

  /**
   * 绑定三方账号
   *
   * @param dto 绑定DTO
   * @return 三方账号VO
   */
  ExternalAccountVO bind(ExternalAccountBindDTO dto);

  /**
   * 解绑三方账号
   *
   * @param id 三方账号ID
   */
  void unbind(Long id);

  /**
   * 根据ID获取三方账号
   *
   * @param id 三方账号ID
   * @return 三方账号VO
   */
  ExternalAccountVO getById(Long id);

  /**
   * 根据用户ID查询三方账号列表
   *
   * @param userId 用户ID
   * @return 三方账号列表
   */
  List<ExternalAccountVO> listByUserId(Long userId);

  /**
   * 根据员工ID查询三方账号列表
   *
   * @param employeeId 员工ID
   * @return 三方账号列表
   */
  List<ExternalAccountVO> listByEmployeeId(Long employeeId);

  /**
   * 更新最后登录时间
   *
   * @param id 三方账号ID
   */
  void updateLastLoginAt(Long id);
}
