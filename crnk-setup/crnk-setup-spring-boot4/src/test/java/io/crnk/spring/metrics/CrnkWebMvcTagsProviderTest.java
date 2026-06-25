package io.crnk.spring.metrics;

import static org.junit.Assert.assertEquals;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.engine.registry.ResourceRegistry;
import io.crnk.spring.setup.boot.monitor.CrnkServerRequestObservationConvention;
import io.crnk.test.mock.TestModule;
import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(JUnitParamsRunner.class)
public class CrnkWebMvcTagsProviderTest {

	private ResourceRegistry resourceRegistry;

	private CrnkBoot boot;

	private CrnkServerRequestObservationConvention convention;

	@Before
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
		// no path pattern resolved by Crnk -> falls back to the default convention, which yields URI "UNKNOWN"
		assertEquals("UNKNOWN", getUriTag(convention.getLowCardinalityKeyValues(context)));
	}

	@SuppressWarnings("unused")
	private Object[] handleCrnkResourceParameters() {
		String id = "124";

		return new Object[] {
				new Object[] {
						"/tasks",
						"/tasks"
				},
				new Object[] {
						"/tasks/" + id,
						"/tasks/{id}"
				},
				new Object[] {
						"/tasks/" + id + "/name",
						"/tasks/{id}/name"
				},
				new Object[] {
						"/tasks/" + id + "/relationships/project",
						"/tasks/{id}/relationships/project"
				}
		};
	}

	@Test
	@Parameters(method = "handleCrnkResourceParameters")
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
