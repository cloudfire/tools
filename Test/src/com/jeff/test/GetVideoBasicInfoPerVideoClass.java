package com.jeff.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


//用于统计单日所有视频的uv 及vv等相关信息，主要对vv进行分析
public class GetVideoBasicInfoPerVideoClass {

	/**
	 * @param args
	 */
	
	//用于处理每一天的所有videoid信息
	private static ArrayList<Integer> 	videoidPerDay 		= new ArrayList<Integer>();
	private static ArrayList<Integer> 	vvInfoPerVideoid 	= new ArrayList<Integer>();
	private static ArrayList<Long> 		vvPerVideoid 		= new ArrayList<Long>();
	private static ArrayList<Long> 		uvPerVideoid 		= new ArrayList<Long>();
	private static ArrayList<Double> 	cvPerVideoid 		= new ArrayList<Double>();
	
	//用于处理某一videoid的所有cookie
	private static ArrayList<String> 	cookiePerVideoid 	= new ArrayList<String>();
	private static ArrayList<Integer> 	vvPerCookie 		= new ArrayList<Integer>();
	
	// 用于处理每一条cookie信息
	private static int preDatecol;
	private static int currentDatecol;
	private static int preVideoid;
	private static int currentVideoid;
	private static String preCookie;
	private static String currentCookie;
	private static String preIp;
	private static String currentIp;
	private static int vvSingleCookie;
	
	
	private static long vvTotalPerVideoid;					//每个vidoid的vv总和
	private static long	vvTotalPerDay;
	private static int  cookieNumPerVideoid;
	
	/*
	 * 设置静态变量的初始值 
	 * 
	 */
	public static void setDefaultValue()
	{
		//per day
		if(!videoidPerDay.isEmpty())
			videoidPerDay.clear();
		if(!vvInfoPerVideoid.isEmpty())
			vvInfoPerVideoid.clear();
		if(!vvPerVideoid.isEmpty())
			vvPerVideoid.clear();
		if(!uvPerVideoid.isEmpty())
			uvPerVideoid.clear();
		//per videoid
		if(!cookiePerVideoid.isEmpty())
			cookiePerVideoid.clear();
		if(!vvPerCookie.isEmpty())
			vvPerCookie.clear();
		if(!cvPerVideoid.isEmpty())
			cvPerVideoid.clear();
		//per cookie
		preDatecol = 0;
		currentDatecol = 0;
		preVideoid = 0;
		currentVideoid = 0;
		preIp = "";
		currentIp = "";
		preCookie = "";
		currentCookie = "";
		vvSingleCookie = 0;
		vvTotalPerVideoid = 0;
		vvTotalPerDay = 0;
		cookieNumPerVideoid = 0;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String tmp = "20120906";
		//String inFileName = "videobasicinfodata\\" + tmp;
		//String outFileName = inFileName + "_replaced";
		double vvAver;
		double vvSD;
		double vvCV; 
		String inFileName = args[0];
		String outFileName = args[1];
		
		//初始化
		setDefaultValue();
			
		/*处理每一个数据行*/
		try {
			Configuration conf = new Configuration();
			conf.set("fs.default.name", "hdfs://a01.namenode.hadoop.dm.b28.youku:9000");
//			conf.set("hadoop.job.ugi", "wukaikai,wukaikai"); 
			
			FileSystem fs= FileSystem.get(conf);
			
			Path inFile = new Path(inFileName);
			
			FSDataInputStream fin = fs.open(inFile);
			BufferedReader input = new BufferedReader(new InputStreamReader(fin, "UTF-8"));
			Path outFile = new Path(outFileName);
			FSDataOutputStream output = fs.create(outFile);
			//FileOutputStream output = new FileOutputStream(outFileName);  
			// 定义一些处理用的变量
			String strBefore;
			String strAfter;
			String[] splitStr = new String[7];				//每行相关字段值为7个
															//datecol, videoid, ip, cookie, officiallistid, vvpercookie, tstotal
			DecimalFormat dh = new DecimalFormat("0.0000"); // 保留四位小数
			
			/* 处理每一条记录 */
			while ((strBefore = input.readLine()) != null) {
				//将SOH字符转换为空格
				strAfter = strBefore.replace(String.valueOf((char)1)," ");
				splitStr = strAfter.split(" ");
				
				//提取部分字段
				currentDatecol 	= Integer.valueOf(splitStr[0]);
				currentVideoid 	= Integer.valueOf(splitStr[1]);
				currentIp 		= splitStr[2];
				currentCookie 	= splitStr[3];
				vvSingleCookie  = Integer.valueOf(splitStr[5]);
				if (currentDatecol == preDatecol && currentVideoid == preVideoid 
						&& !currentCookie.equals(preCookie)){		//处理同一个videoid不同的cookie
					cookiePerVideoid.add(currentCookie);	//记录当前的cookie的名称及对应的vv数
					vvPerCookie.add(vvSingleCookie);
					vvTotalPerVideoid += vvSingleCookie;
				}else{
					if(currentDatecol != preDatecol){
						//不同的日期，暂时没有相关的处理
						preDatecol = currentDatecol;
						//输出preDatecol相关的信息
						//设置并处理current Datecol;
						//设置并处理 current cookie
						
					}
					if(currentVideoid == preVideoid ){
						//相同的videoid不同的cookie
						cookiePerVideoid.add(currentCookie);
					}else{//不同的videoid
						//output pre cookie 相关信息
						if(cookiePerVideoid.size()>0){
							Collections.sort(vvPerCookie);
							vvAver = OwnStatisticsMethodClass.getAverage(vvPerCookie);
							vvSD = OwnStatisticsMethodClass.getStandardDiviation(vvPerCookie);
							vvCV = vvSD/vvAver;
							//保存相关信息
							videoidPerDay.add(preVideoid);
							vvPerVideoid.add(vvTotalPerVideoid);
							uvPerVideoid.add(Long.valueOf(cookiePerVideoid.size()));
							cvPerVideoid.add(vvCV);
							cookieNumPerVideoid = cookiePerVideoid.size();
							//输出结果
							output.write((
									preVideoid + " " + 
									vvTotalPerVideoid + " " + 
									dh.format(Long.valueOf(cookiePerVideoid.size())) + " " + 
									cookieNumPerVideoid + " " + 
									dh.format(vvCV) + "\n"
									).getBytes("UTF-8"));
						}
						//设置并处理 current cookie
						cookiePerVideoid.clear();
						vvPerCookie.clear();
						cookiePerVideoid.add(currentCookie);
						vvPerCookie.add(vvSingleCookie);
						preVideoid = currentVideoid;
						preCookie = currentCookie;
						vvTotalPerVideoid = vvSingleCookie;
					}
				}
			}
					
			//处理最后一个
			//处理current videoid
			if(cookiePerVideoid.size()>0){
				Collections.sort(vvPerCookie);
				vvAver = OwnStatisticsMethodClass.getAverage(vvPerCookie);
				vvSD = OwnStatisticsMethodClass.getStandardDiviation(vvPerCookie);
				vvCV = vvSD/vvAver; 
				//保存相关信息
				videoidPerDay.add(preVideoid);
				vvPerVideoid.add(vvTotalPerVideoid);
				uvPerVideoid.add(Long.valueOf(cookiePerVideoid.size()));
				cvPerVideoid.add(vvCV);
				cookieNumPerVideoid = cookiePerVideoid.size();
				//输出结果
				output.write((
						preVideoid + " " + 
						vvTotalPerVideoid + " " + 
						dh.format(Long.valueOf(cookiePerVideoid.size())) + " " + 
						cookieNumPerVideoid + " " + 
						dh.format(vvCV) + "\n"
						).getBytes("UTF-8"));
				
			}
			//处理current datecol
			//未操作

			input.close();
			output.close();
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
