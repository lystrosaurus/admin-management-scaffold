package io.github.lystrosaurus.admin.shared.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import org.springframework.stereotype.Component;

/** 雪花 ID 生成器：{@link IdGenerator} 端口的 MyBatis-Plus 实现 */
@Component
public class SnowflakeIdGenerator implements IdGenerator {

  @Override
  public Long nextId() {
    return IdWorker.getId();
  }
}
