package io.github.lystrosaurus.admin.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** 分层与依赖约束校验 — 覆盖 Controller / Service / DAO / Entity / DTO-VO 四层 */
@DisplayName("架构分层约束")
class ArchitectureTest {

  private final JavaClasses classes =
      new ClassFileImporter().importPackages("io.github.lystrosaurus.admin");

  // ==================== Controller 层约束 ====================

  @Test
  @DisplayName("Controller 不应依赖 Mapper")
  void controller_should_not_depend_on_mapper() {
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
  @DisplayName("Controller 不应依赖 DAO")
  void controller_should_not_depend_on_dao() {
    classes()
        .that()
        .resideInAPackage("..controller..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..dao..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("Controller 不应依赖 Entity")
  void controller_should_not_depend_on_entity() {
    classes()
        .that()
        .resideInAPackage("..controller..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..entity..")
        .allowEmptyShould(true)
        .check(classes);
  }

  // ==================== Service 层约束 ====================

  @Test
  @DisplayName("Service 不应依赖 Controller")
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

  @Test
  @DisplayName("Service 不应依赖 Mapper")
  void service_should_not_depend_on_mapper() {
    classes()
        .that()
        .resideInAPackage("..service..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..mapper..")
        .allowEmptyShould(true)
        .check(classes);
  }

  // ==================== DAO 层约束 ====================

  @Test
  @DisplayName("DAO 不应依赖 Controller")
  void dao_should_not_depend_on_controller() {
    classes()
        .that()
        .resideInAPackage("..dao..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..controller..")
        .allowEmptyShould(true)
        .check(classes);
  }

  // ==================== Entity 层约束 ====================

  @Test
  @DisplayName("Entity 不应依赖 Service")
  void entity_should_not_depend_on_service() {
    classes()
        .that()
        .resideInAPackage("..entity..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..service..")
        .allowEmptyShould(true)
        .check(classes);
  }

  // ==================== DTO / VO 层约束 ====================

  @Test
  @DisplayName("DTO/VO 不应依赖 Service")
  void dto_should_not_depend_on_service() {
    classes()
        .that()
        .resideInAnyPackage("..dto..", "..vo..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..service..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("DTO/VO 不应依赖 DAO")
  void dto_should_not_depend_on_dao() {
    classes()
        .that()
        .resideInAnyPackage("..dto..", "..vo..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..dao..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("DTO/VO 不应依赖 Mapper")
  void dto_should_not_depend_on_mapper() {
    classes()
        .that()
        .resideInAnyPackage("..dto..", "..vo..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..mapper..")
        .allowEmptyShould(true)
        .check(classes);
  }
}
