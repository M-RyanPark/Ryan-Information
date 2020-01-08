/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.framework.config;

import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.framework.oauth.AccountUserNoAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

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
	final AuthenticationManager authenticationManager;
	final AccountService accountService;

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
				.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService)
				.accessTokenConverter(accessTokenConverter())
				.tokenStore(tokenStore())
//				.exceptionTranslator()	// todo Oauth2Exception 의 translator 와 JsonDeserialize 를 Customize 하여 error response 규격을 통일 하자
		;
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
		converter.setAccessTokenConverter(defaultAccessTokenConverter());

		return converter;
	}

	public DefaultAccessTokenConverter defaultAccessTokenConverter() {
		DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter() {
			@Override
			public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
				OAuth2Authentication authentication = super.extractAuthentication(map);
				return Objects.isNull(authentication.getUserAuthentication()) ? null : authentication;
			}
		};
		accessTokenConverter.setUserTokenConverter(
				AccountUserNoAuthenticationConverter.of(userDetailsService, accountService)
		);

		return accessTokenConverter;
	}

	// Rest Api 가입 / 로그인 처리시 사용할 TokenGranter
	@Bean("restApiTokenGranter")
	public TokenGranter restApiTokenGranter() {
		return new RestApiTokenGranter(authenticationManager, tokenService, clientDetailsService);
	}

	public static class RestApiTokenGranter extends ResourceOwnerPasswordTokenGranter {
		private static final String REST_API_GRANT_TYPE = "rest_api";

		RestApiTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService) {
			super(authenticationManager, tokenServices, clientDetailsService, new DefaultOAuth2RequestFactory(clientDetailsService), REST_API_GRANT_TYPE);
		}
	}
}
