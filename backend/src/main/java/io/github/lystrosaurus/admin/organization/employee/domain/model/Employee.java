package io.github.lystrosaurus.admin.organization.employee.domain.model;

import io.github.lystrosaurus.admin.organization.employee.domain.event.EmployeeCreated;
import io.github.lystrosaurus.admin.organization.employee.domain.event.EmployeeUpdated;
import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 员工聚合根：守卫员工主数据的不变量，行为入口即业务规则落点。 */
public class Employee extends AggregateRoot<EmployeeId> {

  /** 员工工号 */
  private String employeeNo;

  /** 员工姓名 */
  private String name;

  /** 常用名/花名 */
  private String preferredName;

  /** 手机号 */
  private String mobile;

  /** 邮箱 */
  private String email;

  /** 主组织ID */
  private Long primaryOrgId;

  /** 关联系统用户ID（sys_user.id），可空 */
  private Long userId;

  /** 职位 */
  private String jobTitle;

  /** 在职状态(ACTIVE/RESIGNED/SUSPENDED) */
  private String employmentStatus;

  /** 入职日期 */
  private LocalDate entryDate;

  /** 离职日期 */
  private LocalDate leaveDate;

  /** 数据来源(MANUAL/IMPORT/API) */
  private String sourceType;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 版本号（乐观锁） */
  private Integer version;

  private Employee(EmployeeId id) {
    super(id);
  }

  /**
   * 创建新员工：来源默认 MANUAL，在职状态默认 ACTIVE，并登记 {@link EmployeeCreated} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param employeeNo 员工工号
   * @param name 员工姓名
   * @param mobile 手机号
   * @param email 邮箱
   * @param jobTitle 职位
   * @param primaryOrgId 主组织ID
   * @param entryDate 入职日期
   * @param userId 关联系统用户ID
   * @return 新员工聚合
   */
  public static Employee create(
      EmployeeId id,
      String employeeNo,
      String name,
      String mobile,
      String email,
      String jobTitle,
      Long primaryOrgId,
      LocalDate entryDate,
      Long userId) {
    Employee employee = new Employee(id);
    employee.employeeNo = employeeNo;
    employee.name = name;
    employee.mobile = mobile;
    employee.email = email;
    employee.jobTitle = jobTitle;
    employee.primaryOrgId = primaryOrgId;
    employee.entryDate = entryDate;
    employee.userId = userId;
    employee.employmentStatus = "ACTIVE";
    employee.sourceType = "MANUAL";
    employee.registerEvent(EmployeeCreated.of(id.value(), employeeNo));
    return employee;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 员工聚合
   */
  public static Employee restore(
      EmployeeId id,
      String employeeNo,
      String name,
      String preferredName,
      String mobile,
      String email,
      Long primaryOrgId,
      Long userId,
      String jobTitle,
      String employmentStatus,
      LocalDate entryDate,
      LocalDate leaveDate,
      String sourceType,
      LocalDateTime createdAt,
      Integer version) {
    Employee employee = new Employee(id);
    employee.employeeNo = employeeNo;
    employee.name = name;
    employee.preferredName = preferredName;
    employee.mobile = mobile;
    employee.email = email;
    employee.primaryOrgId = primaryOrgId;
    employee.userId = userId;
    employee.jobTitle = jobTitle;
    employee.employmentStatus = employmentStatus;
    employee.entryDate = entryDate;
    employee.leaveDate = leaveDate;
    employee.sourceType = sourceType;
    employee.createdAt = createdAt;
    employee.version = version;
    return employee;
  }

  /** 更新员工资料：仅更新非空文本字段与非空字段，登记 {@link EmployeeUpdated} 事件。 */
  public void update(
      String name,
      String preferredName,
      String mobile,
      String email,
      String jobTitle,
      Long primaryOrgId,
      String employmentStatus,
      LocalDate entryDate,
      LocalDate leaveDate,
      Long userId) {
    if (hasText(name)) {
      this.name = name;
    }
    if (preferredName != null) {
      this.preferredName = preferredName;
    }
    if (hasText(mobile)) {
      this.mobile = mobile;
    }
    if (email != null) {
      this.email = email;
    }
    if (hasText(jobTitle)) {
      this.jobTitle = jobTitle;
    }
    if (primaryOrgId != null) {
      this.primaryOrgId = primaryOrgId;
    }
    if (hasText(employmentStatus)) {
      this.employmentStatus = employmentStatus;
    }
    if (entryDate != null) {
      this.entryDate = entryDate;
    }
    if (leaveDate != null) {
      this.leaveDate = leaveDate;
    }
    if (userId != null) {
      this.userId = userId;
    }
    registerEvent(EmployeeUpdated.of(id.value()));
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public String employeeNo() {
    return employeeNo;
  }

  public String name() {
    return name;
  }

  public String preferredName() {
    return preferredName;
  }

  public String mobile() {
    return mobile;
  }

  public String email() {
    return email;
  }

  public Long primaryOrgId() {
    return primaryOrgId;
  }

  public Long userId() {
    return userId;
  }

  public String jobTitle() {
    return jobTitle;
  }

  public String employmentStatus() {
    return employmentStatus;
  }

  public LocalDate entryDate() {
    return entryDate;
  }

  public LocalDate leaveDate() {
    return leaveDate;
  }

  public String sourceType() {
    return sourceType;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Integer version() {
    return version;
  }
}
