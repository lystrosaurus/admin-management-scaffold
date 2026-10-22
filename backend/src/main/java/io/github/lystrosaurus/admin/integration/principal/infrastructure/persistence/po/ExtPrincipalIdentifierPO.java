package io.github.lystrosaurus.admin.integration.principal.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.lystrosaurus.admin.shared.infrastructure.persistence.BasePO;
import lombok.Getter;
import lombok.Setter;

/** 外部主体标识符持久化对象（ext_principal_identifier）。 */
@Getter
@Setter
@TableName("ext_principal_identifier")
public class ExtPrincipalIdentifierPO extends BasePO {

  /** 外部主体ID */
  private Long principalId;

  /** ID类型 */
  private String idType;

  /** ID值 */
  private String idValue;

  /** 是否主ID(0-否，1-是) */
  private Integer isPrimary;
}
