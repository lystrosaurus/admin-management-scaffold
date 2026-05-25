package io.github.lystrosaurus.admin.handler;

import cn.dev33.satoken.stp.StpUtil;
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
    this.strictInsertFill(metaObject, "createdBy", () -> getCurrentUserOrDefault(), String.class);
    this.strictInsertFill(metaObject, "updatedBy", () -> getCurrentUserOrDefault(), String.class);
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    log.debug("自动填充更新字段");
    this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);
    this.strictUpdateFill(metaObject, "updatedBy", () -> getCurrentUserOrDefault(), String.class);
  }

  /** 获取当前登录用户ID，未登录或无上下文则返回 system */
  private String getCurrentUserOrDefault() {
    try {
      if (StpUtil.isLogin()) {
        return String.valueOf(StpUtil.getLoginId());
      }
    } catch (Exception ignored) {
      // SaTokenContext 未初始化（如测试环境），返回 system
    }
    return "system";
  }
}
