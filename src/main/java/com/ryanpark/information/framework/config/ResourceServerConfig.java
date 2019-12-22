/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : ResourceServer Config. /api/** 로 요청 오는 자원에 대한 권한 설정
 */
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "information-api";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("information-api");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.requestMatchers()
					.antMatchers("/api/**")
				.and()
					.authorizeRequests()
					.anyRequest().authenticated()
//					.antMatchers("/api/**").hasAuthority("#oauth2.hasScope('read')")
//					.antMatchers("/admin/**").hasAuthority(Account.AccountAuthority.ADMIN.name())
				;

	}
}
