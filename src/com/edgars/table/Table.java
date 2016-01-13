package com.edgars.table;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Edgars on 09/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public abstract class Table {

    private int bookId = 0;

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }

    public abstract void save() throws IOException, SQLException, ClassNotFoundException;

    public abstract void delete();

}
