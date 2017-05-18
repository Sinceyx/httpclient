package com.other.httpclient.pool.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

public class HttpClientConfig {
	private static CloseableHttpClient httpClient;
	private static HttpHost httpProxy;
	static {
		initHttpClient(null, 0, null, null);
	}

	private static void initHttpClient(String httpProxyHost, int httpProxyPort, String httpProxyUsername,
			String httpProxyPassword) {
		ApacheHttpClientBuilder apacheHttpClientBuilder = DefaultApacheHttpClientBuilder.get();
		apacheHttpClientBuilder.httpProxyHost(httpProxyHost).httpProxyPort(httpProxyPort)
				.httpProxyUsername(httpProxyUsername).httpProxyPassword(httpProxyPassword);
		if (httpProxyHost != null && httpProxyPort > 0) {
			httpProxy = new HttpHost(httpProxyHost, httpProxyPort);
		}
		httpClient = apacheHttpClientBuilder.build();
	}

	public static CloseableHttpClient getHttpClient() {
		return httpClient;
	}
	public static HttpHost getHttpProxy() {
		return httpProxy;
	}	
}
