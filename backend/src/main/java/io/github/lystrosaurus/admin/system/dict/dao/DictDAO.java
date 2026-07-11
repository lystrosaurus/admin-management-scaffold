package io.github.lystrosaurus.admin.system.dict.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.system.dict.entity.SysDict;

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
   * 分页查询字典列表（dictType 为空时查询全部）
   *
   * @param page 分页参数
   * @param wrapper 查询条件
   * @return 分页结果
   */
  Page<SysDict> findPage(Page<SysDict> page, LambdaQueryWrapper<SysDict> wrapper);
}
