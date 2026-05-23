package io.github.lystrosaurus.admin.auth.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 配置
 *
 * <p>配置 Sa-Token 和 JWT 相关参数
 */
@Configuration
public class SaTokenConfig {

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
}
