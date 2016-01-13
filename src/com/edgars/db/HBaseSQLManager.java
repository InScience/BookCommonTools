package com.edgars.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class to work with HBase through Apache Phoenix.
 *
 * Created by Edgars on 27/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class HBaseSQLManager {

    /**
     * Connection object.
     */
    private static Connection connection = null;

    /**
     * SQL statement.
     */
    public Statement statement;

    /**
     * Result of what HBase retuned.
     */
    private ResultSet resultSet;

    /**
     * Connection singleton.
     *
     * @return Connection.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:phoenix:158.129.140.188:2181:/hbase-unsecure");
        }

        return connection;
    }

    /**
     * Execute simple SQL.
     *
     * @param query SQL query.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void executeSql(String query) throws SQLException, ClassNotFoundException {
        statement = getConnection().createStatement();
        statement.execute(query);
        getConnection().commit();
    }

    /**
     * Execute SQL query and get results.
     *
     * @param query SQL query.
     * @return data from HBase.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ResultSet executeSqlGetString(String query) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        getConnection().commit();
        return resultSet;
    }

    /**
     * Execute SQL query. Normally returns ID of just inserted column.
     * Must be used when doing UPSERT (insert/update).
     *
     * @param query SQL query.
     * @return ID of just inserted column.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int executeSqlGetIdOnUpdate(String query) throws SQLException, ClassNotFoundException {
        int id = 0;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
            id = preparedStatement.executeUpdate();
            getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Close connection.
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void close() throws SQLException, ClassNotFoundException {
        getConnection().close();
    }

}
