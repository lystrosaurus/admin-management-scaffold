package io.github.lystrosaurus.admin.system.user.controller;

import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.system.user.dto.ChangePasswordDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserCreateDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserQueryDTO;
import io.github.lystrosaurus.admin.system.user.dto.UserUpdateDTO;
import io.github.lystrosaurus.admin.system.user.service.UserService;
import io.github.lystrosaurus.admin.system.user.vo.UserDetailVO;
import io.github.lystrosaurus.admin.system.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * <p>提供用户管理相关接口
 */
@RestController
@RequestMapping("/app/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * 创建用户
   *
   * @param dto 用户创建DTO
   * @return 用户VO
   */
  @PostMapping
  public ApiResponse<UserVO> create(@RequestBody @Valid UserCreateDTO dto) {
    UserVO userVO = userService.create(dto);
    return ApiResponse.success(userVO);
  }

  /**
   * 获取用户详情
   *
   * @param id 用户ID
   * @return 用户详情VO
   */
  @GetMapping("/{id}")
  public ApiResponse<UserDetailVO> findById(@PathVariable Long id) {
    UserDetailVO userDetailVO = userService.findById(id);
    return ApiResponse.success(userDetailVO);
  }

  /**
   * 更新用户
   *
   * @param id 用户ID
   * @param dto 用户更新DTO
   * @return 用户VO
   */
  @PutMapping("/{id}")
  public ApiResponse<UserVO> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
    UserVO userVO = userService.update(id, dto);
    return ApiResponse.success(userVO);
  }

  /**
   * 删除用户
   *
   * @param id 用户ID
   * @return 空响应
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteById(@PathVariable Long id) {
    userService.deleteById(id);
    return ApiResponse.success();
  }

  /**
   * 分页查询用户
   *
   * @param page 页码
   * @param size 每页大小
   * @param username 用户名
   * @param status 状态
   * @param keyword 关键词
   * @return 分页结果
   */
  @GetMapping
  public ApiResponse<PageResult<UserVO>> findPage(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String keyword) {
    UserQueryDTO queryDTO = new UserQueryDTO(username, status, keyword);
    PageResult<UserVO> pageResult = userService.findPage(queryDTO, page, size);
    return ApiResponse.success(pageResult);
  }

  /**
   * 修改密码
   *
   * @param id 用户ID
   * @param dto 修改密码DTO
   * @return 空响应
   */
  @PutMapping("/{id}/password")
  public ApiResponse<Void> changePassword(
      @PathVariable Long id, @RequestBody @Valid ChangePasswordDTO dto) {
    userService.changePassword(id, dto);
    return ApiResponse.success();
  }
}
