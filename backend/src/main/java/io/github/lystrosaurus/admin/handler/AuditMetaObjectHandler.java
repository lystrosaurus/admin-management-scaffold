package io.github.lystrosaurus.admin.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/** MyBatis-Plus 自动填充处理器 */
@Slf4j
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    log.debug("自动填充插入字段");
    this.strictInsertFill(metaObject, "createdAt", LocalDateTime::now, LocalDateTime.class);
    this.strictInsertFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);
    this.strictInsertFill(metaObject, "deleted", () -> 0, Integer.class);
    this.strictInsertFill(metaObject, "version", () -> 1, Integer.class);
    // TODO: 从 Sa-Token 获取当前用户
    this.strictInsertFill(metaObject, "createdBy", () -> "system", String.class);
    this.strictInsertFill(metaObject, "updatedBy", () -> "system", String.class);
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    log.debug("自动填充更新字段");
    this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);
    // TODO: 从 Sa-Token 获取当前用户
    this.strictUpdateFill(metaObject, "updatedBy", () -> "system", String.class);
  }
}
