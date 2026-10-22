package io.github.lystrosaurus.admin.integration.source.application;

import io.github.lystrosaurus.admin.integration.source.adapter.response.ExtSourceResponse;
import io.github.lystrosaurus.admin.integration.source.application.assembler.ExtSourceAssembler;
import io.github.lystrosaurus.admin.integration.source.application.command.ExtSourceCreateCommand;
import io.github.lystrosaurus.admin.integration.source.application.command.ExtSourceUpdateCommand;
import io.github.lystrosaurus.admin.integration.source.domain.acl.ExtSourceRepository;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSource;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSourceErrorCode;
import io.github.lystrosaurus.admin.integration.source.domain.model.ExtSourceId;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 外部身份源应用服务：编排外部身份源聚合的创建、更新、删除与查询。 */
@Service
@RequiredArgsConstructor
public class ExtSourceApplicationService {

  private final ExtSourceRepository extSourceRepository;
  private final IdGenerator idGenerator;
  private final ExtSourceAssembler assembler;

  /** 创建外部身份源 */
  @Transactional(rollbackFor = Exception.class)
  public ExtSourceResponse create(ExtSourceCreateCommand command) {
    if (extSourceRepository.existsByCode(command.code())) {
      throw new DomainException(ExtSourceErrorCode.SOURCE_ALREADY_EXISTS);
    }

    ExtSource source =
        ExtSource.create(
            ExtSourceId.of(idGenerator.nextId()),
            command.code(),
            command.name(),
            command.sourceType(),
            command.tenantKey(),
            command.priority(),
            command.configJson());
    extSourceRepository.save(source);

    // 重新加载以回读数据库填充的审计字段，保持响应契约与旧实现一致
    ExtSource saved = extSourceRepository.findById(source.id()).orElseThrow();
    return assembler.toResponse(saved);
  }

  /** 更新外部身份源（部分更新：仅非空字段生效） */
  @Transactional(rollbackFor = Exception.class)
  public ExtSourceResponse update(Long id, ExtSourceUpdateCommand command) {
    ExtSource source = findOrThrow(id);
    source.update(
        command.name(),
        command.sourceType(),
        command.tenantKey(),
        command.status(),
        command.priority(),
        command.configJson());
    extSourceRepository.save(source);

    ExtSource saved = extSourceRepository.findById(source.id()).orElseThrow();
    return assembler.toResponse(saved);
  }

  /** 删除外部身份源 */
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    findOrThrow(id);
    extSourceRepository.deleteById(ExtSourceId.of(id));
  }

  /** 查询外部身份源详情 */
  public ExtSourceResponse findById(Long id) {
    return assembler.toResponse(findOrThrow(id));
  }

  /** 查询全部外部身份源 */
  public List<ExtSourceResponse> list() {
    return extSourceRepository.findAll().stream().map(assembler::toResponse).toList();
  }

  private ExtSource findOrThrow(Long id) {
    return extSourceRepository
        .findById(ExtSourceId.of(id))
        .orElseThrow(() -> new DomainException(ExtSourceErrorCode.SOURCE_NOT_FOUND));
  }
}
