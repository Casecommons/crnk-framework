package io.crnk.client.http.apache;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

@Deprecated
public interface HttpClientBuilderFactory {

	HttpClientBuilder createBuilder();

}
