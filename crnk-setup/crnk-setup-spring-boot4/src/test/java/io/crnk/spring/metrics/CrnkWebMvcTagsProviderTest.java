package io.crnk.spring.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.engine.registry.ResourceRegistry;
import io.crnk.spring.setup.boot.monitor.CrnkServerRequestObservationConvention;
import io.crnk.test.mock.TestModule;
import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.stream.Stream;

public class CrnkWebMvcTagsProviderTest {

	private ResourceRegistry resourceRegistry;

	private CrnkBoot boot;

	private CrnkServerRequestObservationConvention convention;

	@BeforeEach
	public void setup() {
		boot = new CrnkBoot();
		boot.addModule(new TestModule());
		boot.boot();
		convention = new CrnkServerRequestObservationConvention(boot);
	}

	@Test
	public void useFallbackIfNotCrnkResource() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingPattern", "/any");

		MockHttpServletResponse response = new MockHttpServletResponse();
		ServerRequestObservationContext context = new ServerRequestObservationContext(request, response);
		assertEquals("UNKNOWN", getUriTag(convention.getLowCardinalityKeyValues(context)));
	}

	static Stream<Arguments> handleCrnkResourceParameters() {
		String id = "124";
		return Stream.of(
				Arguments.of("/tasks", "/tasks"),
				Arguments.of("/tasks/" + id, "/tasks/{id}"),
				Arguments.of("/tasks/" + id + "/name", "/tasks/{id}/name"),
				Arguments.of("/tasks/" + id + "/relationships/project", "/tasks/{id}/relationships/project")
		);
	}

	@ParameterizedTest
	@MethodSource("handleCrnkResourceParameters")
	public void handleCrnkResource(final String requestUrl, final String expected) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI(requestUrl);

		MockHttpServletResponse response = new MockHttpServletResponse();
		ServerRequestObservationContext context = new ServerRequestObservationContext(request, response);

		assertEquals(expected, getUriTag(convention.getLowCardinalityKeyValues(context)));
	}

	private String getUriTag(KeyValues keyValues) {
		for (KeyValue keyValue : keyValues) {
			if (keyValue.getKey().equals("uri")) {
				return keyValue.getValue();
			}
		}
		throw new IllegalStateException();
	}
}
