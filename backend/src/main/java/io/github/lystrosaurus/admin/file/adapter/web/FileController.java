package io.github.lystrosaurus.admin.file.adapter.web;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.domain.acl.TokenSessionPort;
import io.github.lystrosaurus.admin.shared.adapter.web.ApiResponse;
import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.file.adapter.request.FileQueryRequest;
import io.github.lystrosaurus.admin.file.adapter.response.FileDownloadResponse;
import io.github.lystrosaurus.admin.file.adapter.response.FileResponse;
import io.github.lystrosaurus.admin.file.application.FileApplicationService;
import io.github.lystrosaurus.admin.file.application.command.FileUploadCommand;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 文件管理控制器 */
@RestController
@RequestMapping("/app/files")
@RequiredArgsConstructor
public class FileController {

  private final FileApplicationService fileApplicationService;
  private final TokenSessionPort tokenSessionPort;

  /** 分页查询文件列表 */
  @RequiresPermission("file:manage")
  @GetMapping
  public ApiResponse<PageResult<FileResponse>> list(@Valid FileQueryRequest query) {
    return ApiResponse.success(
        fileApplicationService.list(query.filename(), query.bizType(), query.page(), query.size()));
  }

  /** 获取文件详情 */
  @RequiresPermission("file:manage")
  @GetMapping("/{id}")
  public ApiResponse<FileResponse> get(@PathVariable Long id) {
    return ApiResponse.success(fileApplicationService.get(id));
  }

  /** 下载文件 */
  @RequiresPermission("file:manage")
  @GetMapping("/{id}/download")
  public ResponseEntity<InputStreamResource> download(@PathVariable Long id) throws IOException {
    FileDownloadResponse download = fileApplicationService.download(id);
    Path path = download.path();
    InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));
    String encodedName =
        URLEncoder.encode(download.originalName(), StandardCharsets.UTF_8).replace("+", "%20");
    return ResponseEntity.ok()
        .contentType(
            download.mimeType() != null
                ? MediaType.parseMediaType(download.mimeType())
                : MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
        .contentLength(download.sizeBytes())
        .body(resource);
  }

  /** 上传文件 */
  @RequiresPermission("file:manage")
  @PostMapping("/upload")
  public ApiResponse<FileResponse> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "bizType", required = false) String bizType) {
    FileResponse response =
        fileApplicationService.upload(toCommand(file), bizType, tokenSessionPort.currentUserId());
    return ApiResponse.success(response);
  }

  /** 删除文件 */
  @RequiresPermission("file:manage")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    fileApplicationService.delete(id);
    return ApiResponse.success();
  }

  /** 将 HTTP 上传文件适配为应用层命令（流可多次获取） */
  private static FileUploadCommand toCommand(MultipartFile file) {
    Supplier<InputStream> content =
        () -> {
          try {
            return file.getInputStream();
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        };
    return new FileUploadCommand(
        file.getOriginalFilename(), file.getContentType(), file.getSize(), content);
  }
}
