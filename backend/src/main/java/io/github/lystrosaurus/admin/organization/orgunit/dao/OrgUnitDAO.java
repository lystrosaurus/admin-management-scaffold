package io.github.lystrosaurus.admin.organization.orgunit.dao;

import io.github.lystrosaurus.admin.organization.orgunit.entity.OrgUnit;
import java.util.List;

/** 组织单元数据访问对象接口 */
public interface OrgUnitDAO {

  /**
   * 根据ID查找组织单元
   *
   * @param id 组织单元ID
   * @return 组织单元实体，不存在时返回null
   */
  OrgUnit findById(Long id);

  /**
   * 根据编码查找组织单元
   *
   * @param code 组织编码
   * @return 组织单元实体，不存在时返回null
   */
  OrgUnit findByCode(String code);

  /**
   * 保存组织单元
   *
   * @param orgUnit 组织单元实体
   */
  void save(OrgUnit orgUnit);

  /**
   * 更新组织单元
   *
   * @param orgUnit 组织单元实体
   */
  void update(OrgUnit orgUnit);

  /**
   * 根据ID删除组织单元
   *
   * @param id 组织单元ID
   */
  void deleteById(Long id);

  /**
   * 根据条件查找组织单元列表
   *
   * @param keyword 关键词(模糊匹配名称/编码)
   * @param status 状态
   * @param parentId 父节点ID
   * @param page 页码
   * @param size 每页大小
   * @return 组织单元列表
   */
  List<OrgUnit> findByCondition(String keyword, String status, Long parentId, int page, int size);

  /**
   * 根据条件统计组织单元数量
   *
   * @param keyword 关键词
   * @param status 状态
   * @param parentId 父节点ID
   * @return 组织单元数量
   */
  long countByCondition(String keyword, String status, Long parentId);

  /**
   * 查找所有未删除的组织单元（用于树构建）
   *
   * @return 所有组织单元列表
   */
  List<OrgUnit> findAll();

  /**
   * 根据父节点ID查找子节点
   *
   * @param parentId 父节点ID
   * @return 子节点列表
   */
  List<OrgUnit> findByParentId(Long parentId);

  /**
   * 根据ID列表查找组织单元
   *
   * @param ids ID列表
   * @return 组织单元列表
   */
  List<OrgUnit> findByIds(List<Long> ids);

  /**
   * 检查编码是否存在
   *
   * @param code 组织编码
   * @return 存在返回true，否则返回false
   */
  boolean existsByCode(String code);

  /**
   * 检查编码是否存在且排除指定ID
   *
   * @param code 组织编码
   * @param id 排除的组织单元ID
   * @return 存在返回true，否则返回false
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * 检查是否有子节点
   *
   * @param id 组织单元ID
   * @return 有子节点返回true，否则返回false
   */
  boolean hasChildren(Long id);
}
