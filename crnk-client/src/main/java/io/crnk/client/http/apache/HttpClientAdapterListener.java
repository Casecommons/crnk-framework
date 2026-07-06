package io.crnk.client.http.apache;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

public interface HttpClientAdapterListener {

	void onBuild(HttpClientBuilder builder);

}
