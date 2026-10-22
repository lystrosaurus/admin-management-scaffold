package io.github.lystrosaurus.admin.system.dict.adapter.request;

import io.github.lystrosaurus.admin.system.dict.application.command.DictUpdateCommand;

/**
 * 字典更新请求（字段与原 DictUpdateDTO 一致）。
 *
 * @param dictType 字典类型
 * @param dictLabel 字典标签
 * @param dictValue 字典值
 * @param sortOrder 排序号
 * @param status 状态
 * @param remark 备注
 */
public record DictUpdateRequest(
    String dictType, String dictLabel, String dictValue, Integer sortOrder, String status, String remark) {

  /** 转为应用层命令 */
  public DictUpdateCommand toCommand() {
    return new DictUpdateCommand(dictType, dictLabel, dictValue, sortOrder, status, remark);
  }
}
