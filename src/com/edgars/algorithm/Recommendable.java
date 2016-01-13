package com.edgars.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Edgars on 13/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 */
public interface Recommendable {

    double predict(int userId, int itemId);
    boolean canPredict(int userId, int itemId);
    void train();
    void saveModel(String filename) throws IOException;
    void saveModel(PrintWriter writer) throws IOException;
    void loadModel(String filename) throws IOException;
    void loadModel(BufferedReader reader) throws IOException;
    String toString();

}
