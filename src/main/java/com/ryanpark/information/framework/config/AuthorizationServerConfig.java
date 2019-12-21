/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-17
 * description : oauth2 Authorization server config
 */
@EnableAuthorizationServer
@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	final UserDetailsService userDetailsService;
	final DataSource dataSource;
	final ClientDetailsService clientDetailsService;


	AuthorizationServerTokenServices tokenService;

	// Rest Api Token Granter 를 생성 하기 위해 AuthorizationServerTokenService가 필요한데
	// autowired candidate가 여러 개 존재하기 때문에 AuthorizationServerEndpointsConfiguration에서 가져오도록 명시한다
	@Autowired
	@Qualifier("defaultAuthorizationServerTokenServices")
	public void setTokenService(AuthorizationServerTokenServices tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
				.jdbc(dataSource);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				.authorizationCodeServices(new JdbcAuthorizationCodeServices(dataSource))
				.userDetailsService(userDetailsService)
				.accessTokenConverter(accessTokenConverter())
				.tokenStore(tokenStore());
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		KeyStoreKeyFactory keyStoreKeyFactory =
				new KeyStoreKeyFactory(new ClassPathResource("information.jks"), "testpass".toCharArray());

		converter.setKeyPair(keyStoreKeyFactory.getKeyPair("infomation", "testpass".toCharArray()));

		return converter;
	}

	// Rest Api 가입 / 로그인 처리시 사용할 TokenGranter
	@Bean("restApiTokenGranter")
	public TokenGranter restApiTokenGranter() {
		return new RestApiTokenGranter(tokenService, clientDetailsService);
	}

	static class RestApiTokenGranter extends AbstractTokenGranter {
		private static final String REST_API_GRANT_TYPE = "rest_api";

		RestApiTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService) {
			super(tokenServices, clientDetailsService, new DefaultOAuth2RequestFactory(clientDetailsService), REST_API_GRANT_TYPE);
		}
	}
}
