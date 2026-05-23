package io.github.lystrosaurus.admin.integration.source.dao;

import io.github.lystrosaurus.admin.integration.source.entity.ExtSource;
import java.util.List;

/**
 * 外部身份源数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface ExtSourceDAO {

  /**
   * 根据ID查找外部身份源
   *
   * @param id ID
   * @return 外部身份源实体，不存在时返回null
   */
  ExtSource findById(Long id);

  /**
   * 根据编码查找外部身份源
   *
   * @param code 系统编码
   * @return 外部身份源实体，不存在时返回null
   */
  ExtSource findByCode(String code);

  /**
   * 保存外部身份源
   *
   * @param entity 外部身份源实体
   */
  void save(ExtSource entity);

  /**
   * 更新外部身份源
   *
   * @param entity 外部身份源实体
   */
  void update(ExtSource entity);

  /**
   * 根据ID删除外部身份源
   *
   * @param id ID
   */
  void deleteById(Long id);

  /**
   * 查询所有外部身份源（按优先级降序）
   *
   * @return 外部身份源列表
   */
  List<ExtSource> findAll();

  /**
   * 检查编码是否存在
   *
   * @param code 系统编码
   * @return 存在返回true，否则返回false
   */
  boolean existsByCode(String code);
}
