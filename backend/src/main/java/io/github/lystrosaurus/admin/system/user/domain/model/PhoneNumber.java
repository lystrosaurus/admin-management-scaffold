package io.github.lystrosaurus.admin.system.user.domain.model;

import io.github.lystrosaurus.admin.shared.domain.exception.DomainException;
import io.github.lystrosaurus.admin.shared.domain.primitive.ValueObject;
import java.util.regex.Pattern;

/**
 * 手机号值对象。null 放行（手机号可选），非 null 时校验格式。
 *
 * @param value 手机号
 */
public record PhoneNumber(String value) implements ValueObject {

  private static final Pattern PATTERN = Pattern.compile("^[0-9+\\-() ]{5,20}$");

  public PhoneNumber {
    if (value != null && !PATTERN.matcher(value).matches()) {
      throw new DomainException(UserErrorCode.USER_PHONE_INVALID);
    }
  }
}
