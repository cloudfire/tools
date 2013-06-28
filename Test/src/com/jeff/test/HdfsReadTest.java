package com.jeff.test;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsReadTest {
	
	public static void main(String[] args) throws Exception {
		try {
		String dst = "hdfs://a01.namenode.hadoop.dm.b28.youku:9000/user/fujie/test";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		FSDataInputStream hdfsInStream = fs.open(new Path(dst));

		byte[] ioBuffer = new byte[1024];
		int readLen = hdfsInStream.read(ioBuffer);

		while(-1 != readLen){
		System.out.write(ioBuffer,0,readLen);
		readLen = hdfsInStream.read(ioBuffer);
		}
		hdfsInStream.close();
		fs.close();
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		finally
		{
		System.out.println("SUCCESS");
		}
		}

}
