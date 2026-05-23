package io.github.lystrosaurus.admin.integration.source.service;

import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceCreateDTO;
import io.github.lystrosaurus.admin.integration.source.dto.ExtSourceUpdateDTO;
import io.github.lystrosaurus.admin.integration.source.vo.ExtSourceVO;
import java.util.List;

/** 外部身份源服务接口 */
public interface ExtSourceService {

  /**
   * 创建外部身份源
   *
   * @param dto 创建DTO
   * @return VO
   */
  ExtSourceVO create(ExtSourceCreateDTO dto);

  /**
   * 更新外部身份源
   *
   * @param id ID
   * @param dto 更新DTO
   * @return VO
   */
  ExtSourceVO update(Long id, ExtSourceUpdateDTO dto);

  /**
   * 删除外部身份源
   *
   * @param id ID
   */
  void delete(Long id);

  /**
   * 根据ID查询外部身份源
   *
   * @param id ID
   * @return VO
   */
  ExtSourceVO getById(Long id);

  /**
   * 查询所有外部身份源（按优先级降序）
   *
   * @return VO列表
   */
  List<ExtSourceVO> list();
}
