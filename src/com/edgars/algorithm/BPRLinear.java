package com.edgars.algorithm;

import org.mymedialite.IItemAttributeAwareRecommender;
import org.mymedialite.IIterativeModel;
import org.mymedialite.data.IPosOnlyFeedback;
import org.mymedialite.datatype.SparseBooleanMatrix;
import org.mymedialite.io.ItemData;
import org.mymedialite.itemrec.ItemRecommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * NOT WORKING!
 *
 * Created by Edgars on 13/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public class BPRLinear extends Algorithm {

    private static final String MODEL_FILE = System.getProperty("user.dir") + "/models/" + "BPRLinearModel.txt";
    private static final String MODEL_FILE_TEST = System.getProperty("user.dir") + "/models/" + "TESTBPRLinearModel.txt";

    public BPRLinear() throws SQLException {

    }

    public BPRLinear(int visitorId, int count) throws SQLException {
        this.visitorId = visitorId;
        this.count = count;
        setRecommender(new org.mymedialite.itemrec.BPRLinear());
        initializeDataSets();
    }

    protected void initializeDataSets() {

        IPosOnlyFeedback iPosOnlyFeedback = null;
        BufferedReader bufferedReader = getReader();
        try {
            iPosOnlyFeedback = ItemData.read(bufferedReader, userMapping, itemMapping, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ItemRecommender) recommender).setFeedback(iPosOnlyFeedback);

        SparseBooleanMatrix sparseBooleanMatrix = getSparseBooleanMatrix();

        ((IItemAttributeAwareRecommender) recommender).setItemAttributes(sparseBooleanMatrix);

        SparseBooleanMatrix sparseBooleanMatrix2 = ((IItemAttributeAwareRecommender) recommender).getItemAttributes();
        System.out.println("*****");
        for (int i = 0; i < sparseBooleanMatrix2.numberOfRows(); i++) {
            System.out.println(itemMapping.toOriginalID(i) + " : " + sparseBooleanMatrix2.get(i).toString());
        }
        System.out.println(recommender.toString());

        File file = new File(MODEL_FILE);
        try {
            if (file.exists()) {
                recommender.loadModel(MODEL_FILE);

                ((IIterativeModel) recommender).iterate();

               // testUpdate();

                recommender.saveModel(MODEL_FILE);
            } else {
                recommender.train();
                recommender.saveModel(MODEL_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private SparseBooleanMatrix getSparseBooleanMatrix() {
        SparseBooleanMatrix sparseBooleanMatrix = new SparseBooleanMatrix();

        try {
            resultSet = hBaseSQLManager.executeSqlGetString(
                    "SELECT BOOK_ID, CATEGORY_ID FROM BOOK_CATEGORY GROUP BY BOOK_ID, CATEGORY_ID"
            );
            while (resultSet.next()) {
                int bookId = resultSet.getInt("BOOK_ID");
                bookId = itemMapping.toInternalID(Integer.toString(bookId));
                int categoryId = resultSet.getInt("CATEGORY_ID");
                sparseBooleanMatrix.set(bookId, categoryId, true);
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
        System.out.println("sparseBooleanMatrix" + sparseBooleanMatrix.numberOfRows());
        System.out.println(sparseBooleanMatrix.toString());
        for (int i = 0; i < sparseBooleanMatrix.numberOfRows(); i++) {
            System.out.println(itemMapping.toOriginalID(i) + " : " + sparseBooleanMatrix.get(i).toString());
        }
        return sparseBooleanMatrix;
    }

   /* protected void testUpdate() {
        SparseBooleanMatrix itemAttributes = ((IItemAttributeAwareRecommender) recommender).getItemAttributes();
        SampleTriple t = new SampleTriple();
        org.mymedialite.util.Random random = Random.getInstance();
        Matrix<Double> itemAttributeWeightByUser = new Matrix<Double>(2 + 1, itemAttributes.numberOfColumns(), 0.0);
        double regularization = 0.015;
        double learnRate = 0.05;

        for (int f = 0; f < 50; f++) {
            t.u = random.nextInt(0, 2);
            t.i = 0;
            t.j = random.nextInt(4, 5);

            double x_uij = ((ItemRecommender) recommender).predict(t.u, t.i) - ((ItemRecommender) recommender).predict(t.u, t.j);
            System.out.println("t.u: " + t.u);
            System.out.println("t.i: " + t.i);
            System.out.println("t.j: " + t.j);


            IntCollection attr_i = itemAttributes.get(t.i);
            IntCollection attr_j = itemAttributes.get(t.j);
            System.out.println("attr_i: " + attr_i.toString());
            System.out.println("attr_j: " + attr_j.toString());

            // Assumption: attributes are sparse
            IntSet attr_i_over_j = new IntArraySet(attr_i);
            attr_i_over_j.removeAll(attr_j);

            IntSet attr_j_over_i = new IntArraySet(attr_j);
            attr_j_over_i.removeAll(attr_i);
            System.out.println("attr_i_over_j: " + attr_i_over_j.toString());
            System.out.println("attr_j_over_i: " + attr_j_over_i.toString());
            double one_over_one_plus_ex = 1 / (1 + Math.exp(x_uij));
            System.out.println("x_uij: " + x_uij);
            System.out.println("one_over_one_plus_ex: " + one_over_one_plus_ex);


            for (int a : attr_i_over_j) {
                System.out.println("a: " + a);

                double w_uf = itemAttributeWeightByUser.get(t.u, a);
                double uf_update = one_over_one_plus_ex - regularization * w_uf;
                itemAttributeWeightByUser.set(t.u, a, w_uf + learnRate * uf_update);

                System.out.println("w_uf: " + w_uf);
                System.out.println("uf_update: " + uf_update);
                System.out.println("set: " + itemAttributeWeightByUser.get(t.u, a));

            }

            for (int a : attr_j_over_i) {
                System.out.println("a2: " + a);

                double w_uf = itemAttributeWeightByUser.get(t.u, a);
                double uf_update = -one_over_one_plus_ex - regularization * w_uf;
                itemAttributeWeightByUser.set(t.u, a, w_uf + learnRate * uf_update);

                System.out.println("w_uf: " + w_uf);
                System.out.println("uf_update: " + uf_update);
                System.out.println("set: " + itemAttributeWeightByUser.get(t.u, a));

            }


        }
        try {
            try (PrintWriter writer = Model.getWriter(MODEL_FILE_TEST, this.getClass(), "1")) {
                IMatrixExtensions.writeMatrix(writer, itemAttributeWeightByUser);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

/*
    private class SampleTriple {
        int u;  // user_id
        int i;  // item_id positive item
        int j;  // item_id negative item
    }*/
}
