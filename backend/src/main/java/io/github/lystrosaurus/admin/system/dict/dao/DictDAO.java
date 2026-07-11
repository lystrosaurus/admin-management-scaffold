package io.github.lystrosaurus.admin.system.dict.dao;

import io.github.lystrosaurus.admin.system.dict.entity.SysDict;
import java.util.List;

/**
 * 字典数据访问对象接口
 *
 * <p>定义 Service 需要的数据访问语义，而不是简单地暴露 Mapper 方法。
 */
public interface DictDAO {

  /**
   * 根据ID查找字典
   *
   * @param id 字典ID
   * @return 字典实体，不存在时返回null
   */
  SysDict findById(Long id);

  /**
   * 保存字典
   *
   * @param dict 字典实体
   */
  void save(SysDict dict);

  /**
   * 更新字典
   *
   * @param dict 字典实体
   */
  void update(SysDict dict);

  /**
   * 根据ID逻辑删除字典
   *
   * @param id 字典ID
   */
  void deleteById(Long id);

  /**
   * 按字典类型查询字典列表（dictType 为空时返回全部）
   *
   * @param dictType 字典类型（可选）
   * @return 字典列表
   */
  List<SysDict> list(String dictType);
}
