package io.github.lystrosaurus.admin.auth.vo;

import io.github.lystrosaurus.admin.auth.external.vo.ExternalAccountVO;
import io.github.lystrosaurus.admin.auth.log.vo.LoginLogVO;
import java.util.List;

/**
 * 账号安全信息 VO
 *
 * @param hasPassword 是否设置了密码
 * @param boundAccounts 已绑定的三方账号列表
 * @param recentLogins 最近登录记录
 */
public record SecurityProfileVO(
    boolean hasPassword, List<ExternalAccountVO> boundAccounts, List<LoginLogVO> recentLogins) {}
