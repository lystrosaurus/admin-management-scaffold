package io.github.lystrosaurus.admin.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 请求日志过滤器 */
@Slf4j
@Component
@Order(1)
public class RequestLogFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    long startTime = System.currentTimeMillis();
    String method = httpRequest.getMethod();
    String uri = httpRequest.getRequestURI();
    String queryString = httpRequest.getQueryString();
    String clientIp = getClientIp(httpRequest);

    log.info(
        "请求开始: {} {} {} from {}", method, uri, queryString != null ? queryString : "", clientIp);

    try {
      chain.doFilter(request, response);
    } finally {
      long duration = System.currentTimeMillis() - startTime;
      int status = httpResponse.getStatus();
      log.info("请求结束: {} {} status={} 耗时={}ms", method, uri, status, duration);
    }
  }

  /** 获取客户端真实 IP */
  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // 多个代理时取第一个
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip;
  }
}
