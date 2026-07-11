package io.github.lystrosaurus.admin.file.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.lystrosaurus.admin.file.entity.SysFile;
import java.util.Optional;

/** 文件数据访问对象接口 */
public interface SysFileDAO {

  /** 根据ID查找文件 */
  Optional<SysFile> findById(Long id);

  /** 条件分页查询文件 */
  Page<SysFile> findPage(String filename, String bizType, int page, int size);

  /** 保存文件元数据 */
  void save(SysFile file);

  /** 逻辑删除文件 */
  void logicDelete(Long id);

  /** 根据MD5查找文件(用于秒传/去重判断) */
  Optional<SysFile> findByMd5(String md5);
}
