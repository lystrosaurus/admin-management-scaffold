package io.github.lystrosaurus.admin.system.dict.adapter.request;

import io.github.lystrosaurus.admin.system.dict.application.command.DictCreateCommand;
import jakarta.validation.constraints.NotBlank;

/**
 * 字典创建请求（字段与原 DictCreateDTO 一致）。
 *
 * @param dictType 字典类型
 * @param dictLabel 字典标签
 * @param dictValue 字典值
 * @param sortOrder 排序号
 * @param status 状态
 * @param remark 备注
 */
public record DictCreateRequest(
    @NotBlank(message = "字典类型不能为空") String dictType,
    @NotBlank(message = "字典标签不能为空") String dictLabel,
    @NotBlank(message = "字典值不能为空") String dictValue,
    Integer sortOrder,
    String status,
    String remark) {

  /** 转为应用层命令 */
  public DictCreateCommand toCommand() {
    return new DictCreateCommand(dictType, dictLabel, dictValue, sortOrder, status, remark);
  }
}
