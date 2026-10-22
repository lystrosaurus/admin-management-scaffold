package io.github.lystrosaurus.admin.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import io.github.lystrosaurus.admin.auth.annotation.PermitAll;
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

  // ==================== Organization 模块约束 ====================

  @Test
  @DisplayName("Organization Controller 不应依赖 DAO/Mapper/Entity")
  void organization_controller_should_not_depend_on_dao_mapper_entity() {
    classes()
        .that()
        .resideInAPackage("..organization..controller..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackages("..dao..", "..mapper..", "..entity..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("Organization Service 不应依赖 Mapper")
  void organization_service_should_not_depend_on_mapper() {
    classes()
        .that()
        .resideInAPackage("..organization..service..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..mapper..")
        .allowEmptyShould(true)
        .check(classes);
  }

  // ==================== Integration 模块约束 ====================

  @Test
  @DisplayName("Integration Controller 不应依赖 DAO/Mapper/Entity")
  void integration_controller_should_not_depend_on_dao_mapper_entity() {
    classes()
        .that()
        .resideInAPackage("..integration..controller..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackages("..dao..", "..mapper..", "..entity..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("Integration Service 不应依赖 Mapper")
  void integration_service_should_not_depend_on_mapper() {
    classes()
        .that()
        .resideInAPackage("..integration..service..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..mapper..")
        .allowEmptyShould(true)
        .check(classes);
  }

  // ==================== Auth 模块约束 ====================

  @Test
  @DisplayName("Auth Controller 不应依赖 DAO/Mapper/Entity")
  void auth_controller_should_not_depend_on_dao_mapper_entity() {
    classes()
        .that()
        .resideInAPackage("..auth..controller..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackages("..dao..", "..mapper..", "..entity..")
        .allowEmptyShould(true)
        .check(classes);
  }

  @Test
  @DisplayName("Auth Service 不应依赖 Mapper")
  void auth_service_should_not_depend_on_mapper() {
    classes()
        .that()
        .resideInAPackage("..auth..service..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..mapper..")
        .allowEmptyShould(true)
        .check(classes);
  }

  // ==================== Web 模块约束 ====================

  @Test
  @DisplayName("Web 包不应依赖 Mapper")
  void web_should_not_depend_on_mapper() {
    classes()
        .that()
        .resideInAPackage("..web..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("..mapper..")
        .allowEmptyShould(true)
        .check(classes);
  }

  // ==================== 授权回归约束（F-01 最小切片） ====================

  @Test
  @DisplayName("RoleController 的所有方法必须声明 @RequiresPermission")
  void role_controller_methods_must_declare_requires_permission() {
    methods()
        .that(
            new DescribedPredicate<JavaMethod>("属于 RoleController 的方法") {
              @Override
              public boolean test(JavaMethod method) {
                return method
                    .getOwner()
                    .getName()
                    .equals("io.github.lystrosaurus.admin.system.role.adapter.web.RoleController");
              }
            })
        .should(
            ArchConditions.beAnnotatedWith(
                io.github.lystrosaurus.admin.auth.annotation.RequiresPermission.class))
        .check(classes);
  }

  // ==================== 授权回归约束（本次铺开：User / Permission / Menu） ====================

  @Test
  @DisplayName("UserController 的所有方法必须声明 @RequiresPermission")
  void user_controller_methods_must_declare_requires_permission() {
    methods()
        .that(
            new DescribedPredicate<JavaMethod>("属于 UserController 的方法") {
              @Override
              public boolean test(JavaMethod method) {
                return method
                    .getOwner()
                    .getName()
                    .equals("io.github.lystrosaurus.admin.system.user.adapter.web.UserController");
              }
            })
        .should(
            ArchConditions.beAnnotatedWith(
                io.github.lystrosaurus.admin.auth.annotation.RequiresPermission.class))
        .check(classes);
  }

  @Test
  @DisplayName("PermissionController 的所有方法必须声明 @RequiresPermission")
  void permission_controller_methods_must_declare_requires_permission() {
    methods()
        .that(
            new DescribedPredicate<JavaMethod>("属于 PermissionController 的方法") {
              @Override
              public boolean test(JavaMethod method) {
                return method
                    .getOwner()
                    .getName()
                    .equals(
                        "io.github.lystrosaurus.admin.system.permission.adapter.web.PermissionController");
              }
            })
        .should(
            ArchConditions.beAnnotatedWith(
                io.github.lystrosaurus.admin.auth.annotation.RequiresPermission.class))
        .check(classes);
  }

  @Test
  @DisplayName("MenuController 的所有方法必须声明 @RequiresPermission 或 @PermitAll")
  void menu_controller_methods_must_declare_requires_permission_or_permit_all() {
    methods()
        .that(
            new DescribedPredicate<JavaMethod>("属于 MenuController 的方法") {
              @Override
              public boolean test(JavaMethod method) {
                return method
                    .getOwner()
                    .getName()
                    .equals("io.github.lystrosaurus.admin.system.menu.adapter.web.MenuController");
              }
            })
        .should(
            ArchConditions.beAnnotatedWith(
                    io.github.lystrosaurus.admin.auth.annotation.RequiresPermission.class)
                .or(
                    ArchConditions.beAnnotatedWith(
                        io.github.lystrosaurus.admin.auth.annotation.PermitAll.class)))
        .check(classes);
  }
}
