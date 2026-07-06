package io.crnk.data.jpa.query;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.querydsl.core.types.Expression;
import io.crnk.data.jpa.internal.query.backend.querydsl.QuerydslObjectArrayTupleImpl;

import jakarta.persistence.TupleElement;

public class ObjectArrayTupleImplTest {

	private QuerydslObjectArrayTupleImpl impl = new QuerydslObjectArrayTupleImpl(new Object[]{"0", "1"}, null);

	@Test
	public void testGetByExpressionNotSupported() {
		assertThrows(UnsupportedOperationException.class, () -> {
			impl.get((Expression<?>) null);
		});
	}

	@Test
	public void testGetByTupleNotSupported() {
		assertThrows(UnsupportedOperationException.class, () -> {
			impl.get((TupleElement<?>) null);
		});
	}

	@Test
	public void testGetByNameNotSupported() {
		assertThrows(UnsupportedOperationException.class, () -> {
			impl.get((String) null);
		});
	}

	@Test
	public void testGetElementsNotSupported() {
		assertThrows(UnsupportedOperationException.class, () -> {
			impl.getElements();
		});
	}

	@Test
	public void testReduce() {
		Assertions.assertEquals(2, impl.size());
		Assertions.assertEquals(2, impl.size());
		Assertions.assertArrayEquals(new Object[]{"0", "1"}, impl.toArray());
		impl.reduce(1);
		Assertions.assertEquals("1", impl.get(0, String.class));
		Assertions.assertEquals(1, impl.size());
		Assertions.assertArrayEquals(new Object[]{"1"}, impl.toArray());
	}
}
