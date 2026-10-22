package io.github.lystrosaurus.admin.integration.source.domain.acl;

/** 外部身份源查询端口：供 principal 等其它上下文读取身份源名称。 */
public interface ExtSourceQueryPort {

  /** 按ID查询身份源名称，不存在时返回 null */
  String findNameById(Long sourceId);
}
