package io.crnk.monitor.brave.internal;

import brave.http.HttpTracing;
import brave.httpclient5.HttpClient5Tracing;
import io.crnk.client.http.apache.HttpClientAdapterListener;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;

/**
 * Integrates Brave tracing with Apache HttpClient 5.x.
 * Implements a custom build step that uses Brave's {@link HttpClient5Tracing}
 * to add tracing interceptors to the HttpClient builder.
 */
public class HttpClientBraveIntegration implements HttpClientAdapterListener {

	private final HttpClient5Tracing tracing;

	public HttpClientBraveIntegration(HttpTracing httpTracing) {
		this.tracing = HttpClient5Tracing.newBuilder(httpTracing);
	}

	@Override
	public void onBuild(HttpClientBuilder builder) {
		// Nothing to do here — interceptors are added during buildClient()
	}

	/**
	 * Build a traced HttpClient. Called by the adapter instead of builder.build()
	 * when this integration is present.
	 */
	public CloseableHttpClient buildClient(HttpClientBuilder builder) {
		return tracing.build(builder);
	}
}
