package io.github.lystrosaurus.admin.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 四层架构守卫（adapter / application / domain / infrastructure）。
 *
 * <p>波次 1（user 上下文）启用：除 {@code transactional_event_listener_only_in_application} 仍冻结至波次 5 外，
 * 其余规则已启用。仅检查生产代码（排除测试类）；{@code only_infrastructure_depends_on_base_mapper} 暂限定在
 * {@code ..system.user..} 范围，待其余上下文迁移后恢复全局。规则细节见 {@code
 * .trae/documents/ddd-four-layer-refactor-plan.md}。
 */
@DisplayName("四层架构守卫（波次1 user 上下文启用）")
class FourLayerArchitectureTest {

  private final JavaClasses classes =
      new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages("io.github.lystrosaurus.admin");

  @Test
  @DisplayName("adapter 不应依赖 infrastructure")
  void adapter_should_not_depend_on_infrastructure() {
    noClasses()
        .that()
        .resideInAPackage("..adapter..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..infrastructure..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("adapter 不应直接依赖 domain.model（经 application 出入参隔离）")
  void adapter_should_not_depend_on_domain_model() {
    noClasses()
        .that()
        .resideInAPackage("..adapter..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..domain.model..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("application 不应依赖 infrastructure")
  void application_should_not_depend_on_infrastructure() {
    noClasses()
        .that()
        .resideInAPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..infrastructure..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("application 不应依赖 Spring Web / Servlet API")
  void application_should_not_depend_on_web() {
    noClasses()
        .that()
        .resideInAPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("org.springframework.web..", "jakarta.servlet..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("domain 层保持框架无关（Spring/MyBatis/Jackson/Servlet/Sa-Token）")
  void domain_should_be_framework_free() {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "org.springframework..",
            "com.baomidou..",
            "com.fasterxml.jackson..",
            "jakarta.servlet..",
            "jakarta.persistence..",
            "cn.dev33.satoken..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("@TableName 持久化类必须以 PO 结尾并继承 BasePO")
  void po_naming_and_hierarchy() {
    classes()
        .that()
        .areAnnotatedWith(com.baomidou.mybatisplus.annotation.TableName.class)
        .and()
        .resideInAPackage("..infrastructure..")
        .should()
        .haveSimpleNameEndingWith("PO")
        .andShould()
        .beAssignableTo(BasePO.class)
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("聚合根必须位于 domain.model 包")
  void aggregate_roots_must_reside_in_domain_model() {
    classes()
        .that()
        .areAssignableTo(AggregateRoot.class)
        .and()
        .doNotHaveFullyQualifiedName(AggregateRoot.class.getName())
        .should()
        .resideInAPackage("..domain.model..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("仅 infrastructure 可依赖 BaseMapper（波次1 限定 user 上下文）")
  void only_infrastructure_depends_on_base_mapper() {
    noClasses()
        .that()
        .resideInAPackage("..system.user..")
        .and()
        .resideOutsideOfPackage("..infrastructure..")
        .and()
        // SysUserRoleMapper 按计划保留至波次2 随 role 上下文迁移，其自身继承 BaseMapper 不算违规
        .doNotHaveFullyQualifiedName(
            "io.github.lystrosaurus.admin.system.user.mapper.SysUserRoleMapper")
        .should()
        .dependOnClassesThat()
        .areAssignableTo(com.baomidou.mybatisplus.core.mapper.BaseMapper.class)
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @Disabled("波次5启用（audit 事件化）")
  @DisplayName("@TransactionalEventListener 仅允许出现在 application 包")
  void transactional_event_listener_only_in_application() {
    methods()
        .that()
        .areAnnotatedWith(org.springframework.transaction.event.TransactionalEventListener.class)
        .should()
        .beDeclaredInClassesThat()
        .resideInAPackage("..application..")
        .allowEmptyShould(true)
        .check(classes);
  }
}
