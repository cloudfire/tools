package com.youku.log.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

public class HDFSTest1 {

	 
	public static void WriteToHDFS(String file, String words)
			throws IOException, URISyntaxException {
		
		Configuration conf = new Configuration();
		
		FileSystem fs = FileSystem.get(URI.create(file), conf);
		Path path = new Path(file);
		FSDataOutputStream out = fs.create(path); 
		for (int i = 0; i < 10; i++) {
			System.out.println(words);
			out.write(words.getBytes("UTF-8"));
		}
		out.close();
	}

	public static void main(String[] args) throws IOException,
			URISyntaxException {
		 
		Configuration conf =new Configuration();
		System.out.println(conf.get("hadoop.security.authentication"));
		UserGroupInformation.loginUserFromKeytab("logc/10.103.9.61@DATA.YOUKU","/opt/hadoop-1.0.3/logc_keytab");
		String fileWrite = "hdfs://a001.namenode.hadoop.qingdao.youku:9000/user/logc/ok";
		String words = "This words is to write into file!\n";
		System.out.println(words);
		WriteToHDFS(fileWrite, words);
//	 
	}
}
