package io.github.lystrosaurus.admin.auth.aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * PermissionAspect 4 态默认拒绝决策单测（fail-closed，纯函数无 Sa-Token 依赖）。
 */
@DisplayName("PermissionAspect 4 态决策测试")
class PermissionAspectDecisionTest {

  @ParameterizedTest(name = "[{index}] permitAll={0} requiresPermission={1} requiresRole={2} defaultDeny={3} -> {4}")
  @MethodSource("cases")
  @DisplayName("decidePolicy 覆盖 4 态且默认拒绝 fail-closed")
  void decidePolicy_covers_four_states(
      boolean methodPermitAll,
      boolean requiresPermission,
      boolean requiresRole,
      boolean defaultDenyEnabled,
      PermissionAspect.AuthDecision expected) {
    assertEquals(
        expected,
        PermissionAspect.decidePolicy(methodPermitAll, requiresPermission, requiresRole, defaultDenyEnabled));
  }

  static Stream<Arguments> cases() {
    return Stream.of(
        // 逃生舱：方法级 @PermitAll 永远放行（覆盖其余所有标志）
        Arguments.of(true, true, true, true, PermissionAspect.AuthDecision.ALLOW),
        Arguments.of(true, false, false, false, PermissionAspect.AuthDecision.ALLOW),
        // @RequiresPermission（方法/类）优先于 @RequiresRole
        Arguments.of(false, true, true, true, PermissionAspect.AuthDecision.CHECK_PERMISSION),
        Arguments.of(false, true, false, false, PermissionAspect.AuthDecision.CHECK_PERMISSION),
        // 仅 @RequiresRole
        Arguments.of(false, false, true, true, PermissionAspect.AuthDecision.CHECK_ROLE),
        Arguments.of(false, false, true, false, PermissionAspect.AuthDecision.CHECK_ROLE),
        // 无任何注解：默认拒绝开启 -> DENY（fail-closed）；关闭 -> ALLOW（既有放行行为）
        Arguments.of(false, false, false, true, PermissionAspect.AuthDecision.DENY),
        Arguments.of(false, false, false, false, PermissionAspect.AuthDecision.ALLOW));
  }

  @Test
  @DisplayName("逃生舱(@PermitAll)优先级高于类级 @RequiresPermission")
  void permit_all_overrides_class_requires_permission() {
    // 类级 @RequiresPermission + 方法级 @PermitAll：逃生舱优先生效
    assertEquals(
        PermissionAspect.AuthDecision.ALLOW,
        PermissionAspect.decidePolicy(true, true, false, true));
  }
}
