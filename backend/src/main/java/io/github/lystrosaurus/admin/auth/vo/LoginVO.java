package io.github.lystrosaurus.admin.auth.vo;

import io.github.lystrosaurus.admin.system.user.vo.UserVO;

/**
 * 登录VO
 *
 * @param accessToken 访问令牌
 * @param user 用户信息
 */
public record LoginVO(String accessToken, UserVO user) {}
