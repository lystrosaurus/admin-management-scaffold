package io.github.lystrosaurus.admin.shared.domain.acl;

/**
 * 分页查询参数。
 *
 * @param pageNum 页码（从 1 开始）
 * @param pageSize 每页大小（1-200）
 */
public record PageQuery(int pageNum, int pageSize) {

  public PageQuery {
    if (pageNum < 1) {
      throw new IllegalArgumentException("pageNum 必须 >= 1");
    }
    if (pageSize < 1 || pageSize > 200) {
      throw new IllegalArgumentException("pageSize 必须在 1-200 之间");
    }
  }

  public static PageQuery of(int pageNum, int pageSize) {
    return new PageQuery(pageNum, pageSize);
  }
}
