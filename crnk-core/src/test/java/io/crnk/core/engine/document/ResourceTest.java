package io.crnk.core.engine.document;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class ResourceTest {

	@Test
	public void testResourceEqualsContract() {
		EqualsVerifier.forClass(Resource.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS)
				.withPrefabValues(com.fasterxml.jackson.databind.node.ObjectNode.class,
						com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode(),
						com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode().put("a", "b"))
				.withPrefabValues(com.fasterxml.jackson.databind.JsonNode.class,
						com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode(),
						com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode().put("a", "b"))
				.verify();
	}
}
