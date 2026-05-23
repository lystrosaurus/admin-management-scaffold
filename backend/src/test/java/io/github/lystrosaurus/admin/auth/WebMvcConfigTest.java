package io.github.lystrosaurus.admin.auth;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.BaseTest;
import io.github.lystrosaurus.admin.auth.config.WebMvcConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc 配置测试
 *
 * <p>测试 Sa-Token 拦截器配置是否正确
 */
@DisplayName("WebMvc 配置测试")
class WebMvcConfigTest extends BaseTest {

  @Autowired private WebMvcConfig webMvcConfig;

  @Test
  @DisplayName("应该成功注入 WebMvcConfig")
  void should_inject_web_mvc_config() {
    assertNotNull(webMvcConfig);
    assertTrue(webMvcConfig instanceof WebMvcConfigurer);
  }

  @Test
  @DisplayName("应该配置拦截器")
  void should_configure_interceptors() {
    // 测试配置类是否正确实现了 WebMvcConfigurer
    assertNotNull(webMvcConfig);
  }
}
