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
 * The type Algorithm.
 */
public class Algorithm {

    /**
     * The Recommender. From MyMediaLiteJava library.
     */
    protected org.mymedialite.IRecommender recommender;

    /**
     * The Ignore list.
     */
    protected List ignoreList;

    /**
     * The Attribute item list.
     */
    protected List attributeItemList;

    /**
     * The History item list.
     */
    protected List historyItemList;

    /**
     * The Rating item list.
     */
    protected List ratingItemList;

    /**
     * The Model path.
     */
    protected String modelPath;

    /**
     * The Candidate list.
     */
    protected ArrayList<Integer> candidateList;

    /**
     * The Visitor id.
     */
    protected int visitorId;

    /**
     * The Count.
     */
    protected int count;

    /**
     * The Result set.
     */
    ResultSet resultSet = null;

    /**
     * The H base sql manager.
     */
    HBaseSQLManager hBaseSQLManager = new HBaseSQLManager();

    /**
     * The User mapping.
     */
    EntityMapping userMapping = new EntityMapping();
    /**
     * The Item mapping.
     */
    EntityMapping itemMapping = new EntityMapping();

    /**
     * Instantiates a new Algorithm.
     *
     * @throws SQLException the sql exception
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

    /**
     * Initialize data sets.
     */
    protected void initializeDataSets() {

    }

    /**
     * Sets recommender.
     *
     * @param recommender the recommender
     */
    public void setRecommender(org.mymedialite.IRecommender recommender) {
        this.recommender = recommender;
    }

    /**
     * Laod visitor model.
     *
     * @throws IOException the io exception
     */
    public void laodVisitorModel() throws IOException {
        recommender.loadModel("file");
    }

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
     * Gets recommendation.
     *
     * @return the recommendation
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
     * Gets reader.
     *
     * @return the reader
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
                            "WHERE B.ID IN (SELECT CART FROM VISITOR_LOG) GROUP BY B.ID, V.ID"
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
