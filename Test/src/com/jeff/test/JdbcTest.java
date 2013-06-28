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

        // 驱动程序名
        String driver = "com.mysql.jdbc.Driver";

        // URL指向要访问的数据库名scutcs
        String url = "jdbc:mysql://127.103.23.127:3306/test";

        // MySQL配置时的用户名
        String user = "root";

        // MySQL配置时的密码
        String password = "123";

        try { 
         // 加载驱动程序
         Class.forName(driver);

         // 连续数据库
         Connection conn = DriverManager.getConnection(url, user, password);

         if(!conn.isClosed()) 
          System.out.println("Succeeded connecting to the Database!");

         // statement用来执行SQL语句
         Statement statement = conn.createStatement();

         // 要执行的SQL语句
         String sql = "select groupId from groupInfo where userId=?";

         // 结果集
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
