package io.github.lystrosaurus.admin.auth.oauth.state;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** OAuthStateService 单元测试 */
@DisplayName("OAuth状态服务测试")
class OAuthStateServiceTest {

  private OAuthStateService stateService;

  @BeforeEach
  void setUp() {
    stateService = new OAuthStateService();
  }

  @Test
  @DisplayName("应该成功生成state")
  void should_generate_state() {
    String state = stateService.generateState();
    assertNotNull(state);
    assertFalse(state.isEmpty());
  }

  @Test
  @DisplayName("应该成功保存和验证state")
  void should_save_and_validate_state() {
    String state = "test-state";
    stateService.saveState(state, "test-nonce");

    StateData result = stateService.validateAndConsumeState(state);

    assertNotNull(result);
    assertEquals("test-state", result.state());
    assertEquals("test-nonce", result.nonce());
  }

  @Test
  @DisplayName("验证已使用的state应该抛出异常")
  void should_throw_exception_when_validating_used_state() {
    String state = "test-state";
    stateService.saveState(state, "test-nonce");
    stateService.validateAndConsumeState(state);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> stateService.validateAndConsumeState(state));
    assertEquals(ErrorCode.OAUTH_STATE_INVALID.getCode(), exception.getCode());
  }

  @Test
  @DisplayName("验证不存在的state应该抛出异常")
  void should_throw_exception_when_validating_nonexistent_state() {
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> stateService.validateAndConsumeState("nonexistent"));
    assertEquals(ErrorCode.OAUTH_STATE_INVALID.getCode(), exception.getCode());
  }
}
