/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.config;

import com.ryanpark.information.common.service.TokenManager;
import com.ryanpark.information.framework.filter.RefreshBearerTokenProcessingFilter;
import com.ryanpark.information.framework.oauth.RefreshBearerTokenExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : ResourceServer Config. /api/** 로 요청 오는 자원에 대한 권한 설정
 */
@Configuration
@EnableResourceServer
@RequiredArgsConstructor
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "information-api";

	final TokenStore tokenStore;
	final TokenManager tokenManager;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources
				.resourceId(RESOURCE_ID)
				.tokenExtractor(new RefreshBearerTokenExtractor())
				.tokenStore(tokenStore)
		;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.requestMatchers()
					.antMatchers("/api/**")
				.and()
					.authorizeRequests()
					.anyRequest()
					.authenticated()
				.and()
					.addFilterAfter(new RefreshBearerTokenProcessingFilter(tokenManager), AbstractPreAuthenticatedProcessingFilter.class)
				;

	}
}
