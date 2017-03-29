package com.other.testhttp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ReadText {
	public static void readTxtFile(String filePath){
        try {
                String encoding="UTF-8";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    StringBuilder sb=new StringBuilder();
                    while((lineTxt = bufferedReader.readLine()) != null){
                        System.out.print(lineTxt);
                        sb.append(lineTxt);
                    }
                    System.out.println("=");
                    System.out.println(sb.toString().split(",").length);
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
     
    }
	 public static void main(String argv[]){
	        String filePath = "C:\\Users\\syq\\Desktop\\样本5000人csv.txt";
	        readTxtFile(filePath);
	    }
}
