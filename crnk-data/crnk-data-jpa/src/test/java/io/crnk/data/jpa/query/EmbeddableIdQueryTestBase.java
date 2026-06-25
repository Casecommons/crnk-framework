package io.crnk.data.jpa.query;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.crnk.data.jpa.model.TestEmbeddedIdEntity;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public abstract class EmbeddableIdQueryTestBase extends AbstractJpaTest {

	private JpaQuery<TestEmbeddedIdEntity> builder() {
		return queryFactory.query(TestEmbeddedIdEntity.class);
	}

	@Test
	public void testAll() {
		assertEquals(5, builder().buildExecutor().getResultList().size());
	}

}
