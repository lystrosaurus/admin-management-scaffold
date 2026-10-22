package io.github.lystrosaurus.admin.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 四层架构守卫（adapter / application / domain / infrastructure）。
 *
 * <p>波次 0 冻结：全部规则 @Disabled，随各上下文四层化迁移逐波启用； 波次 8 清理时全部启用并移除 @Disabled。规则细节见 {@code
 * .trae/documents/ddd-four-layer-refactor-plan.md}。
 */
@DisplayName("四层架构守卫（冻结态）")
class FourLayerArchitectureTest {

  private final JavaClasses classes =
      new ClassFileImporter().importPackages("io.github.lystrosaurus.admin");

  @Test
  @Disabled("波次1起逐上下文启用")
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
  @Disabled("波次1起逐上下文启用")
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
  @Disabled("波次1起逐上下文启用")
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
  @Disabled("波次1起逐上下文启用")
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
  @Disabled("波次1起逐上下文启用")
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
  @Disabled("波次1起逐上下文启用")
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
  @Disabled("波次1起逐上下文启用")
  @DisplayName("聚合根必须位于 domain.model 包")
  void aggregate_roots_must_reside_in_domain_model() {
    classes()
        .that()
        .areAssignableTo(AggregateRoot.class)
        .should()
        .resideInAPackage("..domain.model..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @Disabled("波次1起逐上下文启用")
  @DisplayName("仅 infrastructure 可依赖 BaseMapper")
  void only_infrastructure_depends_on_base_mapper() {
    noClasses()
        .that()
        .resideOutsideOfPackage("..infrastructure..")
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
