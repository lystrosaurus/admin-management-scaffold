package io.github.lystrosaurus.admin.auth.oauth.state.impl;

import io.github.lystrosaurus.admin.auth.oauth.state.OAuthStateService;
import io.github.lystrosaurus.admin.auth.oauth.state.StateData;
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

/** 内存实现的 OAuth 状态服务 */
@Service
public class InMemoryOAuthStateService implements OAuthStateService {

  private final ConcurrentHashMap<String, StateData> stateStore = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public InMemoryOAuthStateService() {
    // 每分钟清理超过 5 分钟的条目
    scheduler.scheduleAtFixedRate(this::cleanupExpiredStates, 1, 1, TimeUnit.MINUTES);
  }

  @Override
  public String generateState() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  @Override
  public void saveState(String state, String nonce) {
    saveState(state, nonce, null);
  }

  @Override
  public void saveState(String state, String nonce, Long userId) {
    StateData stateData = new StateData(state, nonce, userId, LocalDateTime.now());
    stateStore.put(state, stateData);
  }

  @Override
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
