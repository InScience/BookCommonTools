package com.edgars.algorithm;

import org.mymedialite.data.IPosOnlyFeedback;
import org.mymedialite.eval.Items;
import org.mymedialite.io.ItemData;
import org.mymedialite.itemrec.IncrementalItemRecommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

/**
 * MostPopular recommender.
 * <p/>
 * Created by Edgars on 13/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class MostPopular extends Algorithm {

    /**
     * Path where model file is stored.
     */
    private static final String MODEL_FILE = System.getProperty("user.dir") + "/models/" + "MostPopularModel.txt";
    private static final String TRAIN_MODEL_FILE = System.getProperty("user.dir") + "/scheduler/models/" + "MostPopularModel.txt";

    /**
     * Constructor if visitor is known.
     *
     * @param visitorId Unique id of visitor.
     * @param count     Item count to return.
     * @throws SQLException
     */
    public MostPopular(int visitorId, int count) throws SQLException {
        this.visitorId = visitorId;
        this.count = count;
        setRecommender(new org.mymedialite.itemrec.MostPopular());
        initializeDataSets();
    }

    public MostPopular() throws SQLException {
        setRecommender(new org.mymedialite.itemrec.MostPopular());
        initializeDataSets();
        recommender.train();
        try {
            recommender.saveModel(TRAIN_MODEL_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void initializeDataSets() {

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

        /**
         * This part of code prints out precisions etc.
         */
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

}
