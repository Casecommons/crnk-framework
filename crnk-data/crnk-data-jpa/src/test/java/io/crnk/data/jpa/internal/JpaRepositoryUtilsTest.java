package io.crnk.data.jpa.internal;

import org.junit.jupiter.api.Test;

import io.crnk.test.mock.ClassTestUtils;

public class JpaRepositoryUtilsTest {

	@Test
	public void hasPrivateConstructor() {
		ClassTestUtils.assertPrivateConstructor(JpaRepositoryUtils.class);
	}
}
