package io.github.lystrosaurus.admin.system.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/** 字典数据实体 */
@Getter
@Setter
@TableName("sys_dict")
public class SysDict extends BaseEntity {

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
