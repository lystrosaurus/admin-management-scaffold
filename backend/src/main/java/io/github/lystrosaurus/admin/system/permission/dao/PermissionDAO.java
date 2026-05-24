package io.github.lystrosaurus.admin.system.permission.dao;

import io.github.lystrosaurus.admin.system.permission.entity.SysPermission;
import java.util.List;

/**
 * 权限数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface PermissionDAO {

  /**
   * 根据ID查找权限
   *
   * @param id 权限ID
   * @return 权限实体，不存在时返回null
   */
  SysPermission findById(Long id);

  /**
   * 根据权限编码查找权限
   *
   * @param code 权限编码
   * @return 权限实体，不存在时返回null
   */
  SysPermission findByCode(String code);

  /**
   * 保存权限
   *
   * @param permission 权限实体
   */
  void save(SysPermission permission);

  /**
   * 更新权限
   *
   * @param permission 权限实体
   */
  void update(SysPermission permission);

  /**
   * 根据ID删除权限
   *
   * @param id 权限ID
   */
  void deleteById(Long id);

  /**
   * 根据条件查找权限列表
   *
   * @param module 模块（可选）
   * @param type 类型（可选）
   * @param page 页码
   * @param size 每页大小
   * @return 权限列表
   */
  List<SysPermission> findByCondition(String module, String type, int page, int size);

  /**
   * 根据条件统计权限数量
   *
   * @param module 模块（可选）
   * @param type 类型（可选）
   * @return 权限数量
   */
  long countByCondition(String module, String type);

  /**
   * 根据模块查找权限列表
   *
   * @param module 模块
   * @return 权限列表
   */
  List<SysPermission> findByModule(String module);

  /**
   * 根据角色ID查找权限列表
   *
   * @param roleId 角色ID
   * @return 权限列表
   */
  List<SysPermission> findByRoleId(Long roleId);

  /**
   * 根据用户ID查找权限列表
   *
   * @param userId 用户ID
   * @return 权限列表
   */
  List<SysPermission> findByUserId(Long userId);

  /**
   * 检查权限编码是否存在
   *
   * @param code 权限编码
   * @return 存在返回true，否则返回false
   */
  boolean existsByCode(String code);

  /**
   * 检查权限编码是否存在且排除指定ID
   *
   * @param code 权限编码
   * @param id 排除的权限ID
   * @return 存在返回true，否则返回false
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * 查询所有权限
   *
   * @return 全部权限列表
   */
  List<SysPermission> findAll();
}
