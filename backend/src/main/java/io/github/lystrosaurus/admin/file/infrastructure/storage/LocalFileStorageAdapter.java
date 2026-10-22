package io.github.lystrosaurus.admin.file.infrastructure.storage;

import io.github.lystrosaurus.admin.file.domain.acl.FileStoragePort;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 本地磁盘文件存储实现。 */
@Component
public class LocalFileStorageAdapter implements FileStoragePort {

  @Value("${file.storage.local-base-path:./uploads}")
  private String localBasePath;

  @Override
  public void write(String relativePath, Supplier<InputStream> content) throws IOException {
    Path targetDir = Paths.get(localBasePath);
    Path targetPath = targetDir.resolve(relativePath);
    Files.createDirectories(targetDir);
    try (InputStream in = content.get()) {
      Files.copy(in, targetPath);
    }
  }

  @Override
  public Path resolve(String relativePath) {
    return Paths.get(localBasePath).resolve(relativePath);
  }

  @Override
  public boolean existsReadable(Path path) {
    return Files.exists(path) && Files.isReadable(path);
  }

  @Override
  public void delete(String relativePath) throws IOException {
    Files.deleteIfExists(Paths.get(localBasePath).resolve(relativePath));
  }
}
