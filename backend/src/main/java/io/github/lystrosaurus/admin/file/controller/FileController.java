package io.github.lystrosaurus.admin.file.controller;

import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.context.UserContext;
import io.github.lystrosaurus.admin.common.ApiResponse;
import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.file.dto.FileQueryDTO;
import io.github.lystrosaurus.admin.file.service.FileService;
import io.github.lystrosaurus.admin.file.vo.FileDownloadVO;
import io.github.lystrosaurus.admin.file.vo.FileVO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

  private final FileService fileService;

  /** 分页查询文件列表 */
  @RequiresPermission("file:manage")
  @GetMapping
  public ApiResponse<PageResult<FileVO>> list(@Valid FileQueryDTO query) {
    return ApiResponse.success(fileService.list(query));
  }

  /** 获取文件详情 */
  @RequiresPermission("file:manage")
  @GetMapping("/{id}")
  public ApiResponse<FileVO> get(@PathVariable Long id) {
    return ApiResponse.success(fileService.get(id));
  }

  /** 下载文件 */
  @RequiresPermission("file:manage")
  @GetMapping("/{id}/download")
  public ResponseEntity<InputStreamResource> download(@PathVariable Long id) throws IOException {
    FileDownloadVO download = fileService.download(id);
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
  public ApiResponse<FileVO> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "bizType", required = false) String bizType) {
    FileVO vo = fileService.upload(file, bizType, UserContext.getCurrentUserId());
    return ApiResponse.success(vo);
  }

  /** 删除文件 */
  @RequiresPermission("file:manage")
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    fileService.delete(id);
    return ApiResponse.success();
  }
}
