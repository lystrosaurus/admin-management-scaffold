package io.github.lystrosaurus.admin.file.infrastructure.converter;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.file.domain.model.SysFile;
import io.github.lystrosaurus.admin.file.infrastructure.persistence.po.SysFilePO;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/** SysFilePoConverter MapStruct 单元测试 */
@DisplayName("SysFilePoConverter MapStruct 测试")
class FilePoConverterTest {

  private static final SysFilePoConverter converter = Mappers.getMapper(SysFilePoConverter.class);

  @Test
  @DisplayName("应该将SysFilePO转换为SysFile领域对象")
  void should_convert_po_to_domain() {
    SysFilePO po = new SysFilePO();
    po.setId(1L);
    po.setOriginalName("report.pdf");
    po.setStoredName("uuid.pdf");
    po.setPath("uuid.pdf");
    po.setExt("pdf");
    po.setMimeType("application/pdf");
    po.setSizeBytes(1024L);
    po.setMd5("abc");
    po.setUploaderId(2L);
    po.setBizType("doc");
    po.setStatus("NORMAL");
    po.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
    po.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 12, 0));

    SysFile file = converter.toDomain(po);

    assertNotNull(file);
    assertEquals(1L, file.id());
    assertEquals("report.pdf", file.originalName());
    assertEquals("uuid.pdf", file.storedName());
    assertEquals("uuid.pdf", file.path());
    assertEquals("pdf", file.ext());
    assertEquals("application/pdf", file.mimeType());
    assertEquals(1024L, file.sizeBytes());
    assertEquals("abc", file.md5());
    assertEquals(2L, file.uploaderId());
    assertEquals("doc", file.bizType());
    assertEquals("NORMAL", file.status());
    assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), file.createdAt());
    assertEquals(LocalDateTime.of(2024, 1, 2, 12, 0), file.updatedAt());
  }

  @Test
  @DisplayName("应该将SysFile领域对象转换为SysFilePO")
  void should_convert_domain_to_po() {
    SysFile file =
        new SysFile(
            1L,
            "report.pdf",
            "uuid.pdf",
            "uuid.pdf",
            "pdf",
            "application/pdf",
            1024L,
            "abc",
            2L,
            "doc",
            "NORMAL",
            LocalDateTime.of(2024, 1, 1, 12, 0),
            LocalDateTime.of(2024, 1, 2, 12, 0));

    SysFilePO po = converter.toPo(file);

    assertNotNull(po);
    assertEquals(1L, po.getId());
    assertEquals("report.pdf", po.getOriginalName());
    assertEquals("uuid.pdf", po.getStoredName());
    assertEquals("uuid.pdf", po.getPath());
    assertEquals("pdf", po.getExt());
    assertEquals("application/pdf", po.getMimeType());
    assertEquals(1024L, po.getSizeBytes());
    assertEquals("abc", po.getMd5());
    assertEquals(2L, po.getUploaderId());
    assertEquals("doc", po.getBizType());
    assertEquals("NORMAL", po.getStatus());
    assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), po.getCreatedAt());
    assertEquals(LocalDateTime.of(2024, 1, 2, 12, 0), po.getUpdatedAt());
  }

  @Test
  @DisplayName("转换 null 应返回 null")
  void should_return_null_when_source_is_null() {
    assertNull(converter.toDomain(null));
    assertNull(converter.toPo(null));
  }
}
