package io.github.lystrosaurus.admin.auth.oauth.state;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/** OAuth 状态服务实现（Redis 存储） */
@Service
@RequiredArgsConstructor
public class OAuthStateService {

  private static final String STATE_KEY_PREFIX = "oauth:state:";
  private static final long STATE_TTL_MINUTES = 5;

  private final RedisTemplate<String, StateData> redisTemplate;

  public String generateState() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public void saveState(String state, String nonce) {
    saveState(state, nonce, null);
  }

  public void saveState(String state, String nonce, Long userId) {
    StateData stateData = new StateData(state, nonce, userId, LocalDateTime.now());
    redisTemplate
        .opsForValue()
        .set(STATE_KEY_PREFIX + state, stateData, STATE_TTL_MINUTES, TimeUnit.MINUTES);
  }

  public StateData validateAndConsumeState(String state) {
    String key = STATE_KEY_PREFIX + state;
    StateData stateData = redisTemplate.opsForValue().getAndDelete(key);
    if (stateData == null) {
      throw new BusinessException(ErrorCode.OAUTH_STATE_INVALID);
    }
    return stateData;
  }
}
