/*
 * Copyright 2019-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.http.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.http.Header;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.vividus.http.handler.HttpResponseHandler;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("MethodCount")
public class HttpClientConfig
{
    private String baseUrl;
    private AuthConfig authConfig;
    private AuthScope authScope;
    private Map<String, String> headers;
    private SslConfig sslConfig;
    private HttpClientConnectionManager connectionManager;
    private int maxTotalConnections;
    private int maxConnectionsPerRoute;
    private HttpRequestInterceptor lastRequestInterceptor;
    private HttpResponseInterceptor lastResponseInterceptor;
    private RedirectStrategy redirectStrategy;
    private int connectionRequestTimeout = -1;
    private int connectTimeout = -1;
    private int socketTimeout;
    private CookieStore cookieStore;
    private boolean skipResponseEntity;
    private DnsResolver dnsResolver;
    private boolean circularRedirectsAllowed;
    private String cookieSpec;
    private HttpRequestRetryHandler httpRequestRetryHandler;
    private ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy;
    private List<HttpResponseHandler> httpResponseHandlers;

    public boolean hasBaseUrl()
    {
        return baseUrl != null && !baseUrl.isEmpty();
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public boolean hasAuthScope()
    {
        return authScope != null;
    }

    public List<Header> createHeaders()
    {
        return headers != null ? headers.entrySet().stream().map(e -> new BasicHeader(e.getKey(), e.getValue()))
                .collect(Collectors.toList()) : List.of();
    }

    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }

    public AuthScope getAuthScope()
    {
        return authScope;
    }

    public void setAuthScope(AuthScope authScope)
    {
        this.authScope = authScope;
    }

    public SslConfig getSslConfig()
    {
        return sslConfig;
    }

    @JsonProperty("ssl")
    public void setSslConfig(SslConfig sslConfig)
    {
        this.sslConfig = sslConfig;
    }

    public AuthConfig getAuthConfig()
    {
        return authConfig;
    }

    @JsonProperty("auth")
    public void setAuthConfig(AuthConfig authConfig)
    {
        this.authConfig = authConfig;
    }

    public HttpClientConnectionManager getConnectionManager()
    {
        return connectionManager;
    }

    public void setConnectionManager(HttpClientConnectionManager connectionManager)
    {
        this.connectionManager = connectionManager;
    }

    public int getMaxTotalConnections()
    {
        return maxTotalConnections;
    }

    public void setMaxTotalConnections(int maxTotalConnections)
    {
        this.maxTotalConnections = maxTotalConnections;
    }

    public int getMaxConnectionsPerRoute()
    {
        return maxConnectionsPerRoute;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute)
    {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public HttpRequestInterceptor getLastRequestInterceptor()
    {
        return lastRequestInterceptor;
    }

    public void setLastRequestInterceptor(HttpRequestInterceptor lastRequestInterceptor)
    {
        this.lastRequestInterceptor = lastRequestInterceptor;
    }

    public HttpResponseInterceptor getLastResponseInterceptor()
    {
        return lastResponseInterceptor;
    }

    public void setLastResponseInterceptor(HttpResponseInterceptor lastResponseInterceptor)
    {
        this.lastResponseInterceptor = lastResponseInterceptor;
    }

    public RedirectStrategy getRedirectStrategy()
    {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy)
    {
        this.redirectStrategy = redirectStrategy;
    }

    public int getConnectionRequestTimeout()
    {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout)
    {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getConnectTimeout()
    {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout()
    {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }

    public boolean hasCookieStore()
    {
        return cookieStore != null;
    }

    public CookieStore getCookieStore()
    {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore)
    {
        this.cookieStore = cookieStore;
    }

    public boolean isSkipResponseEntity()
    {
        return skipResponseEntity;
    }

    public void setSkipResponseEntity(boolean skipResponseEntity)
    {
        this.skipResponseEntity = skipResponseEntity;
    }

    public DnsResolver getDnsResolver()
    {
        return dnsResolver;
    }

    public void setDnsResolver(DnsResolver dnsResolver)
    {
        this.dnsResolver = dnsResolver;
    }

    public boolean isCircularRedirectsAllowed()
    {
        return circularRedirectsAllowed;
    }

    public void setCircularRedirectsAllowed(boolean circularRedirectsAllowed)
    {
        this.circularRedirectsAllowed = circularRedirectsAllowed;
    }

    public String getCookieSpec()
    {
        return cookieSpec;
    }

    public void setCookieSpec(String cookieSpec)
    {
        this.cookieSpec = cookieSpec;
    }

    public HttpRequestRetryHandler getHttpRequestRetryHandler()
    {
        return httpRequestRetryHandler;
    }

    public void setHttpRequestRetryHandler(HttpRequestRetryHandler httpRequestRetryHandler)
    {
        this.httpRequestRetryHandler = httpRequestRetryHandler;
    }

    public ServiceUnavailableRetryStrategy getServiceUnavailableRetryStrategy()
    {
        return serviceUnavailableRetryStrategy;
    }

    public void setServiceUnavailableRetryStrategy(ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy)
    {
        this.serviceUnavailableRetryStrategy = serviceUnavailableRetryStrategy;
    }

    public List<HttpResponseHandler> getHttpResponseHandlers()
    {
        return httpResponseHandlers;
    }

    public void setHttpResponseHandlers(List<HttpResponseHandler> httpResponseHandlers)
    {
        this.httpResponseHandlers = httpResponseHandlers;
    }
}
