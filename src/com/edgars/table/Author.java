package com.edgars.table;


import com.edgars.db.HBaseSQLManager;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author class works with data about authors.
 * <p/>
 * Created by Edgars on 09/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class Author extends BookDecorator {

    /**
     * Object to operate with HBase.
     */
    HBaseSQLManager hBaseSQLManager = new HBaseSQLManager();

    /**
     * Contains results which returns HBase.
     */
    private ResultSet resultSet = null;

    /**
     * Table variable.
     */
    private Table table;
    /**
     * Author's name.
     */
    private String name;
    /**
     * Author's surname.
     */
    private String surname;
    /**
     * Author's data are put into JSON to later work with.
     */
    // private JSONObject json = new JSONObject();

    /**
     * This constructor used in case, if no authors were found at book.
     *
     * @param table
     */
    public Author(Table table) {
        this.table = table;
        this.name = null;
        this.surname = null;
    }

    /**
     * This constructor is used if authors were found, following decorator pattern.
     *
     * @param table   Table type object passed to create decorator pattern logic.
     * @param name    author's name.
     * @param surname author's surname.
     */
    public Author(Table table, String name, String surname) {
        this.table = table;
        this.name = name;
        this.surname = surname;
    }

    /**
     * Saving author.
     *
     * @throws IOException
     */
    public void save() throws IOException, SQLException, ClassNotFoundException {
        HBaseSQLManager hBaseSQLManager = new HBaseSQLManager();
        int authorid = 0;
        try {
            resultSet = hBaseSQLManager.executeSqlGetString("SELECT ID FROM AUTHORS WHERE SURNAME = '" + surname + "'");
            if (resultSet.next()) {
                authorid = resultSet.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (hBaseSQLManager.statement != null) hBaseSQLManager.statement.close();
        }

        if (authorid == 0) {
            System.out.println("if authorId == 0");
            try {
                authorid = hBaseSQLManager.executeSqlGetIdOnUpdate(
                        "UPSERT INTO AUTHORS(ID, NAME, SURNAME) VALUES(NEXT VALUE FOR AUTHORS.AUTHORS_SEQUENCE, '" + name + "', '" +
                                surname + "')");
            } finally {
                if (hBaseSQLManager.statement != null) hBaseSQLManager.statement.close();
            }
        } else {
            hBaseSQLManager.executeSqlGetIdOnUpdate("UPSERT INTO AUTHORS(ID, NAME, SURNAME) VALUES(" + authorid + ", '" + name + "', '" +
                    surname + "')");
        }

        System.out.println("AUTHOR ID: " + authorid);
        AuthorCategorieLists.AuthorCategoriesIds.setAuthors(authorid);


        // Saving table.
        if (table instanceof Table) {
            table.save();
        }
    }
}

