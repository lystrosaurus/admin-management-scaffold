package io.github.lystrosaurus.admin.system.user.service;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.system.user.dto.ChangePasswordDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserCreateDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserQueryDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserUpdateDTO;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;

/** 用户服务接口 */
public interface UserService {

  /**
   * 创建用户
   *
   * @param dto 用户创建DTO
   * @return 用户VO
   */
  UserVO create(UserCreateDTO dto);

  /**
   * 更新用户信息
   *
   * @param id 用户ID
   * @param dto 用户更新DTO
   * @return 用户VO
   */
  UserVO update(Long id, UserUpdateDTO dto);

  /**
   * 删除用户
   *
   * @param id 用户ID
   */
  void deleteById(Long id);

  /**
   * 查询用户详情（含角色）
   *
   * @param id 用户ID
   * @return 用户详情VO
   */
  UserDetailVO findById(Long id);

  /**
   * 分页查询用户
   *
   * @param dto 查询条件
   * @param page 页码
   * @param size 每页大小
   * @return 分页结果
   */
  PageResult<UserVO> findPage(UserQueryDTO dto, int page, int size);

  /**
   * 修改密码
   *
   * @param id 用户ID
   * @param dto 修改密码DTO
   */
  void changePassword(Long id, ChangePasswordDTO dto);
}
