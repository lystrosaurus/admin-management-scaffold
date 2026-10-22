package io.github.lystrosaurus.admin.file.application;

import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.shared.kernel.exception.BusinessException;
import io.github.lystrosaurus.admin.shared.kernel.exception.ErrorCode;
import io.github.lystrosaurus.admin.file.adapter.response.FileDownloadResponse;
import io.github.lystrosaurus.admin.file.adapter.response.FileResponse;
import io.github.lystrosaurus.admin.file.application.command.FileUploadCommand;
import io.github.lystrosaurus.admin.file.domain.acl.FileRepository;
import io.github.lystrosaurus.admin.file.domain.acl.FileStoragePort;
import io.github.lystrosaurus.admin.file.domain.model.SysFile;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 文件应用服务：编排文件上传/查询/下载/删除用例。 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileApplicationService {

  private final FileRepository fileRepository;
  private final FileStoragePort fileStoragePort;

  /** 上传文件并落库元数据 */
  @Transactional(rollbackFor = Exception.class)
  public FileResponse upload(FileUploadCommand command, String bizType, Long uploaderId) {
    if (command == null || command.size() == 0) {
      throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
    }

    String originalName = command.originalName();
    String ext = parseExt(originalName);
    String storedName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

    try {
      fileStoragePort.write(storedName, command.content());
    } catch (IOException | UncheckedIOException e) {
      log.error("文件写入失败: {}", storedName, e);
      throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
    }

    String md5 = computeMd5(command.content());

    SysFile file =
        new SysFile(
            null,
            originalName,
            storedName,
            storedName,
            ext,
            command.contentType(),
            command.size(),
            md5,
            uploaderId,
            StringUtils.hasText(bizType) ? bizType : "default",
            "NORMAL",
            null,
            null);

    return toResponse(fileRepository.save(file));
  }

  /** 条件分页查询文件 */
  public PageResult<FileResponse> list(String filename, String bizType, int page, int size) {
    Page<SysFile> result = fileRepository.findPage(filename, bizType, PageQuery.of(page, size));
    List<FileResponse> responses = result.records().stream().map(this::toResponse).toList();
    return new PageResult<>(responses, result.total(), result.pageNum(), result.pageSize());
  }

  /** 获取文件详情 */
  public FileResponse get(Long id) {
    return toResponse(findOrThrow(id));
  }

  /** 逻辑删除文件并删除物理文件 */
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    SysFile file = findOrThrow(id);
    fileRepository.logicDelete(id);
    deletePhysicalFile(file.path());
  }

  /** 下载文件，返回文件载体给 Controller */
  public FileDownloadResponse download(Long id) {
    SysFile file = findOrThrow(id);
    Path physical = fileStoragePort.resolve(file.path());
    if (!fileStoragePort.existsReadable(physical)) {
      throw new BusinessException(ErrorCode.FILE_NOT_READABLE);
    }
    return new FileDownloadResponse(
        physical, file.originalName(), file.mimeType(), file.sizeBytes());
  }

  /** 根据MD5查找已存在文件(用于秒传判断) */
  public Optional<FileResponse> findByMd5(String md5) {
    return fileRepository.findByMd5(md5).map(this::toResponse);
  }

  private SysFile findOrThrow(Long id) {
    return fileRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
  }

  private void deletePhysicalFile(String relativePath) {
    if (!StringUtils.hasText(relativePath)) {
      return;
    }
    try {
      fileStoragePort.delete(relativePath);
    } catch (IOException e) {
      log.warn("物理文件删除失败: {}", relativePath, e);
    }
  }

  private String parseExt(String filename) {
    if (!StringUtils.hasText(filename) || !filename.contains(".")) {
      return "";
    }
    int idx = filename.lastIndexOf('.');
    String ext = filename.substring(idx + 1);
    return ext.toLowerCase(java.util.Locale.ROOT);
  }

  private String computeMd5(Supplier<InputStream> content) {
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      try (InputStream in = content.get()) {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
          digest.update(buffer, 0, read);
        }
      }
      return HexFormat.of().formatHex(digest.digest());
    } catch (IOException | NoSuchAlgorithmException | UncheckedIOException e) {
      log.warn("计算文件MD5失败", e);
      return null;
    }
  }

  private FileResponse toResponse(SysFile file) {
    return new FileResponse(
        file.id(),
        file.originalName(),
        file.storedName(),
        file.ext(),
        file.mimeType(),
        file.sizeBytes(),
        file.md5(),
        file.uploaderId(),
        file.bizType(),
        file.status(),
        file.createdAt());
  }
}
