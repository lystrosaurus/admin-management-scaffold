package io.github.lystrosaurus.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/** 管理后台脚手架应用入口 */
@SpringBootApplication
@ComponentScan(basePackages = "io.github.lystrosaurus.admin")
public class AdminManagementScaffoldApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdminManagementScaffoldApplication.class, args);
  }
}
