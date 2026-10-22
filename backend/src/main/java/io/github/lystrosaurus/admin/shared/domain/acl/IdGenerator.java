package io.github.lystrosaurus.admin.shared.domain.acl;

/** ID 生成端口：领域层预生成标识，技术实现（雪花算法）在基础设施层 */
public interface IdGenerator {

  /** 生成下一个全局唯一 ID */
  Long nextId();
}
