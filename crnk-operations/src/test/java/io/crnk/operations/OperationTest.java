package io.crnk.operations;

import io.crnk.core.engine.document.Resource;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OperationTest {


	@Test
	public void testEquals() {
		EqualsVerifier.forClass(Operation.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS)
				.withPrefabValues(Resource.class, resource("a"), resource("b"))
				.verify();

	}

	private static Resource resource(String id) {
		Resource resource = new Resource();
		resource.setId(id);
		resource.setType("type");
		return resource;
	}

	@Test
	public void testHashCode() {
		Operation op1 = new Operation("a", "b", new Resource());
		Operation op2 = new Operation("a", "b", new Resource());
		Operation op3 = new Operation("x", "b", new Resource());
		Assertions.assertEquals(op1, op2);
		Assertions.assertNotEquals(op3.hashCode(), op2.hashCode());
	}

}
