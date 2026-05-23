package io.github.lystrosaurus.admin.system.user.dao;

import io.github.lystrosaurus.admin.system.role.entity.SysRole;
import io.github.lystrosaurus.admin.system.user.entity.SysUser;
import java.util.List;

/**
 * 用户数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface UserDAO {

  /**
   * 根据ID查找用户
   *
   * @param id 用户ID
   * @return 用户实体，不存在时返回null
   */
  SysUser findById(Long id);

  /**
   * 根据用户名查找用户
   *
   * @param username 用户名
   * @return 用户实体，不存在时返回null
   */
  SysUser findByUsername(String username);

  /**
   * 保存用户
   *
   * @param user 用户实体
   */
  void save(SysUser user);

  /**
   * 更新用户
   *
   * @param user 用户实体
   */
  void update(SysUser user);

  /**
   * 根据ID删除用户
   *
   * @param id 用户ID
   */
  void deleteById(Long id);

  /**
   * 根据条件查找用户列表
   *
   * @param username 用户名（可选）
   * @param status 状态（可选）
   * @param page 页码
   * @param size 每页大小
   * @return 用户列表
   */
  List<SysUser> findByCondition(String username, String status, int page, int size);

  /**
   * 根据条件统计用户数量
   *
   * @param username 用户名（可选）
   * @param status 状态（可选）
   * @return 用户数量
   */
  long countByCondition(String username, String status);

  /**
   * 检查用户名是否存在
   *
   * @param username 用户名
   * @return 存在返回true，否则返回false
   */
  boolean existsByUsername(String username);

  /**
   * 检查用户名是否存在且排除指定ID
   *
   * @param username 用户名
   * @param id 排除的用户ID
   * @return 存在返回true，否则返回false
   */
  boolean existsByUsernameAndIdNot(String username, Long id);

  /**
   * 查找用户关联的角色
   *
   * @param userId 用户ID
   * @return 角色列表
   */
  List<SysRole> findRolesByUserId(Long userId);

  /**
   * 分配角色给用户
   *
   * @param userId 用户ID
   * @param roleIds 角色ID列表
   */
  void assignRoles(Long userId, List<Long> roleIds);

  /**
   * 移除用户的所有角色
   *
   * @param userId 用户ID
   */
  void removeRoles(Long userId);

  /**
   * 根据绑定员工ID查找用户
   *
   * @param employeeId 员工ID
   * @return 用户实体，不存在时返回null
   */
  SysUser findByEmployeeId(Long employeeId);
}
