package io.crnk.client.suite;

import io.crnk.test.suite.InformationAccessTestBase;
import org.junit.jupiter.api.BeforeAll;

public class InformationClientTest extends InformationAccessTestBase {

	@BeforeAll
	public static void prepare() {
		ClientTestContainer.prepare();
	}

	public InformationClientTest() {
		ClientTestContainer testContainer = new ClientTestContainer();
		this.testContainer = testContainer;
	}

}