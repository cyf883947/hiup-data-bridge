package com.djhu;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 14:28
 **/
public class JdbcTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {


        JdbcTemplate jdbcTemplate = new JdbcTemplate();


        //加载驱动程序
        Class.forName("com.mysql.jdbc.Driver");
        //定义连接数据库的参数
        String url = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=utf-8";
        String user = "admin";
        String password = "admin";
        //获取数据库连接对象
        Connection connection = DriverManager.getConnection(url, user, password);
        //通过connection获取操作类
        Statement statement = connection.createStatement();
        //接收查询数据
        ResultSet resultSet = statement.executeQuery("select * from sys_role");
        //遍历resultSet打印数据
        while(resultSet.next()) {
            System.out.println(resultSet.getString("role")+"---"+resultSet.getString("description"));
        }

        //关闭连接资源
        resultSet.close();
        statement.close();
        connection.close();
    }

}
