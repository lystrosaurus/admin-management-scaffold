package io.github.lystrosaurus.admin.auth.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 配置
 *
 * <p>配置 Sa-Token 和 JWT 相关参数
 */
@Configuration
public class SaTokenConfig {

  @Value("${sa-token.jwt-secret-key:}")
  private String jwtSecretKey;

  /**
   * 配置 Sa-Token 的 JWT 风格
   *
   * <p>使用 Simple 风格，Token 以 jwt 形式存储
   *
   * @return StpLogic 实例
   */
  @Bean
  public StpLogic getStpLogic() {
    return new StpLogicJwtForSimple();
  }

  /**
   * 启动时校验 JWT 密钥配置
   *
   * <p>确保 JWT_SECRET 环境变量已正确配置，且密钥长度满足安全要求
   */
  @PostConstruct
  public void validateJwtSecretKey() {
    if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
      throw new IllegalStateException(
          "sa-token.jwt-secret-key must be configured. "
              + "Set JWT_SECRET environment variable.");
    }
    if (jwtSecretKey.length() < 32) {
      throw new IllegalStateException(
          "sa-token.jwt-secret-key must be at least 32 characters long. "
              + "Current length: "
              + jwtSecretKey.length());
    }
    if (jwtSecretKey.contains("your-jwt-secret")) {
      throw new IllegalStateException(
          "sa-token.jwt-secret-key cannot use default/example value. "
              + "Please set a secure JWT_SECRET environment variable.");
    }
  }
}
