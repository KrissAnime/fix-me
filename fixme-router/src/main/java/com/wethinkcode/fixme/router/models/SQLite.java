package com.wethinkcode.fixme.router.models;

import java.io.File;
import java.sql.*;

public class SQLite {
    public SQLite() {
        try {

//            connect();
//            PrintDirectory("./");
            CreateTables();
//            System.out.println("After ");
//            CreateTables();
        } catch (Exception e) {
            System.out.println("An exception occurred");
            e.printStackTrace();
        }
    }

    public void SaveTransaction(FIXMessage fixMessage) throws SQLException {
        Connection connection = connect();
        String query = "INSERT INTO `TRANSACTIONS` (`fix_message`) VALUE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setString(1, fixMessage.toString());

        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }
    // Date format convert(varchar, getdate(), 20) => 2006-12-30 00:38:54

    private void CreateTables() throws SQLException {
        Connection connection = connect();

        System.out.println("Connection created");

        Statement statement = connection.createStatement();

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `TRANSACTIONS` (" +
                "`time_stamp` DATETIME," +
                "`fix_message` VARCHAR(256) NOT NULL )");

        statement.close();
        connection.close();
    }

    private Connection connect() throws SQLException {
//        System.out.println("Before ");
//        PrintDirectory("./");
        String url = "jdbc:sqlite:fixme.db";
//        Connection conn = null;
//        conn = DriverManager.getConnection(url);
//        String url = "jdbc:sqlite:fixme.db";
//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection(url);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return conn;

        return DriverManager.getConnection(url);
    }

    private void PrintDirectory(String filepath) {
        File folder = new File(filepath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }
}
