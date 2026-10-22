package io.github.lystrosaurus.admin.system.dict.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.system.dict.domain.event.DictCreated;
import io.github.lystrosaurus.admin.system.dict.domain.event.DictUpdated;
import java.time.LocalDateTime;

/** 字典聚合根：守卫字典项的不变量，行为入口即业务规则落点。 */
public class Dict extends AggregateRoot<DictId> {

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

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 版本号（乐观锁） */
  private Integer version;

  private Dict(DictId id) {
    super(id);
  }

  /**
   * 创建新字典：排序号默认 0，状态默认 ENABLED，并登记 {@link DictCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param dictType 字典类型
   * @param dictLabel 字典标签
   * @param dictValue 字典值
   * @param sortOrder 排序号
   * @param status 状态
   * @param remark 备注
   * @return 新字典聚合
   */
  public static Dict create(
      DictId id,
      String dictType,
      String dictLabel,
      String dictValue,
      Integer sortOrder,
      String status,
      String remark) {
    Dict dict = new Dict(id);
    dict.dictType = dictType;
    dict.dictLabel = dictLabel;
    dict.dictValue = dictValue;
    dict.sortOrder = sortOrder != null ? sortOrder : 0;
    dict.status = hasText(status) ? status : "ENABLED";
    dict.remark = remark;
    dict.registerEvent(DictCreated.of(id.value(), dictType));
    return dict;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 字典聚合
   */
  public static Dict restore(
      DictId id,
      String dictType,
      String dictLabel,
      String dictValue,
      Integer sortOrder,
      String status,
      String remark,
      LocalDateTime createdAt,
      Integer version) {
    Dict dict = new Dict(id);
    dict.dictType = dictType;
    dict.dictLabel = dictLabel;
    dict.dictValue = dictValue;
    dict.sortOrder = sortOrder;
    dict.status = status;
    dict.remark = remark;
    dict.createdAt = createdAt;
    dict.version = version;
    return dict;
  }

  /** 更新字典资料：仅更新非空文本字段与非空排序号，登记 {@link DictUpdated} 事件。 */
  public void update(
      String dictType,
      String dictLabel,
      String dictValue,
      Integer sortOrder,
      String status,
      String remark) {
    if (hasText(dictType)) {
      this.dictType = dictType;
    }
    if (hasText(dictLabel)) {
      this.dictLabel = dictLabel;
    }
    if (hasText(dictValue)) {
      this.dictValue = dictValue;
    }
    if (sortOrder != null) {
      this.sortOrder = sortOrder;
    }
    if (hasText(status)) {
      this.status = status;
    }
    if (hasText(remark)) {
      this.remark = remark;
    }
    registerEvent(DictUpdated.of(id.value()));
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public String dictType() {
    return dictType;
  }

  public String dictLabel() {
    return dictLabel;
  }

  public String dictValue() {
    return dictValue;
  }

  public Integer sortOrder() {
    return sortOrder;
  }

  public String status() {
    return status;
  }

  public String remark() {
    return remark;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Integer version() {
    return version;
  }
}
