package io.github.lystrosaurus.admin.web.util;

import jakarta.servlet.http.HttpServletRequest;

/** 客户端 IP 获取工具类 */
public final class ClientIpUtil {

  private ClientIpUtil() {}

  /**
   * 从 HttpServletRequest 获取客户端真实 IP
   *
   * <p>优先从代理头中获取，支持 X-Forwarded-For、X-Real-IP 等常见代理头。
   *
   * @param request HTTP 请求
   * @return 客户端真实 IP
   */
  public static String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (isEmptyOrUnknown(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (isEmptyOrUnknown(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (isEmptyOrUnknown(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (isEmptyOrUnknown(ip)) {
      ip = request.getRemoteAddr();
    }
    // 多个代理时取第一个
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip;
  }

  private static boolean isEmptyOrUnknown(String ip) {
    return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
  }
}
