/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.business.finance.repsitory.entity;

import com.ryanpark.information.common.repository.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-22
 * description : 금융 기관 별 지원 금액 Entity
 */
@Entity
@Table(
		name = "tb_bank_support"
		, indexes = @Index(name = "bank_support_time", columnList = "year,month,code", unique = true)
)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BankSupportEntity extends BaseEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer supportId;

	@ManyToOne(targetEntity = BankEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "code")
	private BankEntity bank;

	@Column(length = 4, nullable = false)
	private Integer year;

	@Column(length = 2, nullable = false)
	private Integer month;

	@Column(nullable = false)
	private Integer amount;
}
