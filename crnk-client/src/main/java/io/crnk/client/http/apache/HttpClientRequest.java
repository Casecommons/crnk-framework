package io.crnk.client.http.apache;

import io.crnk.client.http.HttpAdapterListener;
import io.crnk.client.http.HttpAdapterRequest;
import io.crnk.client.http.HttpAdapterResponse;
import io.crnk.core.engine.http.HttpHeaders;
import io.crnk.core.engine.http.HttpMethod;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpClientRequest implements HttpAdapterRequest {

    private static final ContentType CONTENT_TYPE = ContentType.create(HttpHeaders.JSONAPI_CONTENT_TYPE,
            HttpHeaders.DEFAULT_CHARSET);

    private final List<HttpAdapterListener> listeners;

    private final String requestBody;

    private HttpUriRequestBase requestBase;

    private CloseableHttpClient impl;

    public HttpClientRequest(CloseableHttpClient impl, String url, HttpMethod method, String requestBody, List<HttpAdapterListener> listeners) {
        this.impl = impl;
        this.listeners = listeners;
        this.requestBody = requestBody;
        if (method == HttpMethod.GET) {
            requestBase = new HttpGet(url);
        } else if (method == HttpMethod.POST) {
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(requestBody, CONTENT_TYPE));
            requestBase = post;
        } else if (method == HttpMethod.PATCH) {
            HttpPatch patch = new HttpPatch(url);
            patch.setEntity(new StringEntity(requestBody, CONTENT_TYPE));
            requestBase = patch;
        } else if (method == HttpMethod.DELETE) {
            requestBase = new HttpDelete(url);
        } else {
            throw new UnsupportedOperationException(method.toString());
        }
    }

    @Override
    public void header(String name, String value) {
        requestBase.setHeader(name, value);
    }

    @Override
    public HttpAdapterResponse execute() throws IOException {
        listeners.stream().forEach(it -> it.onRequest(this));
        HttpClientResponse response = new HttpClientResponse(impl.execute(requestBase));
        listeners.stream().forEach(it -> it.onResponse(this, response));
        return response;
    }

    @Override
    public String getBody() {
        return requestBody;
    }

    @Override
    public String getUrl() {
        try {
            return requestBase.getUri().toString();
        } catch (URISyntaxException e) {
            return requestBase.getRequestUri();
        }
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.valueOf(requestBase.getMethod());
    }

    @Override
    public Set<String> getHeadersNames() {
        return Arrays.asList(requestBase.getHeaders())
                .stream()
                .map(it -> it.getName())
                .collect(Collectors.toSet());
    }

    @Override
    public String getHeaderValue(String name) {
        Header firstHeader = requestBase.getFirstHeader(name);
        return firstHeader != null ? firstHeader.getValue() : null;
    }
}
