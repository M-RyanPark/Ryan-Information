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
import com.ryanpark.information.common.service.TokenManager;
import com.ryanpark.information.framework.exception.BadRequestException;
import com.ryanpark.information.framework.exception.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

	final AccountService accountService;
	final TokenManager tokenManager;

	@Override
	@Transactional
	public SignUpResponse signUp(SignUpRequest signUpRequest) {
		if (isInvalidPasswordRequest(signUpRequest)) {
			throw BadRequestException.of("비밀 번호가 일치 하지 않습니다.");
		}

		Account newAccount = accountService.createAccount(signUpRequest.getUserId(), signUpRequest.getPassword());

		OAuth2AccessToken oAuth2AccessToken = tokenManager.createAccessToken(newAccount.getUserId(), signUpRequest.getPassword())
				.orElseThrow(() -> BusinessValidationException.of("가입 처리 중 오류가 발생하였습니다."));

		return new SignUpResponse(oAuth2AccessToken);
	}

	private boolean isInvalidPasswordRequest(SignUpRequest signUpRequest) {
		return signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword()) == false;
	}

	@Override
	public SignInResponse signIn(SignInRequest signInRequest) {
		Account account = accountService.findAccount(signInRequest.getUserId())
				.orElseThrow(() -> BusinessValidationException.of("존재 하지 않는 회원입니다."));

		OAuth2AccessToken oAuth2AccessToken = tokenManager.createAccessToken(account.getUserId(), signInRequest.getPassword())
				.orElseThrow(() -> BusinessValidationException.of("로그인 처리 중 오류가 발생하였습니다."));

		return new SignInResponse(oAuth2AccessToken);
	}
}
