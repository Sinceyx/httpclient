package com.other.testhttp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class PostClient {
	//String menuUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={APPID}{APPID}&redirect_uri ="+URLEncoder.encode("http://jjjj.tunnel.qydev.com/mgr/cust/gotoBidding","UTF-8")+"&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
	private static String jsonStr="{\"1\":{\"button\":[{\"name\":\"11\",\"type\":\"view\",\"url\":\"{redirect_prefix}http%3A%2F%2Fwww.baidu.com{redirect_suffix}\"},{\"name\":\"12\",\"type\":\"view\",\"url\":\"{redirect_prefix}333333{redirect_suffix}\"},{\"name\":\"13\",\"type\":\"view\",\"url\":\"{redirect_prefix}44444{redirect_suffix}\"}],\"matchrule\":{\"tag_id\":\"100\"}},\"2\":{\"button\":[{\"name\":\"pt\",\"type\":\"view\",\"url\":\"{redirect_prefix}123{redirect_suffix}\"}]}}";
	private static String appid="123";
	private static String redirectPrefix="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri =";
	private static String redirectSuffix="&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
	public static void main(String[] args) throws UnsupportedEncodingException {
		String url = "http://wxkfkf.tunnel.qydev.com/doMenu2/createMenu";
		String charset = "UTF-8";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String result = null;		
		//String menuUrlformat=StringUtils.replaceEach(jsonStr,new String[]{"{redirect_suffix}", "{redirect_suffix}"}, new String[]{redirectPrefix, redirectSuffix});
		try {
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("json", jsonStr));
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.setEntity(entity);
			}
			HttpResponse response = httpclient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
					System.out.println(result);
				}
			}
		} catch (Exception e) {
			
		}finally{
			 try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
