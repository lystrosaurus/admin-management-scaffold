package io.github.lystrosaurus.admin.file.infrastructure.persistence.po;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** SysFilePO 持久化对象单元测试 */
@DisplayName("SysFilePO 持久化对象测试")
class SysFilePOTest {

  @Test
  @DisplayName("应该正确设置和读取字段")
  void should_set_and_get_fields() {
    SysFilePO file = new SysFilePO();
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
    file.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
    file.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 12, 0));

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
    assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), file.getCreatedAt());
    assertEquals(LocalDateTime.of(2024, 1, 2, 12, 0), file.getUpdatedAt());
  }
}
