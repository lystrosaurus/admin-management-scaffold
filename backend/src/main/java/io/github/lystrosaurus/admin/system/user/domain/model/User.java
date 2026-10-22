package io.github.lystrosaurus.admin.system.user.domain.model;

import io.github.lystrosaurus.admin.shared.domain.primitive.AggregateRoot;
import io.github.lystrosaurus.admin.system.user.domain.event.UserDisabled;
import io.github.lystrosaurus.admin.system.user.domain.event.UserEmployeeBound;
import io.github.lystrosaurus.admin.system.user.domain.event.UserEmployeeUnbound;
import io.github.lystrosaurus.admin.system.user.domain.event.UserEnabled;
import io.github.lystrosaurus.admin.system.user.domain.event.UserPasswordChanged;
import io.github.lystrosaurus.admin.system.user.domain.event.UserRegistered;
import io.github.lystrosaurus.admin.system.user.domain.event.UserUpdated;
import java.time.LocalDateTime;
import java.util.Objects;

/** 用户聚合根：守卫用户账号的不变量，行为入口即业务规则落点。 */
public class User extends AggregateRoot<UserId> {

  /** 用户名 */
  private String username;

  /** 密码哈希 */
  private String passwordHash;

  /** 昵称 */
  private String nickname;

  /** 头像文件ID */
  private Long avatarFileId;

  /** 手机号 */
  private PhoneNumber phone;

  /** 邮箱 */
  private Email email;

  /** 绑定员工ID */
  private Long employeeId;

  /** 状态 */
  private UserStatus status;

  /** token版本号 */
  private Integer tokenVersion;

  /** 最后登录时间 */
  private LocalDateTime lastLoginAt;

  /** 最后登录IP */
  private String lastLoginIp;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 版本号（乐观锁） */
  private Integer version;

  private User(UserId id) {
    super(id);
  }

  /**
   * 注册新用户：初始状态 ENABLED、tokenVersion=1，并登记 {@link UserRegistered} 事件。
   *
   * @param id 由应用层通过 IdGenerator 预生成的标识
   * @param username 用户名
   * @param passwordHash 密码哈希
   * @param nickname 昵称
   * @param phone 手机号（可空）
   * @param email 邮箱（可空）
   * @return 新用户聚合
   */
  public static User register(
      UserId id, String username, String passwordHash, String nickname, String phone, String email) {
    User user = new User(id);
    user.username = username;
    user.passwordHash = passwordHash;
    user.nickname = nickname;
    user.phone = phone == null ? null : new PhoneNumber(phone);
    user.email = email == null ? null : new Email(email);
    user.status = UserStatus.ENABLED;
    user.tokenVersion = 1;
    user.registerEvent(UserRegistered.of(id.value(), username));
    return user;
  }

  /**
   * 从持久化数据重建聚合（不登记事件）。
   *
   * @return 用户聚合
   */
  public static User restore(
      UserId id,
      String username,
      String passwordHash,
      String nickname,
      Long avatarFileId,
      String phone,
      String email,
      Long employeeId,
      UserStatus status,
      Integer tokenVersion,
      LocalDateTime lastLoginAt,
      String lastLoginIp,
      LocalDateTime createdAt,
      Integer version) {
    User user = new User(id);
    user.username = username;
    user.passwordHash = passwordHash;
    user.nickname = nickname;
    user.avatarFileId = avatarFileId;
    user.phone = phone == null ? null : new PhoneNumber(phone);
    user.email = email == null ? null : new Email(email);
    user.employeeId = employeeId;
    user.status = status;
    user.tokenVersion = tokenVersion;
    user.lastLoginAt = lastLoginAt;
    user.lastLoginIp = lastLoginIp;
    user.createdAt = createdAt;
    user.version = version;
    return user;
  }

  /** 更新资料：仅更新非空文本字段，登记 {@link UserUpdated} 事件。 */
  public void updateProfile(String nickname, String phone, String email) {
    if (hasText(nickname)) {
      this.nickname = nickname;
    }
    if (hasText(phone)) {
      this.phone = new PhoneNumber(phone);
    }
    if (hasText(email)) {
      this.email = new Email(email);
    }
    registerEvent(UserUpdated.of(id.value()));
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  /** 修改密码（不递增 tokenVersion），登记 {@link UserPasswordChanged} 事件。 */
  public void changePassword(String newPasswordHash) {
    this.passwordHash = newPasswordHash;
    registerEvent(UserPasswordChanged.of(id.value()));
  }

  /** 禁用用户；已禁用时幂等，不重复登记事件。 */
  public void disable() {
    if (this.status == UserStatus.DISABLED) {
      return;
    }
    this.status = UserStatus.DISABLED;
    registerEvent(UserDisabled.of(id.value()));
  }

  /** 启用用户，登记 {@link UserEnabled} 事件。 */
  public void enable() {
    if (this.status == UserStatus.ENABLED) {
      return;
    }
    this.status = UserStatus.ENABLED;
    registerEvent(UserEnabled.of(id.value()));
  }

  /** 绑定员工；绑定值相同时幂等，不重复登记事件。 */
  public void bindEmployee(Long employeeId) {
    if (Objects.equals(this.employeeId, employeeId)) {
      return;
    }
    this.employeeId = employeeId;
    registerEvent(UserEmployeeBound.of(id.value(), employeeId));
  }

  /** 解绑员工；原本未绑定时不登记事件。 */
  public void unbindEmployee() {
    if (this.employeeId == null) {
      return;
    }
    Long previous = this.employeeId;
    this.employeeId = null;
    registerEvent(UserEmployeeUnbound.of(id.value(), previous));
  }

  /** 记录登录时间与 IP（登录事实，不登记事件）。 */
  public void recordLogin(LocalDateTime at, String ip) {
    this.lastLoginAt = at;
    this.lastLoginIp = ip;
  }

  /** 迁移状态（管理端直接指定，含 LOCKED），登记 {@link UserUpdated} 事件。 */
  public void changeStatus(UserStatus newStatus) {
    this.status = newStatus;
    registerEvent(UserUpdated.of(id.value()));
  }

  public String username() {
    return username;
  }

  public String passwordHash() {
    return passwordHash;
  }

  public String nickname() {
    return nickname;
  }

  public Long avatarFileId() {
    return avatarFileId;
  }

  public PhoneNumber phone() {
    return phone;
  }

  public Email email() {
    return email;
  }

  public Long employeeId() {
    return employeeId;
  }

  public UserStatus status() {
    return status;
  }

  public Integer tokenVersion() {
    return tokenVersion;
  }

  public LocalDateTime lastLoginAt() {
    return lastLoginAt;
  }

  public String lastLoginIp() {
    return lastLoginIp;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Integer version() {
    return version;
  }
}
