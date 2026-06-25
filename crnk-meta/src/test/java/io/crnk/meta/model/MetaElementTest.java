package io.crnk.meta.model;

import io.crnk.meta.AbstractMetaTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MetaElementTest extends AbstractMetaTest {


	@Test
	public void checkDataObjectCast() {
		assertThrows(IllegalStateException.class, () -> {
			new MetaKey().asDataObject();
		});
	}

	@Test
	public void checkTypeCast() {
		assertThrows(IllegalStateException.class, () -> {
			new MetaKey().asType();
		});
	}
}
