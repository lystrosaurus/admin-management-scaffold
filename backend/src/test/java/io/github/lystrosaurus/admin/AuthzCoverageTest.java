package io.github.lystrosaurus.admin;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.lystrosaurus.admin.auth.annotation.PermitAll;
import io.github.lystrosaurus.admin.auth.annotation.RequiresPermission;
import io.github.lystrosaurus.admin.auth.annotation.RequiresRole;
import io.github.lystrosaurus.admin.auth.adapter.web.AuthExternalAccountController;
import io.github.lystrosaurus.admin.auth.adapter.web.OAuthBindController;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权覆盖率测试（纯反射，不加载 Spring 上下文）。
 *
 * <p>扫描所有 {@code @RestController} 中 {@code @RequestMapping} 路径包含 {@code /app} 的类，
 * 断言其每个 HTTP 端点方法都携带 {@code @RequiresPermission} 或 {@code @PermitAll}
 * （或类级携带 {@code @RequiresPermission}）。
 *
 * <p>额外断言：
 * <ul>
 *   <li>(a) 禁止仅由类级 {@code @RequiresRole} 保护的端点（缺少方法级授权注解）。</li>
 *   <li>(b) {@code @PermitAll} 不得出现在 AuthExternalAccountController 与 OAuthBindController 的
 *        bind / unbind 方法上（均为需鉴权的绑定/解绑动作，必须 {@code @RequiresPermission}）。
 *        OAuthBindController 的 bind/unbind 经铁律决策改为 {@code @RequiresPermission}
 *        （自 scope，当前登录用户绑定/解绑自身三方账号），故此处同样对其设限。</li>
 * </ul>
 */
class AuthzCoverageTest {

  private static final String BASE_PACKAGE = "io.github.lystrosaurus.admin";

  @Test
  void everyAppEndpointMustDeclareAuthAnnotation() throws Exception {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

    List<Class<?>> controllers = new ArrayList<>();
    for (BeanDefinition bd : scanner.findCandidateComponents(BASE_PACKAGE)) {
      controllers.add(Class.forName(bd.getBeanClassName()));
    }

    int checked = 0;
    for (Class<?> clazz : controllers) {
      if (!isAppController(clazz)) {
        continue;
      }
      boolean classLevelRequiresPermission = clazz.isAnnotationPresent(RequiresPermission.class);
      boolean classLevelRequiresRole = clazz.isAnnotationPresent(RequiresRole.class);

      for (Method method : clazz.getDeclaredMethods()) {
        if (!Modifier.isPublic(method.getModifiers())) {
          continue;
        }
        if (!isHttpEndpoint(method)) {
          continue;
        }
        boolean methodRequiresPermission = method.isAnnotationPresent(RequiresPermission.class);
        boolean methodPermitAll = method.isAnnotationPresent(PermitAll.class);
        boolean methodRequiresRole = method.isAnnotationPresent(RequiresRole.class);

        // (a) 禁止仅由类级 @RequiresRole 保护的端点
        if (!methodRequiresPermission
            && !methodPermitAll
            && !classLevelRequiresPermission
            && !methodRequiresRole
            && classLevelRequiresRole) {
          fail(
              String.format(
                  "%s#%s 仅由类级 @RequiresRole 保护，缺少方法级授权注解",
                  clazz.getSimpleName(), method.getName()));
        }

        // 方法级 @RequiresPermission / @PermitAll，或类级 @RequiresPermission
        boolean protectedProperly =
            methodRequiresPermission || methodPermitAll || classLevelRequiresPermission;
        assertTrue(
            protectedProperly,
            String.format(
                "%s#%s 缺少 @RequiresPermission / @PermitAll，且类级无 @RequiresPermission",
                clazz.getSimpleName(), method.getName()));

        // (b) bind / unbind 方法不得出现 @PermitAll
        if (methodPermitAll && isForbiddenPermitAll(clazz, method)) {
          fail(
              String.format(
                  "%s#%s 不应出现 @PermitAll（bind/unbind 需鉴权）",
                  clazz.getSimpleName(), method.getName()));
        }
        checked++;
      }
    }
    assertTrue(checked > 0, "未扫描到任何 /app 端点，请检查扫描范围");
  }

  /** 类级或任意方法级 @RequestMapping 路径包含 /app 即视为目标控制器。 */
  private boolean isAppController(Class<?> clazz) {
    List<String> paths = new ArrayList<>();
    RequestMapping classRm = clazz.getAnnotation(RequestMapping.class);
    if (classRm != null) {
      paths.addAll(Arrays.asList(classRm.value()));
      paths.addAll(Arrays.asList(classRm.path()));
    }
    for (Method m : clazz.getDeclaredMethods()) {
      RequestMapping mRm = m.getAnnotation(RequestMapping.class);
      if (mRm != null) {
        paths.addAll(Arrays.asList(mRm.value()));
        paths.addAll(Arrays.asList(mRm.path()));
      }
    }
    return paths.stream().anyMatch(p -> p != null && p.contains("/app"));
  }

  private boolean isHttpEndpoint(Method method) {
    return method.isAnnotationPresent(GetMapping.class)
        || method.isAnnotationPresent(PostMapping.class)
        || method.isAnnotationPresent(PutMapping.class)
        || method.isAnnotationPresent(DeleteMapping.class)
        || method.isAnnotationPresent(RequestMapping.class);
  }

  private boolean isForbiddenPermitAll(Class<?> clazz, Method method) {
    // 铁律：OAuthBindController.bind/unbind 已改为 @RequiresPermission（自 scope），
    // 禁止 @PermitAll。此处对 AuthExternalAccountController 与 OAuthBindController 的
    // bind/unbind 统一设限，任一经 @PermitAll 标注即触发 CI 失败。
    if (clazz.equals(AuthExternalAccountController.class)
        || clazz.equals(OAuthBindController.class)) {
      return method.getName().equals("bind") || method.getName().equals("unbind");
    }
    return false;
  }
}
