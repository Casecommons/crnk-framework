package io.crnk.client.http.apache;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

public class HttpClientAdapterListenerBase implements HttpClientAdapterListener {

	@Override
	public void onBuild(HttpClientBuilder builder) {
		// nothing to do
	}
}
