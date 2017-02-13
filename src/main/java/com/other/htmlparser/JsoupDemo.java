package com.other.htmlparser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class JsoupDemo {
	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.connect("http://www.baidu.com/").get();
		System.out.println(doc.toString());
	}
}
