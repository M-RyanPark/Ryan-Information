/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.service.impl;

import com.ryanpark.information.common.domain.api.SignInRequest;
import com.ryanpark.information.common.domain.api.SignInResponse;
import com.ryanpark.information.common.domain.api.SignUpRequest;
import com.ryanpark.information.common.domain.api.SignUpResponse;
import com.ryanpark.information.common.repository.entity.Account;
import com.ryanpark.information.common.service.AccountService;
import com.ryanpark.information.common.service.SignService;
import com.ryanpark.information.framework.exception.BadRequestException;
import com.ryanpark.information.framework.exception.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : 가입 및 로그인을 위한 인증 서비스
 * Rest Api 를 통하여 가입 및 로그인 을 처리하므로 별도의 TokenGranter를 정의하여 token을 발급
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SignServiceImpl implements SignService {

	private static final String REST_API_GRANT_TYPE = "rest_api";
	private static final String OAUTH_CLIENT_ID = "loan_client";

	final private AccountService accountService;
	final private TokenGranter tokenGranter;

	@Override
	@Transactional
	public SignUpResponse signUp(SignUpRequest signUpRequest) {
		if (isInvalidPasswordRequest(signUpRequest)) {
			throw BadRequestException.of("비밀 번호가 일치 하지 않습니다.");
		}

		Account newAccount = accountService.createAccount(signUpRequest.getUserId(), signUpRequest.getPassword());

		OAuth2AccessToken oAuth2AccessToken = createToken(newAccount.getUserId(), signUpRequest.getPassword())
				.orElseThrow(() -> BadRequestException.of("가입 처리 중 오류가 발생하였습니다."));

		return new SignUpResponse(oAuth2AccessToken);
	}

	private boolean isInvalidPasswordRequest(SignUpRequest signUpRequest) {
		return signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword()) == false;
	}

	@Override
	public SignInResponse signIn(SignInRequest signInRequest) {
		Account account = accountService.findAccount(signInRequest.getUserId())
				.orElseThrow(() -> BusinessValidationException.of("존재 하지 않는 회원입니다."));

		OAuth2AccessToken oAuth2AccessToken = createToken(account.getUserId(), account.getPassword())
				.orElseThrow(() -> BadRequestException.of("로그인 처리 중 오류가 발생하였습니다."));

		return new SignInResponse(oAuth2AccessToken);
	}

	private Optional<OAuth2AccessToken> createToken(@NotNull String userId, @NotNull String password) {
		try {
			Map<String, String> oauthParam = new HashMap<>();
			oauthParam.put("client_id", OAUTH_CLIENT_ID);
			// todo 계정 권한을 추가하고 권한과 연동 할것인가 ?
			oauthParam.put("scope", "read,write");
			oauthParam.put("username", userId);
			oauthParam.put("password", password);

			TokenRequest tokenRequest = new TokenRequest(
					oauthParam
					, OAUTH_CLIENT_ID,
					Collections.singletonList("read"), REST_API_GRANT_TYPE
			);

			return Optional.ofNullable(tokenGranter.grant(REST_API_GRANT_TYPE, tokenRequest));
		} catch (Exception e) {
			log.error("Access Token 생성 중 에러 발생 : {}", e.getMessage(), e);
			return Optional.empty();
		}
	}
}
