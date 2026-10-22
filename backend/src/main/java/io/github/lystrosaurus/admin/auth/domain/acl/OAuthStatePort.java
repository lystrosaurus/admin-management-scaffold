package io.github.lystrosaurus.admin.auth.domain.acl;

import io.github.lystrosaurus.admin.auth.domain.model.StateData;

/** OAuth state 存取端口：生成、保存与一次性消费校验。 */
public interface OAuthStatePort {

  /** 生成 state */
  String generateState();

  /** 保存 state */
  void saveState(String state, String nonce);

  /** 校验并一次性消费 state */
  StateData validateAndConsumeState(String state);
}
