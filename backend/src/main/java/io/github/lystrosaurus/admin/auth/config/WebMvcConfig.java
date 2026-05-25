package io.github.lystrosaurus.admin.auth.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc 配置
 *
 * <p>配置 Sa-Token 拦截器，实现登录认证和 tokenVersion 校验
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final TokenVersionInterceptor tokenVersionInterceptor;

  public WebMvcConfig(
      @Autowired(required = false) TokenVersionInterceptor tokenVersionInterceptor) {
    this.tokenVersionInterceptor = tokenVersionInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
        .addPathPatterns("/app/**")
        .excludePathPatterns("/public/**", "/open/**");

    // tokenVersion 校验：在登录校验之后执行，验证 Token 是否已被吊销
    if (tokenVersionInterceptor != null) {
      registry
          .addInterceptor(tokenVersionInterceptor)
          .addPathPatterns("/app/**")
          .excludePathPatterns("/public/**", "/open/**");
    }
  }
}
