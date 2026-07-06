package io.crnk.core.repository;

import org.junit.jupiter.api.Test;

import io.crnk.core.exception.MethodNotAllowedException;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadOnlyRelationshipRepositoryBaseTest {


	private ReadOnlyRelationshipRepositoryBase repo = new ReadOnlyRelationshipRepositoryBase() {

	};

	@Test
	public void getSourceResourceClass() {
	    assertThrows(UnsupportedOperationException.class, () -> {
    		repo.getSourceResourceClass();
	    });
	}

	@Test
	public void getMatcher() {
	    assertThrows(UnsupportedOperationException.class, () -> {
    		repo.getMatcher();
	    });
	}

	@Test
	public void getTargetResourceClass() {
	    assertThrows(UnsupportedOperationException.class, () -> {
    		repo.getTargetResourceClass();
	    });
	}

	@Test
	public void findOneTarget() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.findOneTarget(null, null, null);
	    });
	}

	@Test
	public void findManyTargets() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.findManyTargets(null, null, null);
	    });
	}

	@Test
	public void setRelation() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.setRelation(null, null, null);
	    });
	}

	@Test
	public void setRelations() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.setRelations(null, null, null);
	    });
	}

	@Test
	public void addRelations() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.addRelations(null, null, null);
	    });
	}

	@Test
	public void removeRelations() {
	    assertThrows(MethodNotAllowedException.class, () -> {
    		repo.removeRelations(null, null, null);
	    });
	}
}
