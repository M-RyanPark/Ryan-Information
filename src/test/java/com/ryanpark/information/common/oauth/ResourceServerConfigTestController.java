/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.oauth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : ResourceServer 설정 테스트를 위한 컨트롤러
 */
@RestController
public class ResourceServerConfigTestController {

	@GetMapping(value = "/api/test")
	@PreAuthorize("#oauth2.hasScope('write') && hasAnyAuthority('USER', 'ADMIN')")
	public String userRoleAuthorizedTestPath() {
		return "ok";
	}

	@GetMapping(value = "/admin/test")
	@PreAuthorize("#oauth2.hasAnyScope('write', 'read') && hasAuthority('ADMIN')")
	public String adminRoleAuthorizedTestPath() {
		return "ok";
	}
}
