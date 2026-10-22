package io.github.lystrosaurus.admin.file.domain.acl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Supplier;

/** 文件存储端口：物理文件读写的技术实现由基础设施层提供。 */
public interface FileStoragePort {

  /** 写入物理文件（内容可多次获取流） */
  void write(String relativePath, Supplier<InputStream> content) throws IOException;

  /** 解析物理文件路径 */
  Path resolve(String relativePath);

  /** 判断物理文件是否存在且可读 */
  boolean existsReadable(Path path);

  /** 删除物理文件（不存在时忽略） */
  void delete(String relativePath) throws IOException;
}
