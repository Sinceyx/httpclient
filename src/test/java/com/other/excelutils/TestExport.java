package com.other.excelutils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestExport {
	
	public static void testMulit(){
		String[] title = new String[]{"Title1","title2"};
		String[] rowsName1 = new String[]{"序号1","货物运输批次号1","提运单号","状态","录入人","录入时间"};
		String[] rowsName2 = new String[]{"序号2","货物运输批次号2","提运单号","状态","录入人","录入时间"};
		List<String[]> rowNameArray=new ArrayList<>();
		rowNameArray.add(rowsName1);
		rowNameArray.add(rowsName2);
		List<Object[]>  dataList1 = new ArrayList<Object[]>();
		Object[] objs = null;
		for (int i = 0; i <9; i++) {
			objs = new Object[rowsName1.length];
			objs[0] = 1;
			objs[1] = 2;
			objs[2] = 3;
			objs[3] = 4;
			objs[4] = 5;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = df.format(new Date());
			objs[5] = date;
			dataList1.add(objs);
		}
		List<Object[]>  dataList2 = new ArrayList<Object[]>();
		Object[] objs2 = null;
		for (int i = 0; i <9; i++) {
			objs2 = new Object[rowsName2.length];
			objs[0] = 21;
			objs[1] = 22;
			objs[2] = 23;
			objs[3] = 24;
			objs[4] = 25;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = df.format(new Date());
			objs[5] = date;
			dataList2.add(objs);
		}
		List<List<Object[]>>  dataLists=new ArrayList<>();
		dataLists.add(dataList1);
		dataLists.add(dataList2);
		
		ExportExcel ex=new ExportExcel(title,rowNameArray,dataLists);
		ex.exportMultipleSheet("D:\\exportMulit.xls", 2);
		System.out.println("done");
	}
	@Test
	public  void testList(){
		String[] title = new String[]{"Title1","title2"};
		String[] rowsName1 = new String[]{"期号","红1","红2","红3","红4","红5","红6","蓝"};
		String[] rowsName2 = new String[]{"期号","红1","红2","红3","红4","红5","红6","蓝"};
		List<String[]> rowNameArray=new ArrayList<>();
		rowNameArray.add(rowsName1);
		rowNameArray.add(rowsName2);
		
		List<String> list=Arrays.asList("2017020,31,12,04,08,33,10,10","2017019,25,04,12,06,23,08,08");
		System.out.println(list.toString());
		List<Object[]>  dataList1 = new ArrayList<Object[]>();
		dataList1.add(list.get(0).split(","));
		dataList1.add(list.get(1).split(","));
		List<Object[]>  dataList2 = new ArrayList<Object[]>();
		dataList2.add(list.get(0).split(","));
		dataList2.add(list.get(1).split(","));
		
		List<List<Object[]>>  dataLists=new ArrayList<>();
		dataLists.add(dataList1);
		dataLists.add(dataList2);
		ExportExcel ex=new ExportExcel(title,rowNameArray,dataLists);
		ex.exportMultipleSheet("D:\\exportMulitlist.xls", 2);
		System.out.println("done");
	}
	public static void testMap(){
		Map<String, Integer> map = new HashMap<>();
		map.put("01", 10);
		map.put("02", 11);
	}
}
