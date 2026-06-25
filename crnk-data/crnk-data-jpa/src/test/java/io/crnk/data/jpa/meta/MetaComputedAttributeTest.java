package io.crnk.data.jpa.meta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.crnk.data.jpa.internal.query.MetaComputedAttribute;

public class MetaComputedAttributeTest {


	@Test
	public void getValueNotSupported() {
		assertThrows(UnsupportedOperationException.class, () -> {
			MetaComputedAttribute attr = new MetaComputedAttribute();
			attr.getValue(null);
		});
	}

	@Test
	public void setValueNotSupported() {
		assertThrows(UnsupportedOperationException.class, () -> {
			MetaComputedAttribute attr = new MetaComputedAttribute();
			attr.setValue(null, null);
		});
	}
}
