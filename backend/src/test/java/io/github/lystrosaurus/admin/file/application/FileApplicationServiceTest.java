package io.github.lystrosaurus.admin.file.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.file.adapter.response.FileResponse;
import io.github.lystrosaurus.admin.file.application.command.FileUploadCommand;
import io.github.lystrosaurus.admin.file.domain.acl.FileRepository;
import io.github.lystrosaurus.admin.file.domain.acl.FileStoragePort;
import io.github.lystrosaurus.admin.file.domain.model.SysFile;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** FileApplicationService 单元测试 */
@ExtendWith(MockitoExtension.class)
@DisplayName("文件应用服务测试")
class FileApplicationServiceTest {

  @Mock private FileRepository fileRepository;
  @Mock private FileStoragePort fileStoragePort;

  @InjectMocks private FileApplicationService fileApplicationService;

  private static Supplier<InputStream> content(String value) {
    return () -> new ByteArrayInputStream(value.getBytes());
  }

  @Test
  @DisplayName("上传应写入元数据并返回响应")
  void should_upload_and_return_response() throws IOException {
    FileUploadCommand command =
        new FileUploadCommand("hello.txt", "text/plain", 5, content("hello"));
    SysFile saved =
        new SysFile(
            1L,
            "hello.txt",
            "uuid.txt",
            "uuid.txt",
            "txt",
            "text/plain",
            5L,
            "md5",
            1L,
            "avatar",
            "NORMAL",
            LocalDateTime.now(),
            LocalDateTime.now());
    when(fileRepository.save(any(SysFile.class))).thenReturn(saved);

    FileResponse result = fileApplicationService.upload(command, "avatar", 1L);

    assertNotNull(result);
    assertEquals("hello.txt", result.originalName());
    assertEquals("avatar", result.bizType());

    ArgumentCaptor<SysFile> captor = ArgumentCaptor.forClass(SysFile.class);
    verify(fileRepository).save(captor.capture());
    SysFile toSave = captor.getValue();
    assertEquals("hello.txt", toSave.originalName());
    assertEquals("txt", toSave.ext());
    assertEquals("NORMAL", toSave.status());
    assertEquals("avatar", toSave.bizType());
    assertEquals(1L, toSave.uploaderId());
    assertNotNull(toSave.md5());
    verify(fileStoragePort).write(eq(toSave.storedName()), any());
  }

  @Test
  @DisplayName("上传空文件应抛异常")
  void should_throw_when_file_empty() {
    FileUploadCommand command = new FileUploadCommand("empty.txt", "text/plain", 0, content(""));

    BusinessException ex =
        assertThrows(
            BusinessException.class, () -> fileApplicationService.upload(command, "avatar", 1L));
    assertEquals(ErrorCode.FILE_UPLOAD_FAILED.getCode(), ex.getCode());
    verify(fileRepository, never()).save(any(SysFile.class));
  }

  @Test
  @DisplayName("存储写入失败应抛上传失败")
  void should_throw_when_storage_write_failed() throws IOException {
    FileUploadCommand command =
        new FileUploadCommand("hello.txt", "text/plain", 5, content("hello"));
    doThrow(new IOException("disk")).when(fileStoragePort).write(anyString(), any());

    BusinessException ex =
        assertThrows(
            BusinessException.class, () -> fileApplicationService.upload(command, "avatar", 1L));
    assertEquals(ErrorCode.FILE_UPLOAD_FAILED.getCode(), ex.getCode());
    verify(fileRepository, never()).save(any(SysFile.class));
  }

  @Test
  @DisplayName("获取不存在文件应抛异常")
  void should_throw_when_get_not_found() {
    when(fileRepository.findById(999L)).thenReturn(Optional.empty());

    BusinessException ex =
        assertThrows(BusinessException.class, () -> fileApplicationService.get(999L));
    assertEquals(ErrorCode.FILE_NOT_FOUND.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("删除不存在文件应抛异常")
  void should_throw_when_delete_not_found() {
    when(fileRepository.findById(999L)).thenReturn(Optional.empty());

    BusinessException ex =
        assertThrows(BusinessException.class, () -> fileApplicationService.delete(999L));
    assertEquals(ErrorCode.FILE_NOT_FOUND.getCode(), ex.getCode());
  }

  @Test
  @DisplayName("删除应先逻辑删库再删物理文件")
  void should_delete_metadata_before_physical_file() throws IOException {
    SysFile file =
        new SysFile(
            1L,
            "a.txt",
            "uuid.txt",
            "uuid.txt",
            "txt",
            null,
            1L,
            null,
            1L,
            "default",
            "NORMAL",
            null,
            null);
    when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

    fileApplicationService.delete(1L);

    InOrder inOrder = inOrder(fileRepository, fileStoragePort);
    inOrder.verify(fileRepository).logicDelete(1L);
    inOrder.verify(fileStoragePort).delete("uuid.txt");
  }

  @Test
  @DisplayName("分页查询应返回结果")
  void should_list_files() {
    SysFile file =
        new SysFile(
            1L,
            "a.txt",
            "s.txt",
            "s.txt",
            "txt",
            null,
            1L,
            null,
            1L,
            "default",
            "NORMAL",
            null,
            null);
    when(fileRepository.findPage(eq("a"), isNull(), any()))
        .thenReturn(new Page<>(List.of(file), 1, 1, 10));

    var result = fileApplicationService.list("a", null, 1, 10);

    assertEquals(1, result.total());
    assertEquals(1, result.items().size());
  }

  @Test
  @DisplayName("下载不可读文件应抛异常")
  void should_throw_when_file_not_readable() {
    SysFile file =
        new SysFile(
            1L,
            "a.txt",
            "not-exist.txt",
            "not-exist.txt",
            "txt",
            null,
            1L,
            null,
            1L,
            "default",
            "NORMAL",
            null,
            null);
    Path physical = Path.of("not-exist.txt");
    when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
    when(fileStoragePort.resolve("not-exist.txt")).thenReturn(physical);
    when(fileStoragePort.existsReadable(physical)).thenReturn(false);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> fileApplicationService.download(1L));
    assertEquals(ErrorCode.FILE_NOT_READABLE.getCode(), ex.getCode());
  }
}
