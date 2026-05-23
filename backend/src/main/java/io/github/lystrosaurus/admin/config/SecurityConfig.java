package io.github.lystrosaurus.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全配置
 *
 * <p>提供 BCryptPasswordEncoder Bean
 */
@Configuration
public class SecurityConfig {

  /**
   * 创建 BCryptPasswordEncoder Bean
   *
   * @return BCryptPasswordEncoder 实例
   */
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
