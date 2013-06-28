package com.youku.log.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class test {
	
	public static List<String> getGroups(String user) throws IOException {
		List<String> groups = new LinkedList<String>();
		ResultSet rs = null;
		Connection conn = null;
		try {
		    Object ob=null;
		    ob.toString();
		} catch (Exception e) {
			System.out.println("connect to group db error");
		} finally {
			System.out.println("00000000000000000");

		}
		if (groups.size() == 0) {
			groups.add(user);
		}
		
		return groups;
	}
	
	
	public static void main(String[] args) throws IOException {
		List<String> test= getGroups("fujie");
		System.out.println(test.size());
	}

}
