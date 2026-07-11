package io.github.lystrosaurus.admin.contract;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * 前后端契约字段对齐测试。
 *
 * <p>背景：曾发生「飞书点击绑定跳首页」的线上 bug——根因是后端
 * {@code OAuthAuthorizeVO} 序列化字段名为 {@code authorizeUrl}，而前端 {@code oauth.ts}
 * 解构的是 {@code { url }}，拿到 {@code undefined} 后 {@code window.location.href = undefined}
 * 静默跳回首页。后端 MVC 测试只「自验证」后端发了什么，从不校验前端期望的字段名，
 * 因此该契约裂缝长期未被发现。
 *
 * <p>本测试把「前端类型声明的字段集合」与「后端 VO 实际序列化字段集合」做差集断言，
 * 直接守住该类回归：任一侧改了字段名，只要不再对齐，此测试立即变红。
 */
class ContractFieldMatchTest {

  /** 前端类型名 -> 后端 VO 全限定类名（二者按 (方法+路径) 配对，此处手工登记关键契约）。 */
  private static final Map<String, String> MANIFEST =
      Map.of(
          "OAuthAuthorizeUrl",
              "io.github.lystrosaurus.admin.auth.oauth.vo.OAuthAuthorizeVO",
          "OAuthAccount",
              "io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO",
          "SecurityProfileVO",
              "io.github.lystrosaurus.admin.auth.vo.SecurityProfileVO",
          "UserVO", "io.github.lystrosaurus.admin.system.user.vo.UserVO",
          "MenuVO", "io.github.lystrosaurus.admin.system.menu.vo.MenuVO");

  private Path typesFile() {
    // Maven Surefire 的工作目录是后端模块目录（backend/）
    Path base = Paths.get(System.getProperty("user.dir"));
    Path p = base.resolveSibling("frontend").resolve("src").resolve("types").resolve("auth.ts");
    if (!Files.exists(p)) {
      // 兜底：绝对路径（跨环境兼容）
      p = Paths.get("C:/workspace/admin-management-scaffold/frontend/src/types/auth.ts");
    }
    return p;
  }

  /** 解析 TS 文件，返回 类型名 -> 字段名集合。 */
  private Map<String, Set<String>> parseTsTypes(Path file) throws Exception {
    String src = Files.readString(file);
    Map<String, Set<String>> map = new HashMap<>();
    // 匹配 `type X = { ... }` 或 `interface X { ... }`
    Pattern block =
        Pattern.compile("(type|interface)\\s+(\\w+)\\s*(?:=\\s*)?\\{([^}]*)\\}", Pattern.DOTALL);
    Matcher m = block.matcher(src);
    while (m.find()) {
      String name = m.group(2);
      String body = m.group(3);
      Set<String> fields = new HashSet<>();
      Matcher fm = Pattern.compile("(\\w+)\\s*\\??\\s*:").matcher(body);
      while (fm.find()) {
        fields.add(fm.group(1));
      }
      map.put(name, fields);
    }
    return map;
  }

  /** 取后端 VO 的序列化字段名（record 用组件名，class 用声明字段名）。 */
  private Set<String> backendFields(String fqn) throws Exception {
    Class<?> c = Class.forName(fqn);
    Set<String> set = new HashSet<>();
    if (c.isRecord()) {
      for (RecordComponent rc : c.getRecordComponents()) {
        set.add(rc.getName());
      }
    } else {
      for (Field f : c.getDeclaredFields()) {
        set.add(f.getName());
      }
    }
    return set;
  }

  @Test
  void frontendFieldsSubsetOfBackendVoFields() throws Exception {
    Map<String, Set<String>> ts = parseTsTypes(typesFile());
    StringBuilder failures = new StringBuilder();

    for (Map.Entry<String, String> e : MANIFEST.entrySet()) {
      String feType = e.getKey();
      Set<String> feFields = ts.get(feType);
      assertNotNull(feFields, "前端类型未在 types/auth.ts 找到: " + feType);
      Set<String> beFields = backendFields(e.getValue());

      for (String f : feFields) {
        if (!beFields.contains(f)) {
          failures.append(
              String.format(
                  "  [%s] 前端字段 '%s' 在后端 VO %s 中不存在（后端字段: %s）%n",
                  feType, f, e.getValue(), beFields));
        }
      }
    }

    if (failures.length() > 0) {
      fail("前后端契约不匹配（前端字段 ⊄ 后端 VO 字段）:\n" + failures);
    }
  }

  /**
   * 显式回归用例：飞书 bug 的根因保护。
   *
   * <p>前端 {@code OAuthAuthorizeUrl} 期望字段 {@code url}；后端 {@code OAuthAuthorizeVO}
   * 必须序列化 {@code url}，且绝不能再出现 {@code authorizeUrl}。
   */
  @Test
  void oauthAuthorizeUrlFieldIsUrlNotAuthorizeUrl() throws Exception {
    Set<String> be = backendFields("io.github.lystrosaurus.admin.auth.oauth.vo.OAuthAuthorizeVO");
    assertTrue(
        be.contains("url"),
        "OAuthAuthorizeVO 必须序列化字段 'url'（前端 OAuthAuthorizeUrl.url 依赖此字段），实际字段: " + be);
    assertFalse(
        be.contains("authorizeUrl"),
        "回归保护：OAuthAuthorizeVO 不应再使用 'authorizeUrl'（曾导致飞书绑定跳首页）");
  }
}
