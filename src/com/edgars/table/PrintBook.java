package com.edgars.table;


import com.edgars.db.HBaseSQLManager;
import json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Edgars on 09/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class PrintBook extends Table {

    /**
     * Object to operate with HBase.
     */
    HBaseSQLManager hBaseSQLManager = new HBaseSQLManager();

    /**
     * Book's title.
     */
    private String title;

    /**
     * Book's cover.
     */
    private String cover;

    /**
     * Book's poster.
     */
    private String img;

    /**
     * Book's ISBN code.
     */
    private String isbn;

    /**
     * Book's old price (non-discounted).
     */
    private String oldPrice;

    /**
     * Book's current price.
     */
    private String price;

    /**
     * Book's publish date.
     */
    private String publishDate;

    /**
     * Book's publisher.
     */
    private String publisher;

    /**
     * Summary about the book.
     */
    private String summary;

    /**
     * Book's translator.
     */
    private String translator;

    /**
     * Url to book.
     */
    private String url;

    /**
     * Book's page count.
     */
    private String pages;

    /**
     * Contains results which returns HBase.
     */
    private ResultSet resultSet = null;

    /**
     * Constructor gets book by ID.
     *
     * @param id book ID.
     */
    public PrintBook(String id) {
        try {
            resultSet = hBaseSQLManager.executeSqlGetString("SELECT * FROM BOOK_STORE WHERE ID = " + id);
            if (resultSet.next()) {
                this.title = resultSet.getString("TITLE");
                this.cover = resultSet.getString("COVER");
                this.img = resultSet.getString("IMAGE");
                this.isbn = resultSet.getString("ISBN");
                this.oldPrice = resultSet.getString("OLD_PRICE");
                this.price = resultSet.getString("PRICE");
                this.pages = resultSet.getString("PAGES");
                this.publishDate = resultSet.getString("PUBLISH_DATE");
                this.publisher = resultSet.getString("PUBLISHER");
                this.summary = resultSet.getString("SUMMARY");
                this.translator = resultSet.getString("TRANSLATOR");
                this.url = resultSet.getString("URL");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (hBaseSQLManager.statement != null) try {
                hBaseSQLManager.statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Book's constructor.
     *
     * @param json JSONObject filled up in Parser.
     */
    public PrintBook(JSONObject json) throws SQLException {
        this.publisher = json.getString("publisher");
        this.publishDate = json.getString("publishDate");
        this.translator = json.getString("translator");
        this.cover = json.getString("cover");
        this.summary = json.getString("summary");
        this.title = json.getString("title");
        this.price = json.getString("price");
        this.oldPrice = json.getString("oldPrice");
        this.img = json.getString("img");
        this.pages = json.getString("pages");
        this.isbn = json.getString("isbn");
        this.url = json.getString("url");

        /**
         * Setting book ID if book is already in database.
         */
        try {
            resultSet = hBaseSQLManager.executeSqlGetString("SELECT ID FROM BOOK_STORE WHERE TITLE = '" + title + "'");
            while (resultSet.next()) {
                setBookId(resultSet.getInt("ID"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (hBaseSQLManager.statement != null) hBaseSQLManager.statement.close();
        }
    }

    /**
     * Saving book.
     *
     * @throws IOException
     */
    @Override
    public void save() throws IOException, SQLException, ClassNotFoundException {
        int bookId = getBookId();
        if (bookId == 0) {
            try {
                bookId = hBaseSQLManager.executeSqlGetIdOnUpdate(
                        "UPSERT INTO BOOK_STORE (ID, TITLE, COVER, IMAGE, ISBN, OLD_PRICE, PAGES, PRICE, PUBLISH_DATE, PUBLISHER, " +
                                "SUMMARY, TRANSLATOR, URL) " +
                                "VALUES(NEXT VALUE FOR BOOK_STORE.BOOK_SEQUENCE, '" + title + "', '" + cover + "', '" + img + "', " +
                                "'" + isbn + "'" + ", '" + oldPrice + "', '" + pages + "', " +
                                "'" + price + "', '" + publishDate + "', '" + publisher + "', " +
                                "'" + summary + "', '" + translator + "', '" + url + "')");
            } finally {
                if (hBaseSQLManager.statement != null) hBaseSQLManager.statement.close();
            }
        } else {
            hBaseSQLManager.executeSqlGetIdOnUpdate(
                    "UPSERT INTO BOOK_STORE (ID, TITLE, COVER, IMAGE, ISBN, OLD_PRICE, PAGES, PRICE, PUBLISH_DATE, PUBLISHER, SUMMARY, " +
                            "TRANSLATOR, URL) " +
                            "VALUES(" + bookId + ", '" + title + "', '" + cover + "', '" + img + "', " +
                            "'" + isbn + "'" + ", '" + oldPrice + "', '" + pages + "', " +
                            "'" + price + "', '" + publishDate + "', '" + publisher + "', " +
                            "'" + summary + "', '" + translator + "', '" + url + "')");
        }

        hBaseSQLManager.executeSql("DELETE FROM BOOK_CATEGORY WHERE BOOK_ID = " + getBookId());
        for (int i = 0; i < AuthorCategorieLists.AuthorCategoriesIds.getCategories().size(); i++) {
            hBaseSQLManager.executeSqlGetIdOnUpdate(
                    "UPSERT INTO BOOK_CATEGORY(ID, BOOK_ID, CATEGORY_ID) " +
                            "VALUES(NEXT VALUE FOR BOOK_CATEGORY.BOOK_CATEGORY_SEQUENCE, " + bookId + ", " +
                            "" + AuthorCategorieLists.AuthorCategoriesIds.getCategories().get(i) + ")");
        }

        hBaseSQLManager.executeSql("DELETE FROM BOOK_AUTHOR WHERE BOOK_ID = " + getBookId());
        for (int i = 0; i < AuthorCategorieLists.AuthorCategoriesIds.getAuthors().size(); i++) {
            hBaseSQLManager.executeSqlGetIdOnUpdate(
                    "UPSERT INTO BOOK_AUTHOR(ID, BOOK_ID, AUTHOR_ID) " +
                            "VALUES(NEXT VALUE FOR BOOK_AUTHOR.BOOK_AUTHOR_SEQUENCE, " + bookId + ", " +
                            "" + AuthorCategorieLists.AuthorCategoriesIds.getAuthors().get(i) + ")");
        }

        AuthorCategorieLists.AuthorCategoriesIds.clearData();
        hBaseSQLManager.close();
    }

    @Override
    public void delete() {

    }

    public HBaseSQLManager gethBaseSQLManager() {
        return hBaseSQLManager;
    }

    public void sethBaseSQLManager(HBaseSQLManager hBaseSQLManager) {
        this.hBaseSQLManager = hBaseSQLManager;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }
}
