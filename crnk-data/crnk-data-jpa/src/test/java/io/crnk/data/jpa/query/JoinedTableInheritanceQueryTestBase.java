package io.crnk.data.jpa.query;

import org.junit.jupiter.api.Test;

import io.crnk.data.jpa.model.JoinedTableBaseEntity;
import io.crnk.data.jpa.model.JoinedTableChildEntity;

public abstract class JoinedTableInheritanceQueryTestBase
		extends AbstractInheritanceTest<JoinedTableBaseEntity, JoinedTableChildEntity> {

	public JoinedTableInheritanceQueryTestBase() {
		super(JoinedTableBaseEntity.class, JoinedTableChildEntity.class);
	}

	@Override
	@Test
	public void testOrderBySubtypeAttribute() {
		// NOTE those not work with JPA/Hibernate
		// so we do nothing here
	}
}
