package io.github.lystrosaurus.admin.shared.domain.primitive;

/**
 * 类型化标识：让 {@code UserId(1)} 与 {@code RoleId(1)} 在编译期不可互换。
 *
 * <p>各上下文以 record 实现，例如 {@code public record UserId(Long value) implements Identifier {}}
 */
public interface Identifier {

  /** 标识值 */
  Long value();
}
