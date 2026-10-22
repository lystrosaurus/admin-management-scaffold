package io.github.lystrosaurus.admin.auth.infrastructure.acl;

import io.github.lystrosaurus.admin.auth.domain.acl.ClientRequestInfoPort;
import io.github.lystrosaurus.admin.web.util.ClientIpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** 客户端请求信息端口的 Servlet 实现：框架细节收敛在此处。 */
@Component
public class RequestContextClientRequestInfoAdapter implements ClientRequestInfoPort {

  @Override
  public String clientIp() {
    ServletRequestAttributes attrs = currentAttributes();
    if (attrs == null) {
      return "unknown";
    }
    return ClientIpUtil.getClientIp(attrs.getRequest());
  }

  @Override
  public String userAgent() {
    ServletRequestAttributes attrs = currentAttributes();
    if (attrs == null) {
      return "unknown";
    }
    return attrs.getRequest().getHeader("User-Agent");
  }

  private ServletRequestAttributes currentAttributes() {
    return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
  }
}
