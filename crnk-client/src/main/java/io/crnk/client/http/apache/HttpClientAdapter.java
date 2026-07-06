package io.crnk.client.http.apache;

import io.crnk.client.http.HttpAdapter;
import io.crnk.client.http.HttpAdapterListener;
import io.crnk.client.http.HttpAdapterRequest;
import io.crnk.core.engine.http.HttpMethod;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class HttpClientAdapter implements HttpAdapter {

    private CloseableHttpClient impl;

    private CopyOnWriteArrayList<HttpClientAdapterListener> nativeListeners = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<HttpAdapterListener> listeners = new CopyOnWriteArrayList<>();

    private Function<HttpClientBuilder, CloseableHttpClient> clientBuilder;

    @Override
    public void addListener(HttpAdapterListener listener) {
        checkNotInitialized();
        listeners.add(listener);
    }

    private Integer receiveTimeout;

    public static HttpClientAdapter newInstance() {
        return new HttpClientAdapter();
    }

    public void addListener(HttpClientAdapterListener listener) {
        checkNotInitialized();
        nativeListeners.add(listener);
    }

    /**
     * Sets a custom build function that takes the configured HttpClientBuilder
     * and returns a CloseableHttpClient. Used by tracing integrations (e.g. Brave)
     * that need to control how the client is built.
     */
    public void setClientBuilder(Function<HttpClientBuilder, CloseableHttpClient> clientBuilder) {
        checkNotInitialized();
        this.clientBuilder = clientBuilder;
    }

    private void checkNotInitialized() {
        if (impl != null) {
            throw new IllegalStateException("already initialized");
        }
    }

    public CloseableHttpClient getImplementation() {
        if (impl == null) {
            initImpl();
        }
        return impl;
    }

    private synchronized void initImpl() {
        if (impl == null) {
            HttpClientBuilder builder = HttpClients.custom();

            if (receiveTimeout != null) {
                RequestConfig.Builder requestBuilder = RequestConfig.custom();
                requestBuilder = requestBuilder.setResponseTimeout(receiveTimeout, TimeUnit.MILLISECONDS);
                builder.setDefaultRequestConfig(requestBuilder.build());
            }

            for (HttpClientAdapterListener listener : nativeListeners) {
                listener.onBuild(builder);
            }

            if (clientBuilder != null) {
                impl = clientBuilder.apply(builder);
            } else {
                impl = builder.build();
            }
        }
    }

    @Override
    public HttpAdapterRequest newRequest(String url, HttpMethod method, String requestBody) {
        CloseableHttpClient implementation = getImplementation();
        return new HttpClientRequest(implementation, url, method, requestBody, listeners);
    }

    @Override
    public void setReceiveTimeout(int timeout, TimeUnit unit) {
        checkNotInitialized();
        receiveTimeout = (int) unit.toMillis(timeout);
    }
}
