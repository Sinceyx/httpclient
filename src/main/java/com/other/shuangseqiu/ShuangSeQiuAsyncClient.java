package com.other.shuangseqiu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.other.excelutils.ExportExcel;

public class ShuangSeQiuAsyncClient {
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		//15页以后规则出现变化，暂不考虑
		int pageNum = 2;

		List<String> rowsList = getAllpages(pageNum);
		Map<String, Integer> redCountMap = new HashMap<>();
		Map<String, Integer> blueCountMap = new HashMap<>();
		initMap(rowsList, redCountMap, blueCountMap);

		System.out.println(redCountMap.toString());
		System.out.println("==========redCountMap===========");
		Map<String, Integer> redSortMap=orderCount(redCountMap);
		System.out.println("==========blueCountMap==========");
		System.out.println(blueCountMap.toString());
		Map<String, Integer> blueSortMap=orderCount(blueCountMap);
		
		exportExcel(rowsList, redSortMap, blueSortMap);

	}
	private  static void exportExcel(List<String> rowsList,Map<String, Integer> redSortMap,Map<String, Integer> blueSortMap ){
		
		String[] rowsName1 = new String[]{"期号","红1","红2","红3","红4","红5","红6","蓝"};
		String[] rowsName2 = new String[]{"红球号码","出现次数排序","总出现次数"};
		String[] rowsName3 = new String[]{"篮球号码","出现次数排序","总出现次数"};
		List<String[]> rowNameArray=new ArrayList<>();
		rowNameArray.add(rowsName1);
		rowNameArray.add(rowsName2);
		rowNameArray.add(rowsName3);
		
		List<List<Object[]>>  dataAllLists=new ArrayList<>();
		List<Object[]>  dataList1 = getFormatListResult(rowsList);
		List<Object[]>  dataList2 = getFormatListResult(redSortMap);
		List<Object[]>  dataList3 = getFormatListResult(blueSortMap);
		dataAllLists.add(dataList1);
		dataAllLists.add(dataList2);
		dataAllLists.add(dataList3);
		Object[] first=dataList1.get(0);
		Object[] last=dataList1.get(dataList1.size()-1);
		String titleFirst=String.format("%s-%s期结果",last[0].toString(),first[0].toString());
		String[] titleArray = new String[]{titleFirst,"红球记录","篮球记录"};
		ExportExcel ex=new ExportExcel(titleArray,rowNameArray,dataAllLists);
		ex.exportMultipleSheet("D:\\dlt.xls", 3);
		
		
	}
	private static List<Object[]> getFormatListResult(Map<String, Integer> sortMap){
		List<Object[]>  dataList = new ArrayList<Object[]>();
	    Object[] objs = null;
	    int sortNum=1;
		for(Entry<String, Integer> e:sortMap.entrySet()){
			objs=new Object[3];
			objs[0]=e.getKey();
			objs[1]=sortNum++;
			objs[2]=e.getValue();
			dataList.add(objs);
		}
		return dataList;
	}
	private static List<Object[]> getFormatListResult(List<String> rowsList){
		List<Object[]>  dataList = new ArrayList<Object[]>();
		for(int i=0;i<rowsList.size();i++){
			dataList.add(rowsList.get(i).split(","));
		}
		dataList.sort(new Comparator<Object[]>(){		
			@Override
			public int compare(Object[] o1, Object[] o2) {				
				return Integer.valueOf(o2[0].toString())-Integer.valueOf(o1[0].toString());
			}});
		return dataList;
	}
	
	private static List<String> getAllpages(int pageNum) throws InterruptedException, ExecutionException, IOException {
		// http://www.cwl.gov.cn/kjxx/ssq/hmhz/
		// http://www.cwl.gov.cn/kjxx/ssq/hmhz/index_1.shtml
		// http://www.cwl.gov.cn/kjxx/ssq/hmhz/index_31.shtml

		List<String> rowsAllList = Collections.synchronizedList(new ArrayList<String>());
		BoundRequestBuilder[] brbArray = new BoundRequestBuilder[pageNum];
		CountDownLatch latch = new CountDownLatch(brbArray.length);
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		for (int i = 0; i < brbArray.length; i++) {
			if (i == 0) {
				brbArray[i] = asyncHttpClient.prepareGet("http://www.cwl.gov.cn/kjxx/ssq/hmhz/");
			} else {
				brbArray[i] = asyncHttpClient
						.prepareGet(String.format("http://www.cwl.gov.cn/kjxx/ssq/hmhz/index_%s.shtml", i));
			}

		}

		for (BoundRequestBuilder boundRequestBuilder : brbArray) {
			boundRequestBuilder.execute(new AsyncCompletionHandler<Response>() {

				@Override
				public Response onCompleted(Response r) throws Exception {
					Document doc = Jsoup.parse(r.getResponseBodyAsStream(), "UTF-8", "");
					Elements table = doc.select("table.hz");
					Document rowsDoc = Jsoup.parse(table.toString());
					Elements rows = rowsDoc.select("td[height=35],p.haoma");
					String[] rowsArray = rows.text().split("\\s+");
					rowsAllList.addAll(getListResult(rowsArray));
					System.out.println(rowsAllList.size());
					System.out.println(rowsAllList.toString());
					latch.countDown();
					return r;
				}

				@Override
				public void onThrowable(Throwable t) {
					latch.countDown();
					System.out.println(t.getStackTrace());
				}
			});
		}
		latch.await();
		asyncHttpClient.close();
		return rowsAllList;
	}

	private static List<String> getListResult(String[] rowsArray) {
		List<String> rowsList = new ArrayList<>();
		StringBuffer rowsStb = new StringBuffer();

		int count = 0;
		for (int i = 0; i < rowsArray.length; i++) {
			count++;
			rowsStb.append(rowsArray[i]);
			if (count != 8) {
				rowsStb.append(",");
			} else {
				rowsList.add(rowsStb.toString());
				rowsStb.setLength(0);
				count = 0;
			}
		}
		return rowsList;
	}

	private static void initMap(List<String> rowsList, Map<String, Integer> redCountMap,
			Map<String, Integer> blueCountMap) {
		for (int i = 1; i <= 33; i++) {
			String tmpKey = String.valueOf(i).length() == 2 ? String.valueOf(i)
					: String.format("0%s", String.valueOf(i));
			redCountMap.put(tmpKey, 0);
		}
		for (int i = 1; i <= 16; i++) {
			String tmpKey = String.valueOf(i).length() == 2 ? String.valueOf(i)
					: String.format("0%s", String.valueOf(i));
			blueCountMap.put(tmpKey, 0);
		}

		for (int i = 0; i < rowsList.size(); i++) {
			String tempList = rowsList.get(i);
			String[] tempArray = tempList.split(",");
			for (int j = 1; j < tempArray.length; j++) {
				if (j < tempArray.length - 1) {
					int tempValue = redCountMap.get(tempArray[j]) + 1;
					redCountMap.put(tempArray[j], tempValue);
				}
				if (j == tempArray.length - 1) {
					int tempValue = blueCountMap.get(tempArray[j]) + 1;
					blueCountMap.put(tempArray[j], tempValue);
				}
			}
		}
	}

	private static Map<String, Integer> orderCount(Map<String, Integer> map) {
		Map<String, Integer> retMap=new LinkedHashMap<>();
		String temp;
		String[] keyArray = new String[map.keySet().size()];
		map.keySet().toArray(keyArray);
		for (int i = 0; i < keyArray.length; i++) {
			for (int j = 0; j < keyArray.length - 1 - i; j++) {
				if (map.get(keyArray[j]) < map.get(keyArray[j + 1])) {
					temp = keyArray[j];
					keyArray[j] = keyArray[j + 1];
					keyArray[j + 1] = temp;
				}
			}
		}
		for (int i = 0; i < keyArray.length; i++) {
			retMap.put(keyArray[i], map.get(keyArray[i]));
			System.out.println(String.format("%s-%d", keyArray[i], map.get(keyArray[i])));
		}
		return retMap;
	}

}
