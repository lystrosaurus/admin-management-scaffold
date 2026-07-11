package io.github.lystrosaurus.admin.file.service;

import io.github.lystrosaurus.admin.common.PageResult;
import io.github.lystrosaurus.admin.exception.BusinessException;
import io.github.lystrosaurus.admin.exception.ErrorCode;
import io.github.lystrosaurus.admin.file.dao.SysFileDAO;
import io.github.lystrosaurus.admin.file.dto.FileQueryDTO;
import io.github.lystrosaurus.admin.file.entity.SysFile;
import io.github.lystrosaurus.admin.file.mapstruct.FileMapper;
import io.github.lystrosaurus.admin.file.vo.FileDownloadVO;
import io.github.lystrosaurus.admin.file.vo.FileVO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/** 文件服务实现 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

  private final SysFileDAO sysFileDAO;
  private final FileMapper fileMapper;

  @Value("${file.storage.local-base-path:./uploads}")
  private String localBasePath;

  /** 上传文件并落库元数据 */
  @Transactional(rollbackFor = Exception.class)
  public FileVO upload(MultipartFile file, String bizType, Long uploaderId) {
    if (file == null || file.isEmpty()) {
      throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
    }

    String originalName = file.getOriginalFilename();
    String ext = parseExt(originalName);
    String storedName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

    Path targetDir = Paths.get(localBasePath);
    Path targetPath = targetDir.resolve(storedName);

    try {
      Files.createDirectories(targetDir);
      try (InputStream in = file.getInputStream()) {
        Files.copy(in, targetPath);
      }
    } catch (IOException e) {
      log.error("文件写入失败: {}", storedName, e);
      throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
    }

    String md5 = computeMd5(file);

    SysFile entity = new SysFile();
    entity.setOriginalName(originalName);
    entity.setStoredName(storedName);
    entity.setPath(storedName);
    entity.setExt(ext);
    entity.setMimeType(file.getContentType());
    entity.setSizeBytes(file.getSize());
    entity.setMd5(md5);
    entity.setUploaderId(uploaderId);
    entity.setBizType(StringUtils.hasText(bizType) ? bizType : "default");
    entity.setStatus("NORMAL");

    sysFileDAO.save(entity);
    return fileMapper.toVO(entity);
  }

  /** 条件分页查询文件 */
  public PageResult<FileVO> list(FileQueryDTO query) {
    var page = sysFileDAO.findPage(query.filename(), query.bizType(), query.page(), query.size());
    List<FileVO> vos = page.getRecords().stream().map(fileMapper::toVO).toList();
    return new PageResult<>(vos, page.getTotal(), query.page(), query.size());
  }

  /** 获取文件详情 */
  public FileVO get(Long id) {
    SysFile file =
        sysFileDAO.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
    return fileMapper.toVO(file);
  }

  /** 逻辑删除文件并删除物理文件 */
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    SysFile file =
        sysFileDAO.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
    sysFileDAO.logicDelete(id);
    deletePhysicalFile(file.getPath());
  }

  /** 下载文件，返回文件载体给 Controller */
  public FileDownloadVO download(Long id) {
    SysFile file =
        sysFileDAO.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
    Path physical = Paths.get(localBasePath).resolve(file.getPath());
    if (!Files.exists(physical) || !Files.isReadable(physical)) {
      throw new BusinessException(ErrorCode.FILE_NOT_READABLE);
    }
    return new FileDownloadVO(
        physical, file.getOriginalName(), file.getMimeType(), file.getSizeBytes());
  }

  private void deletePhysicalFile(String relativePath) {
    if (!StringUtils.hasText(relativePath)) {
      return;
    }
    try {
      Files.deleteIfExists(Paths.get(localBasePath).resolve(relativePath));
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

  private String computeMd5(MultipartFile file) {
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      try (InputStream in = file.getInputStream()) {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
          digest.update(buffer, 0, read);
        }
      }
      return HexFormat.of().formatHex(digest.digest());
    } catch (IOException | NoSuchAlgorithmException e) {
      log.warn("计算文件MD5失败", e);
      return null;
    }
  }

  /** 根据MD5查找已存在文件(用于秒传判断) */
  public Optional<FileVO> findByMd5(String md5) {
    return sysFileDAO.findByMd5(md5).map(fileMapper::toVO);
  }
}
