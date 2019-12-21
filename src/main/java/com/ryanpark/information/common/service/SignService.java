/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.service;

import com.ryanpark.information.common.domain.api.SignInRequest;
import com.ryanpark.information.common.domain.api.SignInResponse;
import com.ryanpark.information.common.domain.api.SignUpRequest;
import com.ryanpark.information.common.domain.api.SignUpResponse;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-21
 * description : 가입 및 로그인을 위한 인증 서비스
 */
public interface SignService {
	SignUpResponse signUp(SignUpRequest signUpRequest);
	SignInResponse signIn(SignInRequest signInRequest);
}
