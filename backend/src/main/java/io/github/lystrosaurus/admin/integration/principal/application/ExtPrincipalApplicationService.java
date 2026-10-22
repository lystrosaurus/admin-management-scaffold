package io.github.lystrosaurus.admin.integration.principal.application;

import io.github.lystrosaurus.admin.integration.principal.adapter.response.ExtPrincipalResponse;
import io.github.lystrosaurus.admin.integration.principal.application.assembler.ExtPrincipalAssembler;
import io.github.lystrosaurus.admin.integration.principal.application.command.ExtPrincipalCreateCommand;
import io.github.lystrosaurus.admin.integration.principal.application.command.ExtPrincipalUpdateCommand;
import io.github.lystrosaurus.admin.integration.principal.domain.acl.ExtPrincipalRepository;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipal;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalErrorCode;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalId;
import io.github.lystrosaurus.admin.integration.principal.domain.model.ExtPrincipalIdentifier;
import io.github.lystrosaurus.admin.integration.source.domain.acl.ExtSourceQueryPort;
import io.github.lystrosaurus.admin.shared.domain.acl.IdGenerator;
import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 外部主体应用服务：编排外部主体聚合的创建、更新、删除与查询。 */
@Service
@RequiredArgsConstructor
public class ExtPrincipalApplicationService {

  private final ExtPrincipalRepository extPrincipalRepository;
  private final ExtSourceQueryPort extSourceQueryPort;
  private final IdGenerator idGenerator;
  private final ExtPrincipalAssembler assembler;

  /** 创建外部主体 */
  @Transactional(rollbackFor = Exception.class)
  public ExtPrincipalResponse create(ExtPrincipalCreateCommand command) {
    if (extPrincipalRepository.existsBySourceAndExternalKey(
        command.sourceId(), command.principalType(), command.externalKey())) {
      throw new DomainException(ExtPrincipalErrorCode.PRINCIPAL_ALREADY_EXISTS);
    }

    List<ExtPrincipalIdentifier> identifiers =
        command.identifiers() == null
            ? List.of()
            : command.identifiers().stream()
                .map(
                    item ->
                        new ExtPrincipalIdentifier(
                            idGenerator.nextId(),
                            item.idType(),
                            item.idValue(),
                            Boolean.TRUE.equals(item.isPrimary())))
                .toList();

    ExtPrincipal principal =
        ExtPrincipal.create(
            ExtPrincipalId.of(idGenerator.nextId()),
            command.sourceId(),
            command.principalType(),
            command.externalKey(),
            command.displayName(),
            command.rawPayloadJson(),
            identifiers);
    extPrincipalRepository.save(principal);

    // 重新加载以回读数据库填充的审计字段与标识符，保持响应契约与旧实现一致
    ExtPrincipal saved = extPrincipalRepository.findById(principal.id()).orElseThrow();
    return assembler.toResponse(saved, extSourceQueryPort.findNameById(saved.sourceId()));
  }

  /** 更新外部主体（部分更新：仅非空字段生效） */
  @Transactional(rollbackFor = Exception.class)
  public ExtPrincipalResponse update(Long id, ExtPrincipalUpdateCommand command) {
    ExtPrincipal principal = findOrThrow(id);
    principal.update(
        command.displayName(),
        command.status(),
        command.canonicalType(),
        command.canonicalId(),
        command.linkStatus());
    extPrincipalRepository.save(principal);

    ExtPrincipal saved = extPrincipalRepository.findById(principal.id()).orElseThrow();
    return assembler.toResponse(saved, extSourceQueryPort.findNameById(saved.sourceId()));
  }

  /** 删除外部主体（连带标识符） */
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    findOrThrow(id);
    extPrincipalRepository.deleteById(ExtPrincipalId.of(id));
  }

  /** 查询外部主体详情 */
  public ExtPrincipalResponse findById(Long id) {
    ExtPrincipal principal = findOrThrow(id);
    return assembler.toResponse(
        principal, extSourceQueryPort.findNameById(principal.sourceId()));
  }

  /** 按条件查询外部主体列表 */
  public List<ExtPrincipalResponse> list(Long sourceId, String linkStatus) {
    return extPrincipalRepository.findByCondition(sourceId, linkStatus).stream()
        .map(
            principal ->
                assembler.toResponse(
                    principal, extSourceQueryPort.findNameById(principal.sourceId())))
        .toList();
  }

  private ExtPrincipal findOrThrow(Long id) {
    return extPrincipalRepository
        .findById(ExtPrincipalId.of(id))
        .orElseThrow(() -> new DomainException(ExtPrincipalErrorCode.PRINCIPAL_NOT_FOUND));
  }
}
