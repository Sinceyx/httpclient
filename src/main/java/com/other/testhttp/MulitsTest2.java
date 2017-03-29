package com.other.testhttp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MulitsTest2 {
	 private static AtomicInteger ai=new AtomicInteger();
	
	public static void main(String[] args) throws Exception {
		List<HttpGet> list=new ArrayList<>();
		for(int i=0;i<500;i++){
			list.add(new HttpGet("http://10.10.111.174:9000/a?app=2&p=8&did=4C577482&bid=8618232375&ts=1471741331515&test=1"));
			list.add(new HttpGet("http://10.10.111.174:9000/a?app=1&p=t&did=123&uid=13866666666&ip=192.168.1.54&net=2&os=ios&gps=39.512.83.123&ts=12092016054607"));
			list.add(new HttpGet("http://10.10.111.174:9000/a?app=0&p=1&did=4C577482&bid=8618232375&ts=1471741331515&test=1"));
		}
		try {
			
			executeBatchGET(list);
			System.out.println("done:"+ai.get());
		} catch (ClientProtocolException e) {
			System.out.println(e.getStackTrace());
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}
		
	}
	private static void executeBatchGET(List<HttpGet> httpGetList) throws ClientProtocolException, IOException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			//HttpGet httpget = new HttpGet("http://10.10.111.174:9000/a?app=2&p=8&ts=1471741331515&did=4C577482&bid=8618232375&test=1");
			for (HttpGet httpGet : httpGetList) {
				
				 httpclient.execute(httpGet,new ResponseHandler<Object>(){
					@Override
					public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
						System.out.println("request: " + httpGet.getRequestLine());
						System.out.println(response.getStatusLine());
						if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
							ai.incrementAndGet();
						};
						HttpEntity entity = response.getEntity();						
						System.out.println(EntityUtils.toString(entity));						
						return null;
					}});

			}
			
		} finally {
			httpclient.close();
		}
	}
}
