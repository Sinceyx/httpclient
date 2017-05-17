package com.other.testhttp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HttpPostWithJSON {
	public static String httpPostWithJSON(String url) throws Exception {

        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        String respContent = null;
        
//        json方式
        JSONObject jsonParam = new JSONObject();  
        jsonParam.put("amount", 10);
        jsonParam.put("channelCustomerNo", "93363DCC6064869708F1F3C72A0CE72A713A9D425CD50CDE");        
        //String jsonParam="{\"amount\":10,\"channelCustomerNo\":\"93363DCC6064869708F1F3C72A0CE72A713A9D425CD50CDE\",\"checker\":\"测试\"}";
        StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题    
        entity.setContentEncoding("UTF-8");    
        entity.setContentType("application/json");    
        httpPost.setEntity(entity);
        
        HttpResponse resp = client.execute(httpPost);
        if(resp.getStatusLine().getStatusCode() == 200) {
            HttpEntity he = resp.getEntity();
            respContent = EntityUtils.toString(he,"UTF-8");
        }
        return respContent;
    }

    
    public static void main(String[] args) throws Exception {
        String result = httpPostWithJSON("http://localhost:8080/dzfp/test");
        System.out.println(result);
    }
}
