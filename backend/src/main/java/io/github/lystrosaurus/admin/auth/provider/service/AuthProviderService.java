package io.github.lystrosaurus.admin.auth.provider.service;

import io.github.lystrosaurus.admin.auth.provider.dto.AuthProviderCreateDTO;
import io.github.lystrosaurus.admin.auth.provider.dto.AuthProviderUpdateDTO;
import io.github.lystrosaurus.admin.auth.provider.vo.AuthProviderVO;
import java.util.List;

/** 认证源服务接口 */
public interface AuthProviderService {

  /**
   * 创建认证源
   *
   * @param dto 创建DTO
   * @return 认证源VO
   */
  AuthProviderVO create(AuthProviderCreateDTO dto);

  /**
   * 更新认证源
   *
   * @param id 认证源ID
   * @param dto 更新DTO
   * @return 认证源VO
   */
  AuthProviderVO update(Long id, AuthProviderUpdateDTO dto);

  /**
   * 删除认证源（逻辑删除）
   *
   * @param id 认证源ID
   */
  void delete(Long id);

  /**
   * 根据ID获取认证源
   *
   * @param id 认证源ID
   * @return 认证源VO
   */
  AuthProviderVO getById(Long id);

  /**
   * 查询所有认证源
   *
   * @return 认证源列表
   */
  List<AuthProviderVO> list();

  /**
   * 根据编码获取认证源
   *
   * @param code 认证源编码
   * @return 认证源VO
   */
  AuthProviderVO getByCode(String code);

  /**
   * 根据编码获取已启用的认证源
   *
   * @param code 认证源编码
   * @return 认证源VO
   * @throws io.github.lystrosaurus.admin.exception.BusinessException 认证源不存在或已禁用
   */
  AuthProviderVO getEnabledByCode(String code);

  /**
   * 获取认证源的客户端密钥（V1 直接返回明文）
   *
   * @param code 认证源编码
   * @return 客户端密钥
   */
  String getClientSecret(String code);
}
