package io.crnk.client.suite;

import io.crnk.test.suite.RelationIdAccessTestBase;
import org.junit.jupiter.api.BeforeAll;

public class RelationIdClientTest extends RelationIdAccessTestBase {

	@BeforeAll
	public static void prepare() {
		ClientTestContainer.prepare();
	}

	public RelationIdClientTest() {
		ClientTestContainer testContainer = new ClientTestContainer();
		this.testContainer = testContainer;
	}
}
