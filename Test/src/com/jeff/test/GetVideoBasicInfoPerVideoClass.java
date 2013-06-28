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


//����ͳ�Ƶ���������Ƶ��uv ��vv�������Ϣ����Ҫ��vv���з���
public class GetVideoBasicInfoPerVideoClass {

	/**
	 * @param args
	 */
	
	//���ڴ���ÿһ�������videoid��Ϣ
	private static ArrayList<Integer> 	videoidPerDay 		= new ArrayList<Integer>();
	private static ArrayList<Integer> 	vvInfoPerVideoid 	= new ArrayList<Integer>();
	private static ArrayList<Long> 		vvPerVideoid 		= new ArrayList<Long>();
	private static ArrayList<Long> 		uvPerVideoid 		= new ArrayList<Long>();
	private static ArrayList<Double> 	cvPerVideoid 		= new ArrayList<Double>();
	
	//���ڴ���ĳһvideoid������cookie
	private static ArrayList<String> 	cookiePerVideoid 	= new ArrayList<String>();
	private static ArrayList<Integer> 	vvPerCookie 		= new ArrayList<Integer>();
	
	// ���ڴ���ÿһ��cookie��Ϣ
	private static int preDatecol;
	private static int currentDatecol;
	private static int preVideoid;
	private static int currentVideoid;
	private static String preCookie;
	private static String currentCookie;
	private static String preIp;
	private static String currentIp;
	private static int vvSingleCookie;
	
	
	private static long vvTotalPerVideoid;					//ÿ��vidoid��vv�ܺ�
	private static long	vvTotalPerDay;
	private static int  cookieNumPerVideoid;
	
	/*
	 * ���þ�̬�����ĳ�ʼֵ 
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
		
		//��ʼ��
		setDefaultValue();
			
		/*����ÿһ��������*/
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
			// ����һЩ�����õı���
			String strBefore;
			String strAfter;
			String[] splitStr = new String[7];				//ÿ������ֶ�ֵΪ7��
															//datecol, videoid, ip, cookie, officiallistid, vvpercookie, tstotal
			DecimalFormat dh = new DecimalFormat("0.0000"); // ������λС��
			
			/* ����ÿһ����¼ */
			while ((strBefore = input.readLine()) != null) {
				//��SOH�ַ�ת��Ϊ�ո�
				strAfter = strBefore.replace(String.valueOf((char)1)," ");
				splitStr = strAfter.split(" ");
				
				//��ȡ�����ֶ�
				currentDatecol 	= Integer.valueOf(splitStr[0]);
				currentVideoid 	= Integer.valueOf(splitStr[1]);
				currentIp 		= splitStr[2];
				currentCookie 	= splitStr[3];
				vvSingleCookie  = Integer.valueOf(splitStr[5]);
				if (currentDatecol == preDatecol && currentVideoid == preVideoid 
						&& !currentCookie.equals(preCookie)){		//����ͬһ��videoid��ͬ��cookie
					cookiePerVideoid.add(currentCookie);	//��¼��ǰ��cookie�����Ƽ���Ӧ��vv��
					vvPerCookie.add(vvSingleCookie);
					vvTotalPerVideoid += vvSingleCookie;
				}else{
					if(currentDatecol != preDatecol){
						//��ͬ�����ڣ���ʱû����صĴ���
						preDatecol = currentDatecol;
						//���preDatecol��ص���Ϣ
						//���ò�����current Datecol;
						//���ò����� current cookie
						
					}
					if(currentVideoid == preVideoid ){
						//��ͬ��videoid��ͬ��cookie
						cookiePerVideoid.add(currentCookie);
					}else{//��ͬ��videoid
						//output pre cookie �����Ϣ
						if(cookiePerVideoid.size()>0){
							Collections.sort(vvPerCookie);
							vvAver = OwnStatisticsMethodClass.getAverage(vvPerCookie);
							vvSD = OwnStatisticsMethodClass.getStandardDiviation(vvPerCookie);
							vvCV = vvSD/vvAver;
							//���������Ϣ
							videoidPerDay.add(preVideoid);
							vvPerVideoid.add(vvTotalPerVideoid);
							uvPerVideoid.add(Long.valueOf(cookiePerVideoid.size()));
							cvPerVideoid.add(vvCV);
							cookieNumPerVideoid = cookiePerVideoid.size();
							//������
							output.write((
									preVideoid + " " + 
									vvTotalPerVideoid + " " + 
									dh.format(Long.valueOf(cookiePerVideoid.size())) + " " + 
									cookieNumPerVideoid + " " + 
									dh.format(vvCV) + "\n"
									).getBytes("UTF-8"));
						}
						//���ò����� current cookie
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
					
			//�������һ��
			//����current videoid
			if(cookiePerVideoid.size()>0){
				Collections.sort(vvPerCookie);
				vvAver = OwnStatisticsMethodClass.getAverage(vvPerCookie);
				vvSD = OwnStatisticsMethodClass.getStandardDiviation(vvPerCookie);
				vvCV = vvSD/vvAver; 
				//���������Ϣ
				videoidPerDay.add(preVideoid);
				vvPerVideoid.add(vvTotalPerVideoid);
				uvPerVideoid.add(Long.valueOf(cookiePerVideoid.size()));
				cvPerVideoid.add(vvCV);
				cookieNumPerVideoid = cookiePerVideoid.size();
				//������
				output.write((
						preVideoid + " " + 
						vvTotalPerVideoid + " " + 
						dh.format(Long.valueOf(cookiePerVideoid.size())) + " " + 
						cookieNumPerVideoid + " " + 
						dh.format(vvCV) + "\n"
						).getBytes("UTF-8"));
				
			}
			//����current datecol
			//δ����

			input.close();
			output.close();
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
