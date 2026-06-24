package io.crnk.client.suite;

import io.crnk.test.suite.InformationAccessTestBase;
import org.junit.BeforeClass;

public class InformationClientTest extends InformationAccessTestBase {

	@BeforeClass
	public static void prepare() {
		ClientTestContainer.prepare();
	}

	public InformationClientTest() {
		ClientTestContainer testContainer = new ClientTestContainer();
		this.testContainer = testContainer;
	}

}