package io.github.lystrosaurus.admin.test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lystrosaurus.admin.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

/**
 * Sa-Token 测试基类
 *
 * <p>继承 BaseTest，提供 Sa-Token 认证状态的模拟。用于测试 /app/** 等需要登录认证的接口。
 *
 * <p>通过 MockedStatic 模拟 StpUtil 的静态方法，使 Sa-Token 拦截器放行请求。
 */
public abstract class SaTokenTest extends BaseTest {

  /** 测试用户ID */
  protected static final Long TEST_USER_ID = 1L;

  private MockedStatic<StpUtil> stpUtilMock;

  @BeforeEach
  void setUpSaToken() {
    // 使用 MockedStatic 模拟 StpUtil 静态方法
    stpUtilMock = mockStatic(StpUtil.class);
    // checkLogin() 需要抛出异常才代表未登录，不抛异常就是已登录
    stpUtilMock.when(StpUtil::checkLogin).thenAnswer(invocation -> null);
    stpUtilMock.when(StpUtil::isLogin).thenReturn(true);
    stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(TEST_USER_ID);
    stpUtilMock.when(StpUtil::getTokenValue).thenReturn("mock-jwt-token");
    // 模拟 checkPermission / checkRole 不抛异常（默认有权限）
    stpUtilMock.when(() -> StpUtil.checkPermission(anyString())).thenAnswer(invocation -> null);
    stpUtilMock.when(() -> StpUtil.checkRole(anyString())).thenAnswer(invocation -> null);
    stpUtilMock
        .when(() -> StpUtil.checkPermissionOr(anyString(), anyString()))
        .thenAnswer(invocation -> null);
    stpUtilMock
        .when(() -> StpUtil.checkPermissionAnd(anyString(), anyString()))
        .thenAnswer(invocation -> null);
  }

  @AfterEach
  void tearDownSaToken() {
    // 关闭 MockedStatic，避免影响其他测试
    if (stpUtilMock != null) {
      stpUtilMock.close();
    }
  }
}
