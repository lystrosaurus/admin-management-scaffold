package io.github.lystrosaurus.admin.auth.domain.acl;

/** 客户端请求信息端口：隔离应用层对 Web 请求上下文的依赖。 */
public interface ClientRequestInfoPort {

  /** 客户端真实 IP（无请求上下文时返回 unknown） */
  String clientIp();

  /** 客户端 User-Agent（无请求上下文时返回 unknown） */
  String userAgent();
}
