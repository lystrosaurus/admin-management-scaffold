package io.github.lystrosaurus.admin.system.dict.application.command;

/**
 * 更新字典命令。
 *
 * @param dictType 字典类型
 * @param dictLabel 字典标签
 * @param dictValue 字典值
 * @param sortOrder 排序号
 * @param status 状态
 * @param remark 备注
 */
public record DictUpdateCommand(
    String dictType, String dictLabel, String dictValue, Integer sortOrder, String status, String remark) {}
