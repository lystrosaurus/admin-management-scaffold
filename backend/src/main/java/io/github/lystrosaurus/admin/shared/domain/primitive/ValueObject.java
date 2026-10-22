package io.github.lystrosaurus.admin.shared.domain.primitive;

/**
 * 值对象标记接口：以值判等，无标识。空接口设计使 record 可以直接实现。
 *
 * <p>实现类应在构造期完成校验，违反约束时抛出领域异常。
 */
public interface ValueObject {}
