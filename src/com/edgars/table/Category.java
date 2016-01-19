package com.edgars.table;

import com.edgars.db.HBaseSQLManager;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Edgars on 09/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class Category extends BookDecorator {

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
     * Category name.
     */
    private String category;
    /**
     * Category's data are put into JSON to later work with.
     */
    //private JSONObject json = new JSONObject();

    /**
     * This constructor used in case, if no categories were found at book.
     *
     * @param table
     */
    public Category(Table table) {
        this.table = table;
        this.category = null;
    }

    /**
     * This constructor is used if categories were found, following decorator pattern.
     *
     * @param table Table type object passed to create decorator pattern logic.
     * @param name  Category name.
     */
    public Category(Table table, String name) {
        this.table = table;
        this.category = name;
    }

    /**
     * Saving category.
     *
     * @throws IOException
     */
    public void save() throws IOException, SQLException, ClassNotFoundException {
        int categoryId = 0;
        try {
            resultSet = hBaseSQLManager.executeSqlGetString("SELECT ID FROM CATEGORIES WHERE CATEGORY = '" + category + "'");
            if (resultSet.next()) {
                categoryId = resultSet.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (hBaseSQLManager.statement != null) hBaseSQLManager.statement.close();
        }

        if (categoryId == 0) {
            System.out.println("if categoryId == 0");
            try {
                categoryId = hBaseSQLManager.executeSqlGetIdOnUpdate(
                        "UPSERT INTO CATEGORIES(ID, CATEGORY) VALUES(NEXT VALUE FOR CATEGORIES.CATEGORIES_SEQUENCE, '" + category + "')");
            } finally {
                if (hBaseSQLManager.statement != null) hBaseSQLManager.statement.close();
            }
        } else {
            hBaseSQLManager.executeSqlGetIdOnUpdate("UPSERT INTO CATEGORIES(ID, CATEGORY) VALUES(" + categoryId + ", '" + category + "')");
        }

        AuthorCategorieLists.AuthorCategoriesIds.setCategories(categoryId);

        // Saving table.
        if (table instanceof Table) {
            table.save();
        }
    }
}
