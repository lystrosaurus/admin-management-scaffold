package io.github.lystrosaurus.admin.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

/** 分层与依赖约束校验 */
class ArchitectureTest {

  private final JavaClasses classes =
      new ClassFileImporter().importPackages("io.github.lystrosaurus.admin");

  @Test
  void controller_should_not_depend_on_mapper_directly() {
    classes()
        .that()
        .resideInAPackage("..controller..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..mapper..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  void service_should_not_depend_on_controller() {
    classes()
        .that()
        .resideInAPackage("..service..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..controller..")
        .allowEmptyShould(true)
        .check(classes);
  }
}
