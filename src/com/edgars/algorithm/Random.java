package com.edgars.algorithm;

import java.sql.SQLException;

/**
 * Random recommender.
 *
 * Created by Edgars on 13/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class Random extends Algorithm {

    /**
     * Constructor if visitor is known.
     *
     * @param visitorId Unique id of visitor.
     * @param count     Item count to return.
     * @throws SQLException
     */
    public Random(int visitorId, int count) throws SQLException {
        setRecommender(new org.mymedialite.itemrec.Random());
        initializeDataSets();
        this.visitorId = visitorId;
        this.count = count;
    }

    /**
     * Constructor will be used by scheduler.
     *
     * @throws SQLException
     */
    public Random() throws SQLException {
        setRecommender(new org.mymedialite.itemrec.Random());
        initializeDataSets();

    }

    public Random(int i) throws SQLException {
        visitorId = i;
    }

    /**
     * Should fill up lists.
     * In random no lists are needed.
     */
    protected void initializeDataSets() {

    }


}
