package io.github.lystrosaurus.admin.handler;

import static org.junit.jupiter.api.Assertions.*;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/** AuditMetaObjectHandler 单元测试 */
@ExtendWith(MockitoExtension.class)
class AuditMetaObjectHandlerTest {

  @InjectMocks private AuditMetaObjectHandler auditMetaObjectHandler;

  @Test
  void should_implement_meta_object_handler_interface() {
    assertTrue(auditMetaObjectHandler instanceof MetaObjectHandler);
  }

  @Test
  void should_have_insert_fill_method() throws NoSuchMethodException {
    assertNotNull(
        AuditMetaObjectHandler.class.getMethod(
            "insertFill", org.apache.ibatis.reflection.MetaObject.class));
  }

  @Test
  void should_have_update_fill_method() throws NoSuchMethodException {
    assertNotNull(
        AuditMetaObjectHandler.class.getMethod(
            "updateFill", org.apache.ibatis.reflection.MetaObject.class));
  }
}
