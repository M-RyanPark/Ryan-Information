/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.repository.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description : 모든 Entity의 기본이 될 추상 클래스
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
	@CreatedDate
	@Column
	private LocalDateTime createDateTime;

	@LastModifiedDate
	@Column
	private LocalDateTime modifyDateTime;
}
