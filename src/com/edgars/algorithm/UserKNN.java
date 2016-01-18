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
    private static final String TRAIN_MODEL_FILE = System.getProperty("user.dir") + "/scheduler/models/" + "UserKNNModel.txt";

    public UserKNN() throws SQLException {
        setRecommender(new org.mymedialite.itemrec.UserKNN());
        initializeDataSets();
        recommender.train();
        try {
            recommender.saveModel(TRAIN_MODEL_FILE);
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
    }

}