package io.crnk.client.suite;

import io.crnk.test.suite.RelationIdAccessTestBase;
import org.junit.BeforeClass;

public class RelationIdClientTest extends RelationIdAccessTestBase {

	@BeforeClass
	public static void prepare() {
		ClientTestContainer.prepare();
	}

	public RelationIdClientTest() {
		ClientTestContainer testContainer = new ClientTestContainer();
		this.testContainer = testContainer;
	}
}
