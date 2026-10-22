package io.github.lystrosaurus.admin.shared.domain.primitive;

/**
 * 规格：可组合的业务谓词，用于不变量校验与准入判断。
 *
 * @param <T> 被校验对象类型
 */
@FunctionalInterface
public interface Specification<T> {

  /** 候选对象是否满足规格 */
  boolean isSatisfiedBy(T candidate);

  default Specification<T> and(Specification<T> other) {
    return candidate -> isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
  }

  default Specification<T> or(Specification<T> other) {
    return candidate -> isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
  }

  default Specification<T> not() {
    return candidate -> !isSatisfiedBy(candidate);
  }
}
