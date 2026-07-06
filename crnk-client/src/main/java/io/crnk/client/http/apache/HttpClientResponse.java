package io.crnk.client.http.apache;

import io.crnk.client.http.HttpAdapterResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpClientResponse implements HttpAdapterResponse {

    private ClassicHttpResponse response;

    private String body;

    public HttpClientResponse(ClassicHttpResponse response) throws IOException {
        this.response = response;

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                body = EntityUtils.toString(entity);
            } catch (ParseException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public boolean isSuccessful() {
        return response.getCode() < 400;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public int code() {
        return response.getCode();
    }

    @Override
    public String message() {
        return response.getReasonPhrase();
    }

    @Override
    public String getResponseHeader(String name) {
        Header header = response.getFirstHeader(name);
        return header != null ? header.getValue() : null;
    }

    @Override
    public Set<String> getHeaderNames() {
        return Arrays.asList(response.getHeaders()).stream().map(it -> it.getName()).collect(Collectors.toSet());
    }
}
