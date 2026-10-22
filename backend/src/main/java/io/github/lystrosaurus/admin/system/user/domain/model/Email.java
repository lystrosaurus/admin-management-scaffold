package io.github.lystrosaurus.admin.system.user.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.shared.domain.primitive.ValueObject;
import java.util.regex.Pattern;

/**
 * 邮箱值对象。null 放行（邮箱可选），非 null 时校验格式。
 *
 * @param value 邮箱地址
 */
public record Email(String value) implements ValueObject {

  private static final Pattern PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  public Email {
    if (value != null && !PATTERN.matcher(value).matches()) {
      throw new DomainException(UserErrorCode.USER_EMAIL_INVALID);
    }
  }
}
