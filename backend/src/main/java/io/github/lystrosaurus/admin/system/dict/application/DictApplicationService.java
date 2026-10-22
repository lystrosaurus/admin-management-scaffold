package io.github.lystrosaurus.admin.system.dict.application;

import io.github.lystrosaurus.admin.shared.adapter.web.PageResult;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.acl.Page;
import io.github.lystrosaurus.admin.shared.domain.acl.PageQuery;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.system.dict.adapter.response.DictResponse;
import io.github.lystrosaurus.admin.system.dict.application.assembler.DictAssembler;
import io.github.lystrosaurus.admin.system.dict.application.command.DictCreateCommand;
import io.github.lystrosaurus.admin.system.dict.application.command.DictUpdateCommand;
import io.github.lystrosaurus.admin.system.dict.domain.acl.DictQueryRepository;
import io.github.lystrosaurus.admin.system.dict.domain.acl.DictRepository;
import io.github.lystrosaurus.admin.system.dict.domain.model.Dict;
import io.github.lystrosaurus.admin.system.dict.domain.model.DictErrorCode;
import io.github.lystrosaurus.admin.system.dict.domain.model.DictId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 字典应用服务：编排字典聚合的创建、更新、删除与查询。 */
@Service
@RequiredArgsConstructor
public class DictApplicationService {

  private final DictRepository dictRepository;
  private final DictQueryRepository dictQueryRepository;
  private final IdGenerator idGenerator;
  private final DictAssembler assembler;

  /** 创建字典 */
  @Transactional(rollbackFor = Exception.class)
  public DictResponse create(DictCreateCommand command) {
    Dict dict =
        Dict.create(
            DictId.of(idGenerator.nextId()),
            command.dictType(),
            command.dictLabel(),
            command.dictValue(),
            command.sortOrder(),
            command.status(),
            command.remark());
    dictRepository.save(dict);
    return assembler.toResponse(dict);
  }

  /** 更新字典 */
  @Transactional(rollbackFor = Exception.class)
  public DictResponse update(Long id, DictUpdateCommand command) {
    Dict dict = findDict(id);
    dict.update(
        command.dictType(),
        command.dictLabel(),
        command.dictValue(),
        command.sortOrder(),
        command.status(),
        command.remark());
    dictRepository.save(dict);
    return assembler.toResponse(dict);
  }

  /** 删除字典 */
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Long id) {
    dictRepository.deleteById(DictId.of(id));
  }

  /** 分页查询字典（可按字典类型过滤） */
  public PageResult<DictResponse> list(String dictType, int page, int size) {
    Page<Dict> result = dictQueryRepository.findPage(dictType, PageQuery.of(page, size));
    List<DictResponse> responses = result.records().stream().map(assembler::toResponse).toList();
    return new PageResult<>(responses, result.total(), result.pageNum(), result.pageSize());
  }

  /** 查询字典详情 */
  public DictResponse getById(Long id) {
    return assembler.toResponse(findDict(id));
  }

  private Dict findDict(Long id) {
    return dictRepository
        .findById(DictId.of(id))
        .orElseThrow(() -> new DomainException(DictErrorCode.DICT_NOT_FOUND));
  }
}
