package io.crnk.core.engine.document;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class ResourceTest {

	@Test
	public void testResourceEqualsContract() {
		EqualsVerifier.forClass(Resource.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS)
				.withPrefabValues(tools.jackson.databind.node.ObjectNode.class,
						tools.jackson.databind.node.JsonNodeFactory.instance.objectNode(),
						tools.jackson.databind.node.JsonNodeFactory.instance.objectNode().put("a", "b"))
				.withPrefabValues(tools.jackson.databind.JsonNode.class,
						tools.jackson.databind.node.JsonNodeFactory.instance.objectNode(),
						tools.jackson.databind.node.JsonNodeFactory.instance.objectNode().put("a", "b"))
				.verify();
	}
}
