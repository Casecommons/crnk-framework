package io.crnk.monitor.brave;

import brave.Tracing;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import io.crnk.client.CrnkClient;
import io.crnk.client.http.HttpAdapter;
import io.crnk.client.http.okhttp.OkHttpAdapter;
import io.crnk.core.boot.CrnkProperties;
import io.crnk.core.module.SimpleModule;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.RelationshipRepository;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.monitor.brave.mock.models.Project;
import io.crnk.monitor.brave.mock.models.Task;
import io.crnk.monitor.brave.mock.repository.ProjectRepository;
import io.crnk.monitor.brave.mock.repository.ProjectToTaskRepository;
import io.crnk.monitor.brave.mock.repository.TaskRepository;
import io.crnk.monitor.brave.mock.repository.TaskToProjectRepository;
import io.crnk.rs.CrnkFeature;
import io.crnk.test.JerseyTestBase;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBraveModuleTest extends JerseyTestBase {

    protected CrnkClient client;

    protected ResourceRepository<Task, Long> taskRepo;

    private List<MutableSpan> clientSpans;

    private List<MutableSpan> serverSpans;

    private HttpAdapter httpAdapter;

    private boolean isOkHttp;


    public AbstractBraveModuleTest(HttpAdapter httpAdapter) {
        this.httpAdapter = httpAdapter;
        this.isOkHttp = httpAdapter instanceof OkHttpAdapter;
    }


    @BeforeEach
    public void setup() {
        clientSpans = new ArrayList<>();
        SpanHandler clientHandler = new SpanHandler() {
            @Override
            public boolean end(TraceContext context, MutableSpan span, Cause cause) {
                clientSpans.add(span);
                return true;
            }
        };
        Tracing clientTracing = Tracing.newBuilder()
                .addSpanHandler(clientHandler)
                .localServiceName("testClient")
                .build();

        client = new CrnkClient(getBaseUri().toString());
        client.setHttpAdapter(httpAdapter);
        client.addModule(BraveClientModule.create(clientTracing));
        taskRepo = client.getRepositoryForType(Task.class);
        TaskRepository.clear();
        ProjectRepository.clear();
        httpAdapter.setReceiveTimeout(10000, TimeUnit.SECONDS);
    }

    @Test
    public void testCreate() {
        Task task = new Task();
        task.setId(13L);
        task.setName("myTask");
        taskRepo.create(task);

        // check client call and link span
        Assertions.assertEquals(1, clientSpans.size());
        MutableSpan callSpan = clientSpans.get(0);
        Assertions.assertEquals("post", callSpan.name().toLowerCase());
        Assertions.assertEquals(brave.Span.Kind.CLIENT, callSpan.kind());

        // check server local span
        Assertions.assertEquals(1, serverSpans.size());
        MutableSpan repositorySpan = serverSpans.get(0);
        Assertions.assertEquals("crnk:POST:/tasks/13/", repositorySpan.name());
        Assertions.assertNotNull(repositorySpan.tag("lc"));
        assertTag(repositorySpan, "lc", "crnk");
        assertTag(repositorySpan, "crnk.query", "?");

    }

    @Test
    public void testError() {
        Task task = new Task();
        task.setId(13L);
        try {
            taskRepo.create(task);
        } catch (Exception e) {
            // ok
        }

        // check client call and link span
        Assertions.assertEquals(1, clientSpans.size());
        MutableSpan callSpan = clientSpans.get(0);
        Assertions.assertEquals("post", callSpan.name().toLowerCase());
        Assertions.assertEquals(brave.Span.Kind.CLIENT, callSpan.kind());
        assertTag(callSpan, "http.status_code", "500");

        // check server local span
        Assertions.assertEquals(1, serverSpans.size());
        MutableSpan repositorySpan = serverSpans.get(0);
        Assertions.assertEquals("crnk:POST:/tasks/13/", repositorySpan.name());
        Assertions.assertNotNull(repositorySpan.tag("lc"));

        assertTag(repositorySpan, "lc", "crnk");
        assertTag(repositorySpan, "crnk.query", "?");
        assertTag(repositorySpan, "crnk.status", "EXCEPTION");
    }

    @Test
    public void testFindAll() {
        Task task = new Task();
        task.setId(13L);
        task.setName("myTask");
        QuerySpec querySpec = new QuerySpec(Task.class);
        querySpec.addFilter(new FilterSpec(Arrays.asList("name"), FilterOperator.EQ, "doe"));
        taskRepo.findAll(querySpec);

        // check client call and link span
        Assertions.assertEquals(1, clientSpans.size());
        MutableSpan callSpan = clientSpans.get(0);
        Assertions.assertEquals("get", callSpan.name().toLowerCase());
        Assertions.assertEquals(brave.Span.Kind.CLIENT, callSpan.kind());

        // check server local span
        Assertions.assertEquals(1, serverSpans.size());
        MutableSpan repositorySpan = serverSpans.get(0);
        Assertions.assertEquals("crnk:GET:/tasks/", repositorySpan.name());
        Assertions.assertNotNull(repositorySpan.tag("lc"));

        assertTag(repositorySpan, "lc", "crnk");
        assertTag(repositorySpan, "crnk.query", "?filter[name]=doe");
        assertTag(repositorySpan, "crnk.results", "0");
        assertTag(repositorySpan, "crnk.status", "OK");
    }

    @Test
    public void testFindTargets() {
        RelationshipRepository<Project, Serializable, Task, Serializable> relRepo = client
                .getRepositoryForType(Project.class, Task.class);
        relRepo.findManyTargets(123L, "tasks", new QuerySpec(Task.class));

        // check client call and link span
        Assertions.assertEquals(1, clientSpans.size());
        MutableSpan callSpan = clientSpans.get(0);
        Assertions.assertEquals("get", callSpan.name().toLowerCase());
        Assertions.assertEquals(brave.Span.Kind.CLIENT, callSpan.kind());

        // check server local span
        Assertions.assertEquals(2, serverSpans.size());

        MutableSpan repositorySpan0 = serverSpans.get(0);
        Assertions.assertEquals("crnk:GET:/tasks/", repositorySpan0.name());
        Assertions.assertNotNull(repositorySpan0.tag("lc"));

        assertTag(repositorySpan0, "lc", "crnk");
        assertTag(repositorySpan0, "crnk.results", "0");
        assertTag(repositorySpan0, "crnk.status", "OK");

        MutableSpan repositorySpan1 = serverSpans.get(1);
        Assertions.assertEquals("crnk:GET:/projects/123/tasks/", repositorySpan1.name());
        Assertions.assertNotNull(repositorySpan1.tag("lc"));

        assertTag(repositorySpan1, "lc", "crnk");
        assertTag(repositorySpan1, "crnk.query", "?");
        assertTag(repositorySpan1, "crnk.results", "0");
        assertTag(repositorySpan1, "crnk.status", "OK");
    }

    private void assertTag(MutableSpan span, String name, String value) {
        String tagValue = span.tag(name);
        Assertions.assertNotNull(tagValue, name + " not found");
        if (value != null) {
            Assertions.assertEquals(value, tagValue);
        }
    }

    @Override
    protected Application configure() {
        return new TestApplication();
    }

    @ApplicationPath("/")
    private class TestApplication extends ResourceConfig {

        public TestApplication() {
            property(CrnkProperties.RESOURCE_DEFAULT_DOMAIN, "http://test.local");

            serverSpans = new ArrayList<>();
            SpanHandler serverHandler = new SpanHandler() {
                @Override
                public boolean end(TraceContext context, MutableSpan span, Cause cause) {
                    serverSpans.add(span);
                    return true;
                }
            };

            Tracing tracing = Tracing.newBuilder()
                    .localServiceName("testServer")
                    .addSpanHandler(serverHandler)
                    .build();

            SimpleModule testModule = new SimpleModule("test");
            testModule.addRepository(new ProjectRepository());
            testModule.addRepository(new TaskRepository());
            testModule.addRepository(new TaskToProjectRepository());
            testModule.addRepository(new ProjectToTaskRepository());

            CrnkFeature feature = new CrnkFeature();
            feature.addModule(BraveServerModule.create(tracing));
            feature.addModule(testModule);
            register(feature);
        }
    }
}
