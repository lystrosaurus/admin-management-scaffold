package io.github.lystrosaurus.admin.system.dict.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 字典持久化对象：仅用于 infrastructure 层映射 sys_dict 表。 */
@Getter
@Setter
@TableName("sys_dict")
public class DictPO extends BasePO {

  /** 字典类型 */
  private String dictType;

  /** 字典标签 */
  private String dictLabel;

  /** 字典值 */
  private String dictValue;

  /** 排序号 */
  private Integer sortOrder;

  /** 状态(ENABLED/DISABLED) */
  private String status;

  /** 备注 */
  private String remark;
}
