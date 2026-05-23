package io.github.lystrosaurus.admin.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** ErrorCode 枚举单元测试 */
class ErrorCodeTest {

  @Test
  void should_have_category_number_format_when_defining_error_codes() {
    // 验证所有 ErrorCode 都使用 CATEGORY_NUMBER 格式
    for (ErrorCode errorCode : ErrorCode.values()) {
      String codeName = errorCode.name();
      // 检查是否包含下划线分隔符
      assertTrue(codeName.contains("_"), "ErrorCode 名称应该使用 CATEGORY_NUMBER 格式: " + codeName);
      // 检查格式是否为 CATEGORY_XXX
      String[] parts = codeName.split("_");
      assertTrue(parts.length >= 2, "ErrorCode 名称应该至少有两部分: " + codeName);
      // 验证第一部分是有效的类别
      String category = parts[0];
      assertTrue(
          category.equals("SYSTEM")
              || category.equals("AUTH")
              || category.equals("USER")
              || category.equals("ROLE")
              || category.equals("PERMISSION")
              || category.equals("MENU")
              || category.equals("DATA")
              || category.equals("PASSWORD"),
          "ErrorCode 类别应该是有效的: " + category);
    }
  }

  @Test
  void should_have_system_category_when_defining_common_errors() {
    // 验证系统通用错误码使用 SYSTEM_ 前缀
    ErrorCode[] systemErrors = {
      ErrorCode.SYSTEM_SUCCESS,
      ErrorCode.SYSTEM_400,
      ErrorCode.SYSTEM_401,
      ErrorCode.SYSTEM_403,
      ErrorCode.SYSTEM_404,
      ErrorCode.SYSTEM_409,
      ErrorCode.SYSTEM_429,
      ErrorCode.SYSTEM_500
    };

    for (ErrorCode errorCode : systemErrors) {
      assertTrue(errorCode.name().startsWith("SYSTEM_"), "系统错误码应该以 SYSTEM_ 开头");
    }
  }

  @Test
  void should_have_auth_category_when_defining_auth_errors() {
    // 验证认证相关错误码使用 AUTH_ 前缀
    ErrorCode[] authErrors = {
      ErrorCode.AUTH_401,
      ErrorCode.AUTH_403,
      ErrorCode.AUTH_TOKEN_EXPIRED,
      ErrorCode.AUTH_TOKEN_INVALID
    };

    for (ErrorCode errorCode : authErrors) {
      assertTrue(errorCode.name().startsWith("AUTH_"), "认证错误码应该以 AUTH_ 开头");
    }
  }

  @Test
  void should_have_user_category_when_defining_user_errors() {
    // 验证用户相关错误码使用 USER_ 前缀
    ErrorCode[] userErrors = {
      ErrorCode.USER_NOT_FOUND,
      ErrorCode.USER_ALREADY_EXISTS,
      ErrorCode.USER_INVALID_PASSWORD,
      ErrorCode.USER_ACCOUNT_DISABLED
    };

    for (ErrorCode errorCode : userErrors) {
      assertTrue(errorCode.name().startsWith("USER_"), "用户错误码应该以 USER_ 开头");
    }
  }

  @Test
  void should_have_unique_code_values_when_defining_error_codes() {
    // 验证所有错误码的 code 值都是唯一的
    ErrorCode[] values = ErrorCode.values();
    for (int i = 0; i < values.length; i++) {
      for (int j = i + 1; j < values.length; j++) {
        assertNotEquals(
            values[i].getCode(),
            values[j].getCode(),
            "ErrorCode 的 code 值应该唯一: " + values[i].name() + " 和 " + values[j].name());
      }
    }
  }

  @Test
  void should_have_role_category_when_defining_role_errors() {
    // 验证角色相关错误码使用 ROLE_ 前缀
    ErrorCode[] roleErrors = {ErrorCode.ROLE_NOT_FOUND, ErrorCode.ROLE_ALREADY_EXISTS};

    for (ErrorCode errorCode : roleErrors) {
      assertTrue(errorCode.name().startsWith("ROLE_"), "角色错误码应该以 ROLE_ 开头");
    }
  }

  @Test
  void should_have_permission_category_when_defining_permission_errors() {
    // 验证权限相关错误码使用 PERMISSION_ 前缀
    ErrorCode[] permissionErrors = {ErrorCode.PERMISSION_DENIED, ErrorCode.PERMISSION_NOT_FOUND};

    for (ErrorCode errorCode : permissionErrors) {
      assertTrue(errorCode.name().startsWith("PERMISSION_"), "权限错误码应该以 PERMISSION_ 开头");
    }
  }

  @Test
  void should_have_menu_category_when_defining_menu_errors() {
    // 验证菜单相关错误码使用 MENU_ 前缀
    ErrorCode[] menuErrors = {ErrorCode.MENU_NOT_FOUND, ErrorCode.MENU_ALREADY_EXISTS};

    for (ErrorCode errorCode : menuErrors) {
      assertTrue(errorCode.name().startsWith("MENU_"), "菜单错误码应该以 MENU_ 开头");
    }
  }

  @Test
  void should_have_data_category_when_defining_data_errors() {
    // 验证数据相关错误码使用 DATA_ 前缀
    ErrorCode[] dataErrors = {ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.DATA_DUPLICATE_KEY};

    for (ErrorCode errorCode : dataErrors) {
      assertTrue(errorCode.name().startsWith("DATA_"), "数据错误码应该以 DATA_ 开头");
    }
  }

  @Test
  void should_have_non_null_message_when_defining_error_codes() {
    // 验证所有错误码都有非空的消息
    for (ErrorCode errorCode : ErrorCode.values()) {
      assertNotNull(errorCode.getMessage(), "ErrorCode 的消息不应该为 null: " + errorCode.name());
      assertFalse(errorCode.getMessage().isEmpty(), "ErrorCode 的消息不应该为空: " + errorCode.name());
    }
  }
}
