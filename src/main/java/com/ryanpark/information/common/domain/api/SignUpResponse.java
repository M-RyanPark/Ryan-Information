/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.domain.api;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : 가입 응답
 */
public class SignUpResponse extends DefaultOAuth2AccessToken {
	public SignUpResponse(OAuth2AccessToken oAuth2AccessToken) {
		super(oAuth2AccessToken);
	}
}
