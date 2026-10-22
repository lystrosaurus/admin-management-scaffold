package io.github.lystrosaurus.admin.file.domain.acl;

import io.github.lystrosaurus.admin.file.domain.model.SysFile;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import java.util.Optional;

/** 文件仓储端口：文件元数据的存取契约。 */
public interface FileRepository {

  /** 根据ID查找文件 */
  Optional<SysFile> findById(Long id);

  /** 条件分页查询文件 */
  Page<SysFile> findPage(String filename, String bizType, PageQuery pageQuery);

  /** 保存文件元数据（新建或更新），并回读数据库填充的审计字段 */
  SysFile save(SysFile file);

  /** 逻辑删除文件 */
  void logicDelete(Long id);

  /** 根据MD5查找文件(用于秒传/去重判断) */
  Optional<SysFile> findByMd5(String md5);
}
