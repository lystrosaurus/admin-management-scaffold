package io.github.lystrosaurus.admin.auth.oauth.state;

/** OAuth 状态服务接口 */
public interface OAuthStateService {

  /**
   * 生成随机 state
   *
   * @return state 值
   */
  String generateState();

  /**
   * 保存 state
   *
   * @param state 状态值
   * @param nonce 随机数
   */
  void saveState(String state, String nonce);

  /**
   * 保存 state（带用户ID）
   *
   * @param state 状态值
   * @param nonce 随机数
   * @param userId 用户ID
   */
  void saveState(String state, String nonce, Long userId);

  /**
   * 验证并消费 state（一次性使用）
   *
   * @param state 状态值
   * @return 状态数据
   * @throws io.github.lystrosaurus.admin.exception.BusinessException state 无效或已过期
   */
  StateData validateAndConsumeState(String state);
}
