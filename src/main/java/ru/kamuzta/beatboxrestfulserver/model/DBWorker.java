package ru.kamuzta.beatboxrestfulserver.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBWorker {
    private static final String URL = "jdbc:mysql://localhost:3306/kamuzta?useUnicode=true&serverTimezone=UTC&useSSL=false";
    private static final String USERNAME = "kamuzta";
    private static final String PASSWORD = "kamuzta";

    private Connection connection;

    public DBWorker() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
