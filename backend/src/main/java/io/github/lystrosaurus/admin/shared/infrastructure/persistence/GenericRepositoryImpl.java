package io.github.lystrosaurus.admin.shared.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.lystrosaurus.admin.shared.domain.acl.DomainEventPublisher;
import io.github.lystrosaurus.admin.shared.domain.acl.GenericRepository;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.shared.domain.primitive.Identifier;
import java.util.Optional;

/**
 * 通用仓储的 MyBatis-Plus 实现。
 *
 * <p>子类只需实现 {@link #toPo} / {@link #toDomain} 两个转换钩子（建议委托 MapStruct converter）， 并以
 * {@code @Repository} 声明为 Spring Bean。乐观锁由 {@link BasePO#version} 上的 {@code @Version} 驱动，更新冲突时由
 * MyBatis-Plus 抛出异常。
 *
 * @param <T> 聚合根类型
 * @param <ID> 标识类型
 * @param <PO> 持久化对象类型
 * @param <M> MyBatis-Plus Mapper 类型
 */
public abstract class GenericRepositoryImpl<
        T extends AggregateRoot<ID>,
        ID extends Identifier,
        PO extends BasePO,
        M extends BaseMapper<PO>>
    implements GenericRepository<T, ID> {

  protected final M mapper;
  protected final DomainEventPublisher eventPublisher;

  protected GenericRepositoryImpl(M mapper, DomainEventPublisher eventPublisher) {
    this.mapper = mapper;
    this.eventPublisher = eventPublisher;
  }

  /** 领域对象转持久化对象 */
  protected abstract PO toPo(T domain);

  /** 持久化对象转领域对象（重建聚合，不登记事件） */
  protected abstract T toDomain(PO po);

  @Override
  public Optional<T> findById(ID id) {
    return Optional.ofNullable(mapper.selectById(id.value())).map(this::toDomain);
  }

  @Override
  public T save(T aggregate) {
    PO po = toPo(aggregate);
    if (mapper.exists(
        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PO>()
            .eq("id", po.getId()))) {
      mapper.updateById(po);
    } else {
      mapper.insert(po);
    }
    eventPublisher.publishAll(aggregate);
    return aggregate;
  }

  @Override
  public void deleteById(ID id) {
    mapper.deleteById(id.value());
  }

  @Override
  public boolean existsById(ID id) {
    return mapper.selectById(id.value()) != null;
  }
}
