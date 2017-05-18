package com.other.httpclient.utils;

import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * Http客户端工具类
 */
public final class HttpClientUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final int DEFAULT_SO_TIMEOUT = 15000;
	private static final String HTTPS_PROTOCOL = "https";
	private static CloseableHttpClient httpClient;
	private static CloseableHttpClient httpsClient;
	private static ResponseHandler<String> stringResponseHandler = new ResponseHandler<String>() {
		@Override
		public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity, DEFAULT_CHARSET) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + statusCode);
			}
		}
	};

	static {
		Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
		try {
			SSLContextBuilder builder = SSLContexts.custom();
			builder.loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			});
			SSLContext sslContext = builder.build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
				@Override
				public void verify(String host, SSLSocket ssl) throws IOException {
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException {
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				}

				@Override
				public boolean verify(String s, SSLSession sslSession) {
					return true;
				}
			});

			socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("https", sslsf).build();
		} catch (Exception e) {
			logger.error("", e);
		}

		PoolingHttpClientConnectionManager httpsConnectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		httpsConnectionManager.setMaxTotal(200);
		httpsConnectionManager.setDefaultMaxPerRoute(30);
		SocketConfig httpsConfig = SocketConfig.custom().setSoTimeout(DEFAULT_SO_TIMEOUT).build();
		httpsConnectionManager.setDefaultSocketConfig(httpsConfig);
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000)
				.setConnectionRequestTimeout(3000).setStaleConnectionCheckEnabled(true).build();
		httpsClient = HttpClients.custom().setConnectionManager(httpsConnectionManager)
				.setDefaultRequestConfig(defaultRequestConfig).build();

		PoolingHttpClientConnectionManager httpConnectionManager = new PoolingHttpClientConnectionManager();
		httpConnectionManager.setMaxTotal(200);
		httpConnectionManager.setDefaultMaxPerRoute(30);
		SocketConfig httpConfig = SocketConfig.custom().setSoTimeout(DEFAULT_SO_TIMEOUT).build();
		httpConnectionManager.setDefaultSocketConfig(httpConfig);

		httpClient = HttpClients.custom().setConnectionManager(httpConnectionManager).build();
	}

	private HttpClientUtils() {
	}

	public static String get(final String uri) throws IOException {
		return send(Method.GET, uri, null);
	}

	public static String send(final Method method, final String uri, final Map<String, String> params)
			throws IOException {
		return send(method, uri, params, false, DEFAULT_CHARSET);
	}

	public static String send(final Method method, final String uri, final String params, final boolean useURLEncoder,
			final String encodeCharset) throws IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		StringEntity entity = null;
		if (null != params) {
			if (params.contains("&")) {
				for (String str : params.split("&")) {
					nameValuePairs.add(new BasicNameValuePair(str.substring(0, str.indexOf("=")),
							str.substring(str.indexOf("=") + 1)));
				}
			} else {
				if (StringUtils.isEmpty(encodeCharset))
					entity = new StringEntity(params);
				else if (useURLEncoder)
					entity = new StringEntity(params, encodeCharset);
			}
		}
		return send(method, uri, null, entity, nameValuePairs, useURLEncoder, encodeCharset, stringResponseHandler,
				new BasicHttpContext());
	}

	public static String send(final Method method, final String uri, final String params, final boolean useURLEncoder,
			final String encodeCharset, final int timeout) throws IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		StringEntity entity = null;
		if (null != params) {
			if (params.contains("&")) {
				for (String str : params.split("&")) {
					nameValuePairs.add(new BasicNameValuePair(str.substring(0, str.indexOf("=")),
							str.substring(str.indexOf("=") + 1)));
				}
			} else {
				if (StringUtils.isEmpty(encodeCharset))
					entity = new StringEntity(params);
				else if (useURLEncoder)
					entity = new StringEntity(params, encodeCharset);
			}
		}
		return send(method, uri, null, entity, nameValuePairs, useURLEncoder, encodeCharset, stringResponseHandler,
				new BasicHttpContext(), RequestConfig.custom().setConnectionRequestTimeout(timeout)
						.setConnectTimeout(timeout).setSocketTimeout(timeout).build());
	}

	public static String send(final Method method, final String uri, final Map<String, String> params,
			final boolean useURLEncoder, final String encodeCharset) throws IOException {
		return send(method, uri, params, useURLEncoder, encodeCharset, null);
	}

	public static String send(final Method method, final String uri, final Map<String, String> params,
			final boolean useURLEncoder, final String encodeCharset, final Integer timeout) throws IOException {
		RequestConfig config = null;
		if (timeout == null)
			config = RequestConfig.DEFAULT;
		else
			RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout)
					.setSocketTimeout(timeout).build();
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		if (params != null && params.size() > 0) {
			for (String name : params.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(name, params.get(name)));
			}
		}
		return send(method, uri, null, null, nameValuePairs, useURLEncoder, encodeCharset, stringResponseHandler, null,
				config);
	}

	/**
	 * 发起POST请求
	 *
	 * @param uri
	 *            请求地址
	 * @param headers
	 *            请求头
	 * @param handler
	 *            响应回调处理
	 * @param context
	 *            请求上下文
	 * @param
	 * @return 响应
	 * @throws IOException
	 *             IO异常
	 */
	public static <T> T send(final Method method, final String uri, final Map<String, String> headers,
			final HttpEntity entity, final List<NameValuePair> nameValuePairs, final boolean useURLEncode,
			final String encodeCharset, final ResponseHandler<T> handler, final HttpContext context)
			throws IOException {
		return send(method, uri, headers, entity, nameValuePairs, useURLEncode, encodeCharset, handler, context, null);
	}

	/**
	 * 发起http请求
	 *
	 * @param method
	 *            请求方式
	 * @param uri
	 *            请求地址
	 * @param headers
	 *            请求头
	 * @param entity
	 *            请求体
	 * @param nameValuePairs
	 *            键值对参数
	 * @param useURLEncode
	 *            使用URL编码
	 * @param encodeCharset
	 *            编码字符集
	 * @param handler
	 *            响应处理
	 * @param context
	 *            请求上下文
	 * @param config
	 *            请求配置
	 * @return 请求处理后的结果
	 * @throws IOException
	 */
	public static <T> T send(final Method method, final String uri, final Map<String, String> headers,
			final HttpEntity entity, final List<NameValuePair> nameValuePairs, final boolean useURLEncode,
			final String encodeCharset, final ResponseHandler<T> handler, final HttpContext context,
			final RequestConfig config) throws IOException {
		RequestBuilder builder = null;
		if (Method.GET.equals(method))
			builder = RequestBuilder.get();
		else if (Method.POST.equals(method))
			builder = RequestBuilder.post();
		if (headers != null && headers.size() > 0) {
			for (String name : headers.keySet()) {
				builder.addHeader(name, headers.get(name));
			}
		}
		builder.setUri(uri);
		builder.addParameters(nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]));
		builder.setEntity(entity);
		builder.setConfig(config == null ? RequestConfig.DEFAULT : config);
		HttpUriRequest request = builder.build();
		if (HTTPS_PROTOCOL.equals(new URL(uri).getProtocol()))
			return httpsClient.execute(request, handler, context == null ? new BasicHttpContext() : context);
		return httpClient.execute(request, handler, context == null ? new BasicHttpContext() : context);

	}

	public static String send(final Method method, final String url, final Map<String, String> headers,
			final Map<String, String> params, final boolean useURLEncoder, final String encodeCharset)
			throws IOException {
		UrlEncodedFormEntity entity = null;
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		if (params != null && params.size() > 0) {
			for (String name : params.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(name, params.get(name)));
			}
			if (useURLEncoder) {
				entity = new UrlEncodedFormEntity(nameValuePairs, encodeCharset);
			}
		}
		if (entity == null)
			return send(method, url, headers, null, nameValuePairs, useURLEncoder, encodeCharset, stringResponseHandler,
					null);
		else
			return send(method, url, headers, entity, nameValuePairs, useURLEncoder, encodeCharset,
					stringResponseHandler, null);
	}

	public static String send(final Method method, final String url, final Map<String, String> headers,
			final Map<String, String> params, final boolean useURLEncoder, final String encodeCharset,
			final Integer timeout) throws IOException {
		RequestConfig config = null;
		if (timeout == null)
			config = RequestConfig.DEFAULT;
		else
			RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout)
					.setSocketTimeout(timeout).build();
		UrlEncodedFormEntity entity = null;
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		if (params != null && params.size() > 0) {
			for (String name : params.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(name, params.get(name)));
			}
			if (useURLEncoder) {
				entity = new UrlEncodedFormEntity(nameValuePairs, encodeCharset);
			}
		}
		if (entity == null)
			return send(method, url, headers, null, nameValuePairs, useURLEncoder, encodeCharset, stringResponseHandler,
					null);
		else
			return send(method, url, headers, entity, nameValuePairs, useURLEncoder, encodeCharset,
					stringResponseHandler, null);
	}

	/**
	 * POST请求发送JSONObject的工具方法
	 *
	 * @param url
	 * @param header
	 * @param jsonObject
	 * @return
	 * @throws IOException
	 */
	public static String sendJSON(String url, Map<String, String> header, JSONObject jsonObject) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		httpPost.addHeader("Content-Type", "application/json;charset=" + DEFAULT_CHARSET);
		// 解决中文乱码问题
		if (jsonObject == null) {
			jsonObject = new JSONObject();
		}
		StringEntity stringEntity = new StringEntity(jsonObject.toString(), DEFAULT_CHARSET);
		stringEntity.setContentEncoding(DEFAULT_CHARSET);
		httpPost.setEntity(stringEntity);
		ResponseHandler<String> responseHandler = (response) -> {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity, DEFAULT_CHARSET) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status);
			}
		};
		return httpClient.execute(httpPost, responseHandler);
	}

	public static String post(String url, Map<String, String> params) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> valuePairs;
		if (params != null) {
			valuePairs = new ArrayList<>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
				valuePairs.add(nameValuePair);
			}
		} else {
			valuePairs = new ArrayList<>();
		}
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, DEFAULT_CHARSET);
		httpPost.setEntity(formEntity);
		ResponseHandler<String> responseHandler = (response) -> {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity, DEFAULT_CHARSET) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status);
			}
		};
		return httpClient.execute(httpPost, responseHandler);
	}

	public static void main(String[] args) throws IOException {

		Map<String, String> params = new HashMap<>();
		params.put("Code", "123");
		params.put("Type", "123");
		System.out.print(send(Method.POST, "http://10.10.116.33:13151/a", null, params, true, "UTF-8"));

	}

	public static enum Method {
		POST, GET, OPTION, CREATE
	}

	public static CloseableHttpClient getHttpClient() {
		return httpClient;
	}
	public static CloseableHttpClient getHttpsClient() {
		return httpsClient;
	}
}
