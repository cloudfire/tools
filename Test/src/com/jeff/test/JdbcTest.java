package com.jeff.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class JdbcTest {
	
	public static void main(String[] args){

        // ����������
        String driver = "com.mysql.jdbc.Driver";

        // URLָ��Ҫ���ʵ����ݿ���scutcs
        String url = "jdbc:mysql://127.103.23.127:3306/test";

        // MySQL����ʱ���û���
        String user = "root";

        // MySQL����ʱ������
        String password = "123";

        try { 
         // ������������
         Class.forName(driver);

         // �������ݿ�
         Connection conn = DriverManager.getConnection(url, user, password);

         if(!conn.isClosed()) 
          System.out.println("Succeeded connecting to the Database!");

         // statement����ִ��SQL���
         Statement statement = conn.createStatement();

         // Ҫִ�е�SQL���
         String sql = "select groupId from groupInfo where userId=?";

         // �����
         ResultSet rs = statement.executeQuery(sql);
         List<String> groups =new LinkedList<String>();
         while(rs.next()) {
          groups.add(rs.getString("groupId"));
         }

         rs.close();
         conn.close();

        } catch(ClassNotFoundException e) {


         System.out.println("Sorry,can`t find the Driver!"); 
         e.printStackTrace();


		} catch (SQLException e) {


         e.printStackTrace();


        } catch(Exception e) {


         e.printStackTrace();


        } 
} 

}
