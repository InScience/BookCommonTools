package com.edgars.table;

import java.util.ArrayList;

/**
 * Static class which hold ArrayLists of categories and authors to pass them to a book.
 *
 * Created by Edgars on 17/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class AuthorCategorieLists {
    public static class AuthorCategoriesIds {

        /**
         * ArrayList of authors.
         */
        private static ArrayList authors;
        /**
         * ArrayList of categories.
         */
        private static ArrayList categories;

        /**
         * getter to get categories ArrayList.
         *
         * @return categories ArrayList.
         */
        public static ArrayList getCategories() {
            return categories;
        }

        /**
         * Setting categories ids to an array.
         *
         * @param categories category id.
         */
        public static void setCategories(int categories) {
            if (AuthorCategoriesIds.categories == null) AuthorCategoriesIds.categories = new ArrayList();
            AuthorCategoriesIds.categories.add(categories);
        }

        /**
         * getter to get authors ArrayList.
         *
         * @return authors ArrayList.
         */
        public static ArrayList getAuthors() {
            return authors;
        }

        /**
         * Setting authors ids to an array.,
         *
         * @param authors author id.
         */
        public static void setAuthors(int authors) {
            if (AuthorCategoriesIds.authors == null) AuthorCategoriesIds.authors = new ArrayList();
            AuthorCategoriesIds.authors.add(authors);
        }

        /**
         * Cleans arrays setting them to null.
         */
        public static void clearData() {
            authors = null;
            categories = null;
        }
    }
}
