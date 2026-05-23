package io.github.lystrosaurus.admin.auth.oauth.state;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.auth.oauth.state.impl.InMemoryOAuthStateService;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** OAuthStateService 单元测试 */
@DisplayName("OAuthStateService 测试")
class OAuthStateServiceImplTest {

  private OAuthStateService stateService;

  @BeforeEach
  void setUp() {
    stateService = new InMemoryOAuthStateService();
  }

  @Test
  @DisplayName("应该正常保存和消费 state")
  void should_save_and_consume_state_successfully() {
    // Given
    String state = "test-state-123";
    String nonce = "test-nonce-456";

    // When
    stateService.saveState(state, nonce);
    StateData result = stateService.validateAndConsumeState(state);

    // Then
    assertNotNull(result);
    assertEquals(state, result.state());
    assertEquals(nonce, result.nonce());
    assertNull(result.userId());
    assertNotNull(result.createdAt());
  }

  @Test
  @DisplayName("应该正常保存和消费带 userId 的 state")
  void should_save_and_consume_state_with_userId() {
    // Given
    String state = "test-state-789";
    String nonce = "test-nonce-012";
    Long userId = 100L;

    // When
    stateService.saveState(state, nonce, userId);
    StateData result = stateService.validateAndConsumeState(state);

    // Then
    assertNotNull(result);
    assertEquals(state, result.state());
    assertEquals(nonce, result.nonce());
    assertEquals(userId, result.userId());
  }

  @Test
  @DisplayName("应该在 state 不存在时抛出 OAUTH_STATE_INVALID")
  void should_throw_oauth_state_invalid_when_state_not_found() {
    // Given
    String nonExistentState = "non-existent-state";

    // When & Then
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> stateService.validateAndConsumeState(nonExistentState));
    assertEquals(ErrorCode.OAUTH_STATE_INVALID.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该在重复消费 state 时抛出 OAUTH_STATE_INVALID")
  void should_throw_oauth_state_invalid_when_state_consumed_twice() {
    // Given
    String state = "test-state-duplicate";
    String nonce = "test-nonce";
    stateService.saveState(state, nonce);

    // When - 第一次消费成功
    stateService.validateAndConsumeState(state);

    // Then - 第二次消费失败
    BusinessException exception =
        assertThrows(BusinessException.class, () -> stateService.validateAndConsumeState(state));
    assertEquals(ErrorCode.OAUTH_STATE_INVALID.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("应该生成不为空的 state")
  void should_generate_non_empty_state() {
    // When
    String state = stateService.generateState();

    // Then
    assertNotNull(state);
    assertFalse(state.isEmpty());
    assertEquals(32, state.length()); // UUID 去掉横杠后 32 位
  }
}
