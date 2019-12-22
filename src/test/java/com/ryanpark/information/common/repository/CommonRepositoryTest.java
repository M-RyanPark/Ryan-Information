/*
 * Copyright (c) 2019 Ryan Information Test
 */

package com.ryanpark.information.common.repository;

import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author : Sanghyun Ryan Park (sanghyun.ryan.park@gmail.com)
 * @since : 2019-12-19
 * description :
 */
@DataJpaTest
public class CommonRepositoryTest {
	@PersistenceContext
	protected EntityManager entityManager;

	protected void expect_ConstraintViolationException(Executable executable) {
		Exception exception = assertThrows(DataIntegrityViolationException.class, executable);

		assertTrue(exception.getClass().isAssignableFrom(DataIntegrityViolationException.class));
	}
}
