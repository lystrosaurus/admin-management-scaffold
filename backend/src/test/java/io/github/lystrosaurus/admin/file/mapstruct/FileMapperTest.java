package io.github.lystrosaurus.admin.file.mapstruct;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lystrosaurus.admin.file.entity.SysFile;
import io.github.lystrosaurus.admin.file.vo.FileVO;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/** FileMapper MapStruct 单元测试 */
@DisplayName("FileMapper MapStruct 测试")
class FileMapperTest {

  private static final FileMapper mapper = Mappers.getMapper(FileMapper.class);

  @Test
  @DisplayName("应该将 SysFile 实体转换为 FileVO")
  void should_convert_entity_to_vo() {
    SysFile entity = new SysFile();
    entity.setId(1L);
    entity.setOriginalName("report.pdf");
    entity.setStoredName("uuid.pdf");
    entity.setExt("pdf");
    entity.setMimeType("application/pdf");
    entity.setSizeBytes(1024L);
    entity.setMd5("abc");
    entity.setUploaderId(2L);
    entity.setBizType("doc");
    entity.setStatus("NORMAL");
    entity.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));

    FileVO vo = mapper.toVO(entity);

    assertNotNull(vo);
    assertEquals(1L, vo.id());
    assertEquals("report.pdf", vo.originalName());
    assertEquals("uuid.pdf", vo.storedName());
    assertEquals("pdf", vo.ext());
    assertEquals("application/pdf", vo.mimeType());
    assertEquals(1024L, vo.sizeBytes());
    assertEquals("abc", vo.md5());
    assertEquals(2L, vo.uploaderId());
    assertEquals("doc", vo.bizType());
    assertEquals("NORMAL", vo.status());
    assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), vo.createdAt());
  }

  @Test
  @DisplayName("转换 null 实体应返回 null")
  void should_return_null_when_entity_is_null() {
    assertNull(mapper.toVO(null));
  }
}
