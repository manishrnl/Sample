package org.example.university_management_system.Databases;

import java.sql.*;

import static com.mysql.cj.conf.PropertyKey.PASSWORD;
import static com.sun.javafx.css.FontFaceImpl.FontFaceSrcType.URL;
import static sun.net.ftp.FtpDirEntry.Permission.USER;


public class DatabaseConnection {

    private static Connection connection;

    // Database Connection for Offline / local SQL Database


    private static final String URL = "jdbc:mysql://localhost:3306" +
            "/university_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = "Manish@2009";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }



    //Database Connection for Online
/*
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://3tbb8zPdSv2Ka3W.root:XSzunTjvuQCG01yk@gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/test?sslMode=VERIFY_IDENTITY");
        }
*/


    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

/*

 public static Connection getConnection() throws SQLException {
          return DriverManager.getConnection("jdbc:mysql://3tbb8zPdSv2Ka3W.root:XSzunTjvuQCG01yk@gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/test?sslMode=VERIFY_IDENTITY");

 */
