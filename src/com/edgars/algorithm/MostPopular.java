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
 *
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

    }

}
