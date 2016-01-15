package com.edgars.algorithm;

import org.mymedialite.data.IPosOnlyFeedback;
import org.mymedialite.eval.Items;
import org.mymedialite.io.ItemData;
import org.mymedialite.itemrec.IncrementalItemRecommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Edgars on 13/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class UserKNN extends Algorithm {

    /**
     * Path where model file is stored.
     */
    private static final String MODEL_FILE = System.getProperty("user.dir") + "/models/" + "UserKNNModel.txt";

    public UserKNN() throws SQLException {
        setRecommender(new org.mymedialite.itemrec.UserKNN());
        initializeDataSets();
        recommender.train();
        try {
            recommender.saveModel(MODEL_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor if visitor is known.
     *
     * @param visitorId Unique id of visitor.
     * @param count     Item count to return.
     * @throws SQLException
     */
    public UserKNN(int visitorId, int count) throws SQLException {
        this.visitorId = visitorId;
        this.count = count;
        setRecommender(new org.mymedialite.itemrec.UserKNN());
        initializeDataSets();
    }

    public UserKNN(int i) throws SQLException {

    }

    protected void initializeDataSets() {
    /*     ArrayList arrayList = getVisitorList();
       for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < arrayList.size(); j++) {
                if (!arrayList.get(i).equals(arrayList.get(j))) {
                    ((IUserSimilarityProvider) recommender).getUserSimilarity(Integer.parseInt(arrayList.get(i).toString()),
                            Integer.parseInt(arrayList.get(j).toString()));
                }
            }
            ((IncrementalItemRecommender) recommender).getMostSimilarUsers(Integer.parseInt(arrayList.get(i).toString()), 2);
        }*/
        //((IncrementalItemRecommender) recommender).addFeedback(visitorId, candidateList);

        // original visitor id mapping to internal id
        //this.visitorId = userMapping.toInternalID(Integer.toString(visitorId));

        IPosOnlyFeedback iPosOnlyFeedback = null;
        BufferedReader bufferedReader = getReader();
        try {
            iPosOnlyFeedback = ItemData.read(bufferedReader, userMapping, itemMapping, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * Setting feedback.
         */
        ((IncrementalItemRecommender) recommender).setFeedback(iPosOnlyFeedback);

        File file = new File(MODEL_FILE);
        try {
            if (file.exists()) {
                recommender.loadModel(MODEL_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

/*        Collection<Integer> candidate_items = iPosOnlyFeedback.allItems();  // items that will be taken into account in the evaluation
        Collection<Integer> test_users = iPosOnlyFeedback.allUsers();  // users that will be taken into account in the evaluation

        try {
            HashMap<String, Double> results = Items.evaluate(recommender, iPosOnlyFeedback, iPosOnlyFeedback, test_users, candidate_items);
            System.out.println("AUC       " + results.get("AUC"));
            System.out.println("MAP       " + results.get("MAP"));
            System.out.println("NDCG      " + results.get("NDCG"));
            System.out.println("prec@5    " + results.get("prec@5"));
            System.out.println("prec@10   " + results.get("prec@10"));
            System.out.println("prec@15   " + results.get("prec@15"));
            System.out.println("num_users " + results.get("num_users"));
            System.out.println("num_items " + results.get("num_items"));
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

/*    private ArrayList getVisitorList() {
        ArrayList arrayList = new ArrayList();
        try {
            resultSet = hBaseSQLManager.executeSqlGetString("SELECT ID FROM VISITOR");
            while (resultSet.next()) {
                arrayList.add(resultSet.getInt("ID"));
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

        return arrayList;
    }*/
}