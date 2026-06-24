package io.crnk.core.module;

import io.crnk.core.engine.registry.ResourceRegistryAware;
import io.crnk.core.mock.models.Schedule;
import io.crnk.core.mock.models.Task;
import io.crnk.core.mock.repository.ScheduleRepository;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.RelationshipRepository;
import io.crnk.core.repository.decorate.WrappedRelationshipRepository;
import io.crnk.core.repository.decorate.WrappedResourceRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DecoratorTest {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testDecoratedResourceRepositoryBase() {
        ScheduleRepository repository = Mockito.mock(ScheduleRepository.class);
        WrappedResourceRepository<Schedule, Long> decorator = new WrappedResourceRepository<>(repository);
        Assert.assertSame(repository, decorator.getWrappedObject());

        decorator.create(null);
        Mockito.verify(repository, Mockito.times(1)).create(Mockito.nullable(Schedule.class));

        decorator.delete(null);
        Mockito.verify(repository, Mockito.times(1)).delete(Mockito.nullable(Long.class));

        decorator.findAll(null);
        Mockito.verify(repository, Mockito.times(1)).findAll(Mockito.nullable(QuerySpec.class));

        decorator.findAll(null, null);
        Mockito.verify(repository, Mockito.times(1)).findAll(Mockito.nullable(java.util.Collection.class), Mockito.nullable(QuerySpec.class));

        decorator.getResourceClass();
        Mockito.verify(repository, Mockito.times(1)).getResourceClass();

        Schedule schedule = Mockito.mock(Schedule.class);
        decorator.save(schedule);
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.eq(schedule));

        decorator.findOne(null, null);
        Mockito.verify(repository, Mockito.times(1)).findOne(Mockito.nullable(Long.class), Mockito.nullable(QuerySpec.class));
    }

    interface RegistryAwareResourceRepository extends ScheduleRepository, ResourceRegistryAware {

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testDecoratedRelationshipRepositoryBase() {
        RelationshipRepository<Schedule, Long, Task, Long> repository = Mockito.mock(RelationshipRepository.class);
        WrappedRelationshipRepository<Schedule, Long, Task, Long> decorator = new WrappedRelationshipRepository(repository);

        decorator.findManyTargets(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).findManyTargets(Mockito.nullable(Long.class), Mockito.nullable(String.class),
                Mockito.nullable(QuerySpec.class));

        decorator.findOneTarget(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).findOneTarget(Mockito.nullable(Long.class), Mockito.nullable(String.class),
                Mockito.nullable(QuerySpec.class));

        decorator.setRelation(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).setRelation(Mockito.nullable(Schedule.class), Mockito.nullable(Long.class),
                Mockito.nullable(String.class));

        decorator.addRelations(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).addRelations(Mockito.nullable(Schedule.class), Mockito.nullable(java.util.Collection.class),
                Mockito.nullable(String.class));

        decorator.setRelations(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).setRelations(Mockito.nullable(Schedule.class), Mockito.nullable(java.util.Collection.class),
                Mockito.nullable(String.class));

        decorator.removeRelations(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).removeRelations(Mockito.nullable(Schedule.class), Mockito.nullable(java.util.Collection.class),
                Mockito.nullable(String.class));

        decorator.getTargetResourceClass();
        Mockito.verify(repository, Mockito.times(1)).getTargetResourceClass();

        decorator.getSourceResourceClass();
        Mockito.verify(repository, Mockito.times(1)).getSourceResourceClass();
    }
}
