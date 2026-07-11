package io.github.lystrosaurus.admin.file.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.file.dao.SysFileDAO;
import io.github.lystrosaurus.admin.file.dto.FileQueryDTO;
import io.github.lystrosaurus.admin.file.entity.SysFile;
import io.github.lystrosaurus.admin.file.mapstruct.FileMapper;
import io.github.lystrosaurus.admin.file.vo.FileVO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

/** FileService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("文件服务测试")
class FileServiceTest {

  @Mock private SysFileDAO sysFileDAO;
  @Mock private FileMapper fileMapper;

  @InjectMocks private FileService fileService;

  @BeforeEach
  void setUp() throws Exception {
    // 注入 @Value 字段，避免单元测试访问真实磁盘
    Field field = FileService.class.getDeclaredField("localBasePath");
    field.setAccessible(true);
    field.set(fileService, "target/test-uploads");
  }

  @Test
  @DisplayName("上传应写入元数据并返回 VO")
  void should_upload_and_return_vo() throws IOException {
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getOriginalFilename()).thenReturn("hello.txt");
    when(file.getSize()).thenReturn(5L);
    when(file.getContentType()).thenReturn("text/plain");
    when(file.getInputStream())
        .thenAnswer(invocation -> new ByteArrayInputStream("hello".getBytes()));

    SysFile saved = new SysFile();
    saved.setId(1L);
    saved.setOriginalName("hello.txt");
    saved.setStoredName("uuid.txt");
    saved.setStatus("NORMAL");
    saved.setCreatedAt(LocalDateTime.now());

    FileVO vo =
        new FileVO(
            1L,
            "hello.txt",
            "uuid.txt",
            "txt",
            "text/plain",
            5L,
            "md5",
            1L,
            "default",
            "NORMAL",
            LocalDateTime.now());

    doAnswer(
            invocation -> {
              SysFile arg = invocation.getArgument(0);
              arg.setId(1L);
              return null;
            })
        .when(sysFileDAO)
        .save(any(SysFile.class));
    when(fileMapper.toVO(any(SysFile.class))).thenReturn(vo);

    FileVO result = fileService.upload(file, "avatar", 1L);

    assertNotNull(result);
    assertEquals("hello.txt", result.originalName());
    verify(sysFileDAO).save(any(SysFile.class));
  }

  @Test
  @DisplayName("上传空文件应抛异常")
  void should_throw_when_file_empty() {
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(true);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> fileService.upload(file, "avatar", 1L));
    assertEquals(ErrorCode.FILE_UPLOAD_FAILED.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("获取不存在文件应抛异常")
  void should_throw_when_get_not_found() {
    when(sysFileDAO.findById(999L)).thenReturn(java.util.Optional.empty());

    BusinessException ex = assertThrows(BusinessException.class, () -> fileService.get(999L));
    assertEquals(ErrorCode.FILE_NOT_FOUND.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("删除不存在文件应抛异常")
  void should_throw_when_delete_not_found() {
    when(sysFileDAO.findById(999L)).thenReturn(java.util.Optional.empty());

    BusinessException ex = assertThrows(BusinessException.class, () -> fileService.delete(999L));
    assertEquals(ErrorCode.FILE_NOT_FOUND.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("分页查询应返回结果")
  void should_list_files() {
    SysFile f = new SysFile();
    f.setId(1L);
    f.setOriginalName("a.txt");
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysFile> page =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
    page.setRecords(java.util.List.of(f));
    page.setTotal(1);
    when(sysFileDAO.findPage(nullable(String.class), nullable(String.class), anyInt(), anyInt()))
        .thenReturn(page);
    when(fileMapper.toVO(any(SysFile.class)))
        .thenReturn(
            new FileVO(1L, "a.txt", "s.txt", "txt", null, 1L, null, 1L, "default", "NORMAL", null));

    var result = fileService.list(new FileQueryDTO("a", null, 1, 10));

    assertEquals(1, result.total());
    assertEquals(1, result.items().size());
  }

  @Test
  @DisplayName("下载不可读文件应抛异常")
  void should_throw_when_file_not_readable() throws Exception {
    SysFile f = new SysFile();
    f.setId(1L);
    f.setOriginalName("a.txt");
    f.setPath("not-exist.txt");
    f.setSizeBytes(1L);
    when(sysFileDAO.findById(1L)).thenReturn(java.util.Optional.of(f));

    BusinessException ex = assertThrows(BusinessException.class, () -> fileService.download(1L));
    assertEquals(ErrorCode.FILE_NOT_READABLE.getCode(), ex.getCode());
  }

  private static Path tempDir() {
    try {
      return java.nio.file.Files.createTempDirectory("file-test");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
