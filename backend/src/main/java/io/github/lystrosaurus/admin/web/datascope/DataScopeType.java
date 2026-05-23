package io.github.lystrosaurus.admin.web.datascope;

/** 数据权限范围类型 */
public enum DataScopeType {

  /** 全部数据 */
  ALL,

  /** 本部门及下级部门 */
  ORG_TREE,

  /** 仅本部门 */
  ORG_ONLY,

  /** 仅本人 */
  SELF,

  /** 自定义部门 */
  CUSTOM
}
