package com.other.httpclient.pool.utils;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		RequestExecutor<String, String> getExecutor=new  SimpleGetRequestExecutor();
		RequestExecutor<String, String> postExecutor=new  SimplePostRequestExecutor();
		String ret=getExecutor.execute(HttpClientConfig.getHttpClient(), null, "http://www.baidu.com", "");
		System.out.println(ret);
		String ret1=postExecutor.execute(HttpClientConfig.getHttpClient(), null, "http://www.hao123.com", "");
		System.out.println(ret1);
	}
}
