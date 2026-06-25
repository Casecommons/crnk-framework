package io.crnk.core.repository;

import org.junit.jupiter.api.Test;

import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadOnlyResourceRepositoryBaseTest {

	private ReadOnlyResourceRepositoryBase repo = new ReadOnlyResourceRepositoryBase(null) {
		@Override
		public ResourceList findAll(QuerySpec querySpec) {
			return null;
		}
	};

	@Test
	public void save() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.save(null);
	    });
	}

	@Test
	public void create() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.create(null);
	    });
	}

	@Test
	public void delete() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.delete(null);
	    });
	}
}
