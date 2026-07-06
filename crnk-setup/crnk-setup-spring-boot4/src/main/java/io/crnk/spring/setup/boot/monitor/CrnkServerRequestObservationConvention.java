package io.crnk.spring.setup.boot.monitor;

import java.net.MalformedURLException;
import java.net.URL;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.engine.http.HttpRequestContext;
import io.crnk.core.engine.internal.dispatcher.path.JsonPath;
import io.crnk.core.engine.internal.dispatcher.path.PathBuilder;
import io.crnk.core.engine.internal.http.HttpRequestContextBaseAdapter;
import io.crnk.core.engine.internal.utils.UrlUtils;
import io.crnk.core.engine.parser.TypeParser;
import io.crnk.servlet.internal.ServletRequestContext;
import io.micrometer.common.KeyValue;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * Crnk {@link org.springframework.http.server.observation.ServerRequestObservationConvention} implementation that
 * extends the built-in {@link DefaultServerRequestObservationConvention} and overrides the {@code uri} key value
 * recognition logic for Crnk resources in order to have a proper {@code uri} tag value. Uses the base class value as
 * fallback.
 * <p>
 * Replaces the Spring Boot 2 {@code WebMvcTagsProvider} based implementation which was removed in Spring Boot 3+.
 */
public class CrnkServerRequestObservationConvention extends DefaultServerRequestObservationConvention {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrnkServerRequestObservationConvention.class);

	private static final String URI_KEY = "uri";

	private final CrnkBoot boot;

	public CrnkServerRequestObservationConvention(CrnkBoot boot) {
		this.boot = boot;
		LOGGER.debug("initialized observation convention");
	}

	@Override
	protected KeyValue uri(ServerRequestObservationContext context) {
		HttpServletRequest request = context.getCarrier();
		if (request != null) {
			String uri = resolveCrnkUri(request);
			if (uri != null) {
				LOGGER.debug("computed mvc tag: uri={}", uri);
				return KeyValue.of(URI_KEY, uri);
			}
		}
		return super.uri(context);
	}

	private String resolveCrnkUri(final HttpServletRequest request) {
		if (matchesPrefix(request)) {
			ServletContext servletContext = request.getServletContext();
			HttpRequestContext context = new HttpRequestContextBaseAdapter(
					new ServletRequestContext(servletContext, request, null, boot.getWebPathPrefix()));
			context.getQueryContext().initializeDefaults(boot.getResourceRegistry());

			String path = context.getPath();

			TypeParser typeParser = boot.getModuleRegistry().getTypeParser();
			PathBuilder pathBuilder = new PathBuilder(boot.getResourceRegistry(), typeParser);

			JsonPath jsonPath = pathBuilder.build(path, context.getQueryContext());
			if (jsonPath != null) {
				URL baseUrl;
				try {
					baseUrl = new URL(context.getBaseUrl());
				}
				catch (MalformedURLException e) {
					throw new IllegalStateException(e);
				}
				String uri = baseUrl.getPath() + "/" + jsonPath.toGroupPath();
				return uri;
			}
			LOGGER.debug("unknown path, using default mvc tags: uri={}", request.getRequestURI());
		}
		return null;
	}

	private boolean matchesPrefix(HttpServletRequest request) {
		String pathPrefix = UrlUtils.removeLeadingSlash(boot.getWebPathPrefix());
		String path = UrlUtils.removeLeadingSlash(
				request.getRequestURI().substring(request.getContextPath().length()));
		return pathPrefix == null || path.startsWith(pathPrefix);
	}
}
