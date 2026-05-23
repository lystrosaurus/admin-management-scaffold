package io.github.lystrosaurus.admin.integration.principal.service;

import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalCreateDTO;
import io.github.lystrosaurus.admin.integration.principal.dto.ExtPrincipalUpdateDTO;
import io.github.lystrosaurus.admin.integration.principal.vo.ExtPrincipalVO;
import java.util.List;

/** 外部主体服务接口 */
public interface ExtPrincipalService {

  /**
   * 创建外部主体（含标识符）
   *
   * @param dto 创建DTO
   * @return VO
   */
  ExtPrincipalVO create(ExtPrincipalCreateDTO dto);

  /**
   * 更新外部主体
   *
   * @param id ID
   * @param dto 更新DTO
   * @return VO
   */
  ExtPrincipalVO update(Long id, ExtPrincipalUpdateDTO dto);

  /**
   * 删除外部主体（含标识符）
   *
   * @param id ID
   */
  void delete(Long id);

  /**
   * 根据ID查询外部主体（含标识符）
   *
   * @param id ID
   * @return VO
   */
  ExtPrincipalVO getById(Long id);

  /**
   * 根据条件查询外部主体列表
   *
   * @param sourceId 外部系统ID（可选）
   * @param linkStatus 关联状态（可选）
   * @return VO列表
   */
  List<ExtPrincipalVO> list(Long sourceId, String linkStatus);

  /**
   * 更新关联状态
   *
   * @param id ID
   * @param canonicalType 映射目标类型
   * @param canonicalId 映射目标ID
   * @param linkStatus 关联状态
   */
  void updateLinkStatus(Long id, String canonicalType, Long canonicalId, String linkStatus);
}
