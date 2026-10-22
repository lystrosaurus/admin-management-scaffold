package io.github.lystrosaurus.admin.auth.infrastructure.oauth.state;

import io.github.lystrosaurus.admin.auth.domain.acl.OAuthStatePort;
import io.github.lystrosaurus.admin.auth.domain.model.StateData;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/** OAuth state 端口的 Redis 实现。 */
@Service
public class RedisOAuthStateAdapter implements OAuthStatePort {

  private static final String STATE_KEY_PREFIX = "oauth:state:";
  private static final long STATE_TTL_MINUTES = 5;

  private final RedisTemplate<String, StateData> redisTemplate;

  public RedisOAuthStateAdapter(
      @Qualifier("stateDataRedisTemplate") RedisTemplate<String, StateData> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public String generateState() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  @Override
  public void saveState(String state, String nonce) {
    StateData stateData = new StateData(state, nonce, null, LocalDateTime.now());
    redisTemplate
        .opsForValue()
        .set(STATE_KEY_PREFIX + state, stateData, STATE_TTL_MINUTES, TimeUnit.MINUTES);
  }

  @Override
  public StateData validateAndConsumeState(String state) {
    String key = STATE_KEY_PREFIX + state;
    StateData stateData = redisTemplate.opsForValue().getAndDelete(key);
    if (stateData == null) {
      throw new BusinessException(ErrorCode.OAUTH_STATE_INVALID);
    }
    return stateData;
  }
}
