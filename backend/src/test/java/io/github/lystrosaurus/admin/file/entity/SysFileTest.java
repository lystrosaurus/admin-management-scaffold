package io.github.lystrosaurus.admin.file.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** SysFile 实体单元测试 */
@DisplayName("SysFile 实体测试")
class SysFileTest {

  @Test
  @DisplayName("应该正确设置和读取字段")
  void should_set_and_get_fields() {
    SysFile file = new SysFile();
    file.setId(1L);
    file.setOriginalName("test.txt");
    file.setStoredName("uuid.txt");
    file.setPath("uuid.txt");
    file.setExt("txt");
    file.setMimeType("text/plain");
    file.setSizeBytes(10L);
    file.setMd5("md5value");
    file.setUploaderId(5L);
    file.setBizType("avatar");
    file.setStatus("NORMAL");

    assertEquals(1L, file.getId());
    assertEquals("test.txt", file.getOriginalName());
    assertEquals("uuid.txt", file.getStoredName());
    assertEquals("uuid.txt", file.getPath());
    assertEquals("txt", file.getExt());
    assertEquals("text/plain", file.getMimeType());
    assertEquals(10L, file.getSizeBytes());
    assertEquals("md5value", file.getMd5());
    assertEquals(5L, file.getUploaderId());
    assertEquals("avatar", file.getBizType());
    assertEquals("NORMAL", file.getStatus());
  }
}
