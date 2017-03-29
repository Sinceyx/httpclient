package com.other.testhttp;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

public class MulitTest {
	private static AtomicInteger ai=new AtomicInteger();
	
	public static void main(String[] args) throws IOException {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		BoundRequestBuilder[] wechatArray = new BoundRequestBuilder[10];
		BoundRequestBuilder[] posArray = new BoundRequestBuilder[10];
		BoundRequestBuilder[] appArray = new BoundRequestBuilder[10];
		for (int i = 0; i < 1; i++) {
			wechatArray[i] = asyncHttpClient.prepareGet(
					//"http://10.10.111.174:9000/a?app=2&p=8&ts=1471741331515&did=4C577482&bid=8618232375&test=1");
					"http://www.baidu.com");
			posArray[i] = asyncHttpClient.prepareGet(
					"http://10.10.111.174:9000/a?app=0&ts=1471741331515&p=1&did=4C577482&bid=8618232375&test=1");
			appArray[i] = asyncHttpClient.prepareGet(
					"http://10.10.111.174:9000/a?app=1&p=t&uid=13866666666&ip=192.168.1.54&net=2&did=123&os=ios&gps=39.512,83.123&ts=12092016054607");
		}
		for (int i=0;i<1;i++){
			wechatArray[i].execute(new AsyncCompletionHandler<Response>() {
				@Override
				public Response onCompleted(Response r) throws Exception {
					System.out.println(r.toString());
					return r;
				}
			});
		}
		asyncHttpClient.close();
		System.out.println("done");
	}
}
