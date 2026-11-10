package io.crnk.core.queryspec.mapper;

import io.crnk.core.CoreTestContainer;
import io.crnk.core.CoreTestModule;
import io.crnk.core.engine.information.resource.ResourceInformation;
import io.crnk.core.engine.query.QueryContext;
import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ParametersDeserializationException;
import io.crnk.core.mock.models.Project;
import io.crnk.core.mock.models.Task;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.queryspec.pagingspec.NumberSizePagingBehavior;
import io.crnk.core.queryspec.pagingspec.NumberSizePagingSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultQuerySpecUrlMapperDeserializerTest extends DefaultQuerySpecUrlMapperDeserializerTestBase {


    private QueryContext queryContext = new QueryContext().setRequestVersion(0);

    @BeforeEach
    public void setup() {
        super.setup();
        urlMapper.setEnforceDotPathSeparator(true);
        Assertions.assertTrue(urlMapper.getEnforceDotPathSeparator());
    }

    @Test
    public void testDotNotationDisallowsBrackets() {
        assertThrows(ParametersDeserializationException.class, () -> {
            Map<String, Set<String>> params = new HashMap<>();
            add(params, "filter[projects][tasks][name]", "test");
            urlMapper.deserialize(taskInformation, params, queryContext);
        });
    }

    @Test
    public void testCannotFilterNonFilterableAttribute() {
        Map<String, Set<String>> params = new HashMap<>();
        add(params, "filter[deleted]", "true");
        try {
            urlMapper.deserialize(taskInformation, params, queryContext);
            Assertions.fail();
        } catch (BadRequestException e) {
            Assertions.assertEquals("path [deleted] is not filterable", e.getMessage());
        }
    }

    @Test
    public void testCannotSortNonSortableAttribute() {
        Map<String, Set<String>> params = new HashMap<>();
        add(params, "sort", "deleted");
        try {
            urlMapper.deserialize(taskInformation, params, queryContext);
            Assertions.fail();
        } catch (BadRequestException e) {
            Assertions.assertEquals("path [deleted] is not sortable", e.getMessage());
        }
    }


    @Test
    public void testNoAmbiguityForType() {
        Map<String, Set<String>> params = new HashMap<>();
        // note that there is both a type and an attribute on tasks called
        // projects
        add(params, "filter[projects][name]", "test");
        QuerySpec querySpec = urlMapper.deserialize(taskInformation, params, queryContext);
        Assertions.assertEquals(Task.class, querySpec.getResourceClass());
        Assertions.assertEquals(0, querySpec.getFilters().size());
        QuerySpec projectQuerySpec = querySpec.getQuerySpec(Project.class);
        Assertions.assertEquals(1, projectQuerySpec.getFilters().size());
        Assertions.assertEquals(Arrays.asList("name"), projectQuerySpec.getFilters().get(0).getAttributePath());
    }

    @Test
    public void testNoAmbiguityForAttribute() {
        // note that there is both a type and an attribute on tasks called
        // projects, here the attribute should match
        Map<String, Set<String>> params = new HashMap<>();
        add(params, "filter[projects]", "someValue");
        urlMapper.setIgnoreParseExceptions(true);
        QuerySpec querySpec = urlMapper.deserialize(taskInformation, params, queryContext);
        Assertions.assertEquals(Task.class, querySpec.getResourceClass());
        Assertions.assertEquals(Arrays.asList("projects"), querySpec.getFilters().get(0).getAttributePath());
        Assertions.assertNull(querySpec.getQuerySpec(Project.class));
    }

    @Test
    public void testRelatedQuerySpecGetsAnEmptyPagingSpecOfTheRootsPagingBehavior() {
        CoreTestContainer container = new CoreTestContainer();
        container.addModule(new CoreTestModule());
        container.getBoot().getModuleRegistry().addPagingBehavior(new NumberSizePagingBehavior());
        container.boot();
        DefaultQuerySpecUrlMapper numberSizeUrlMapper = (DefaultQuerySpecUrlMapper) container.getBoot().getUrlMapper();
        ResourceInformation numberSizeTaskInformation =
                container.getResourceRegistry().getEntry(Task.class).getResourceInformation();

        Map<String, Set<String>> params = new HashMap<>();
        add(params, "filter[projects][name]", "test");
        add(params, "page[number]", "2");
        add(params, "page[size]", "5");
        QuerySpec querySpec = numberSizeUrlMapper.deserialize(numberSizeTaskInformation, params, queryContext);

        NumberSizePagingSpec rootPagingSpec = (NumberSizePagingSpec) querySpec.getPaging();
        Assertions.assertEquals(2, rootPagingSpec.getNumber());
        Assertions.assertEquals(5, rootPagingSpec.getSize());

        // The related query spec is created while the filter is parsed, so it must not keep the default
        // OffsetLimitPagingSpec, nor take on the root's page number and size.
        QuerySpec projectQuerySpec = querySpec.getQuerySpec(Project.class);
        Assertions.assertEquals(1, projectQuerySpec.getFilters().size());
        Assertions.assertInstanceOf(NumberSizePagingSpec.class, projectQuerySpec.getPaging());
        NumberSizePagingSpec projectPagingSpec = (NumberSizePagingSpec) projectQuerySpec.getPaging();
        Assertions.assertEquals(1, projectPagingSpec.getNumber());
        Assertions.assertNull(projectPagingSpec.getSize());
    }
}
