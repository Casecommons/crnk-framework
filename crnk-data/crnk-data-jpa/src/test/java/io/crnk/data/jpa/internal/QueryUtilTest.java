package io.crnk.data.jpa.internal;

import org.junit.jupiter.api.Test;

import io.crnk.data.jpa.internal.query.QueryUtil;
import io.crnk.test.mock.ClassTestUtils;

public class QueryUtilTest {

	@Test
	public void hasPrivateConstructor() {
		ClassTestUtils.assertPrivateConstructor(QueryUtil.class);
	}
}
