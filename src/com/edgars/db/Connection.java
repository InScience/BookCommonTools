package com.edgars.db;

/**
 * Created by Edgars on 18/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public interface Connection {

    // Establish connection with database.
    public void connect();

    // Close connection with database.
    public void close();

    // Drop the table.
    public void drop();

}
