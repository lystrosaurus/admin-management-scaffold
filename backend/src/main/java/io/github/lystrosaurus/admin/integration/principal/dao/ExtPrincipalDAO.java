package io.github.lystrosaurus.admin.integration.principal.dao;

import io.github.lystrosaurus.admin.integration.principal.entity.ExtPrincipal;
import java.util.List;

/**
 * 外部主体数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface ExtPrincipalDAO {

  /**
   * 根据ID查找外部主体
   *
   * @param id ID
   * @return 外部主体实体，不存在时返回null
   */
  ExtPrincipal findById(Long id);

  /**
   * 保存外部主体
   *
   * @param entity 外部主体实体
   */
  void save(ExtPrincipal entity);

  /**
   * 更新外部主体
   *
   * @param entity 外部主体实体
   */
  void update(ExtPrincipal entity);

  /**
   * 根据ID删除外部主体
   *
   * @param id ID
   */
  void deleteById(Long id);

  /**
   * 根据条件查询外部主体列表
   *
   * @param sourceId 外部系统ID（可选）
   * @param linkStatus 关联状态（可选）
   * @return 外部主体列表
   */
  List<ExtPrincipal> findByCondition(Long sourceId, String linkStatus);

  /**
   * 检查(sourceId, principalType, externalKey)唯一性
   *
   * @param sourceId 外部系统ID
   * @param principalType 主体类型
   * @param externalKey 外部系统主键
   * @return 存在返回true，否则返回false
   */
  boolean existsBySourcePrincipalExternalKey(
      Long sourceId, String principalType, String externalKey);
}
