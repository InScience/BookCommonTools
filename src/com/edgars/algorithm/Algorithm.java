package com.edgars.algorithm;

import com.edgars.db.HBaseSQLManager;
import org.apache.hadoop.hbase.util.Pair;
import org.mymedialite.data.EntityMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Algorithm is main class to work with recommendations.
 * Contains all lists and returns recommendations.
 * <p/>
 * Created by Edgars on 09/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class Algorithm {

    /**
     * IRecommender object from MyMediaLiteJava library.
     */
    protected org.mymedialite.IRecommender recommender;

    /**
     * List of ignored items.
     */
    protected List ignoreList;

    /**
     * List of item attributes.
     */
    protected List attributeItemList;

    /**
     * History of items.
     */
    protected List historyItemList;

    /**
     * List of item ratings.
     */
    protected List ratingItemList;

    /**
     * Path to model.
     */
    protected String modelPath;

    /**
     * List of all books original Id's from Hbase.
     */
    protected ArrayList<Integer> candidateList;

    /**
     * Visitor's unique id.
     */
    protected int visitorId;

    /**
     * Item count to return.
     */
    protected int count;

    /**
     * ResultSet is what SELECT returns.
     */
    ResultSet resultSet = null;

    /**
     * Initialize HBaseSQLManager.
     */
    HBaseSQLManager hBaseSQLManager = new HBaseSQLManager();

    /**
     * Mappings..
     */
    EntityMapping userMapping = new EntityMapping();
    EntityMapping itemMapping = new EntityMapping();

    /**
     * Constructor. Filling up candidateList from all records in BOOK_STORE.
     *
     * @throws SQLException
     */
    public Algorithm() throws SQLException {
        candidateList = new ArrayList<>();
        try {
            resultSet = hBaseSQLManager.executeSqlGetString("SELECT ID FROM BOOK_STORE");
            while (resultSet.next()) {
                int originalId = resultSet.getInt("ID");
                candidateList.add(originalId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (hBaseSQLManager.statement != null) hBaseSQLManager.statement.close();
        }
    }

    protected void initializeDataSets() {

    }

    /**
     * Method sets recommender type.
     *
     * @param recommender
     */
    public void setRecommender(org.mymedialite.IRecommender recommender) {
        this.recommender = recommender;
    }

    /**
     * Load MODEL.
     *
     * @throws IOException
     */
    public void laodVisitorModel() throws IOException {
        recommender.loadModel("file");
    }

    /**
     * Custom comparator to sort list of pairs.
     *
     * @return comparator.
     */
    private Comparator<Pair<Integer, Double>> getComparator() {

        Comparator comparator = new Comparator<Pair<Integer, Double>>() {

            @Override
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                return o1.getSecond().compareTo(o2.getSecond());
            }

        };

        return comparator;
    }

    /**
     * Method invokes from Servlet.
     * Making original ID's to internal, after getting recommendation list,
     * making them back to original ID's.
     *
     * @return orderedItems of recommended items.
     */
    public List<Pair<Integer, Double>> getRecommendation() {
        ArrayList internalCandidateItemList = new ArrayList();

        /**
         * Problem with IntList, will add items one by one.
         * Candidate item ID's list to internal ID's.
         */
        for (int i = 0; i < candidateList.size(); i++) {
            internalCandidateItemList.add(itemMapping.toInternalID(candidateList.get(i).toString()));
        }

        /**
         * Visitor ID also made as internal ID.
         */
        this.visitorId = userMapping.toInternalID(Integer.toString(visitorId));
        //getRecommendation(visitorId, count, internalCandidateItemList, ignoreList);
        List originalOrderedItemIds = new ArrayList();
        originalOrderedItemIds = new ArrayList();

        List<Pair<Integer, Double>> internalOrderedItems = getRecommendation(visitorId, count, internalCandidateItemList, ignoreList);

        /**
         * Making recommendation ID's back to original ID's.
         * Cropping out symbols.
         */
        for (int i = 0; i < internalOrderedItems.size(); i++) {
            String[] b = internalOrderedItems.get(i).toString().split(",");
            originalOrderedItemIds.add(b[0].replace("{", "").replace("{", ""));
        }
        originalOrderedItemIds = itemMapping.toInternalID(originalOrderedItemIds);

        return originalOrderedItemIds;
    }

    /**
     * Method to get recommendations into a list.
     *
     * @param internalVisitorId         internal id of visitor.
     * @param n                         Count of recommendations to get. If -1 then as much has possible.
     * @param internalCandidateItemList List of internal items which can be recommended.
     * @param ignoreList                List of items to not recommend/
     * @return Returns list of pairs (item id : item score).
     */
    private List<Pair<Integer, Double>> getRecommendation(int internalVisitorId, int n, ArrayList internalCandidateItemList, List
            ignoreList) {
        if (internalCandidateItemList == null) {
            internalCandidateItemList = new ArrayList<>();
        }
        if (ignoreList == null) {
            ignoreList = new ArrayList<>();
        }

        List<Pair<Integer, Double>> orderedItems;
        if (n == -1) {
            List<Pair<Integer, Double>> scoredItems = new ArrayList<>();
            for (Object itemId : internalCandidateItemList) {
                int item = Integer.parseInt(itemId.toString());
                if (!ignoreList.contains(itemId)) {
                    Double score = recommender.predict(internalVisitorId, item);
                    if (score > Float.MIN_VALUE) {
                        scoredItems.add(new Pair<>(item, score));
                    }
                    ignoreList.add(itemId);
                }
            }
            Collections.sort(scoredItems, getComparator());
            orderedItems = scoredItems;
        } else {
            Comparator comparer = getComparator();
            List<Pair<Integer, Double>> heap = new ArrayList<>();
            Float minRelevantScore = Float.MIN_VALUE;

            for (Object itemId : internalCandidateItemList) {
                if (!ignoreList.contains(itemId)) {
                    int item = Integer.parseInt(itemId.toString());
                    Double score = recommender.predict(internalVisitorId, item);
                    if (score > minRelevantScore) {
                        heap.add(new Pair<>(item, score));
                        if (heap.size() > n) {
                            int min = heap.indexOf(Collections.min(heap, comparer));
                            heap.remove(min);
                            min = heap.indexOf(Collections.min(heap, comparer));
                            String minString = Integer.toString(min);
                            minRelevantScore = Float.parseFloat(minString);
                        }
                    }
                    ignoreList.add(itemId);
                }
            }
            orderedItems = heap;
        }
        return orderedItems;
    }

    /**
     * Reader which gets ID's of visitors with related Book ID's.
     *
     * @return bufferedReader.
     */
    public BufferedReader getReader() {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            /**
             * This SQL is bad.
             */
            resultSet = hBaseSQLManager.executeSqlGetString(
                    "SELECT B.ID, V.ID FROM BOOK_STORE AS B, " +
                            "VISITOR_LOG AS VL " +
                            "INNER JOIN VISITOR AS V " +
                            "ON VL.MD5_ID = V.MD5_ID " +
                            "WHERE B.ID IN (SELECT CART FROM VISITOR_LOG)"
            );
            while (resultSet.next()) {
                String bookId = resultSet.getString("B.ID");
                String visitorId = resultSet.getString("V.ID");
                stringBuilder.append(visitorId + " " + bookId + "\n");
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

        BufferedReader bufferedReader = new BufferedReader(new StringReader(stringBuilder.toString()));

        return bufferedReader;
    }

}
