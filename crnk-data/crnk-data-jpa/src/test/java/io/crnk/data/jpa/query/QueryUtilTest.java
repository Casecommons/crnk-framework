package io.crnk.data.jpa.query;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import io.crnk.core.engine.internal.utils.PreconditionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class QueryUtilTest {

	@Test
	public void testConstructorIsPrivate()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<PreconditionUtil> constructor = PreconditionUtil.class.getDeclaredConstructor();
		Assertions.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

}
