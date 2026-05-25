package io.github.lystrosaurus.admin.auth.oauth.state;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/** OAuth 状态服务实现（内存存储） */
@Service
public class OAuthStateService {

  private final ConcurrentHashMap<String, StateData> stateStore = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public OAuthStateService() {
    // 每分钟清理超过 5 分钟的条目
    scheduler.scheduleAtFixedRate(this::cleanupExpiredStates, 1, 1, TimeUnit.MINUTES);
  }

  public String generateState() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public void saveState(String state, String nonce) {
    saveState(state, nonce, null);
  }

  public void saveState(String state, String nonce, Long userId) {
    StateData stateData = new StateData(state, nonce, userId, LocalDateTime.now());
    stateStore.put(state, stateData);
  }

  public StateData validateAndConsumeState(String state) {
    StateData stateData = stateStore.remove(state);
    if (stateData == null) {
      throw new BusinessException(ErrorCode.OAUTH_STATE_INVALID);
    }
    return stateData;
  }

  /** 清理过期的状态数据 */
  private void cleanupExpiredStates() {
    LocalDateTime now = LocalDateTime.now();
    stateStore
        .entrySet()
        .removeIf(
            entry -> {
              long minutes = ChronoUnit.MINUTES.between(entry.getValue().createdAt(), now);
              return minutes > 5;
            });
  }
}
