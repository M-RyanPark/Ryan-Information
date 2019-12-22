/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.domain.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-23
 * description : 공통 응답 메세지 규격
 */
@Data
@AllArgsConstructor(staticName = "of")
public class CommonMessageResponse implements Serializable {
	private String message;
}
