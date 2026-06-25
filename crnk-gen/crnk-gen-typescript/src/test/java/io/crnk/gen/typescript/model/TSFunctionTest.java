package io.crnk.gen.typescript.model;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class TSFunctionTest {

	@Test
	public void notAField() {
		TSFunction function = new TSFunction();
		Assertions.assertFalse(function.isField());
	}

	@Test
	public void cannotCastToField() {
		assertThrows(UnsupportedOperationException.class, () -> {
			TSFunction function = new TSFunction();
			function.asField();
		});
	}
}
