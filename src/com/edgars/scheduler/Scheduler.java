package com.edgars.scheduler;

import com.edgars.algorithm.Algorithm;
import com.edgars.algorithm.MostPopular;
import com.edgars.algorithm.UserKNN;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Scheduler {

    public static void main(String[] args) {

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        String today = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        Logger LOGGER = Logger.getLogger(Scheduler.class.getSimpleName());
        String logPath = FileSystemView.getFileSystemView().getDefaultDirectory() +
                "/scheduler/log/scheduler-" + today + ".log";

        FileHandler fh;
        try {
            fh = new FileHandler(logPath, true);
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.ALL);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        LOGGER.info(timeStamp + " : " + "Scheduler launched. Calling: " + args[0]);

        Algorithm algorithm = null;

        switch (args[0]) {
            case "mostpopular":
                try {
                    algorithm = new MostPopular();
                } catch (SQLException e) {
                    LOGGER.info(e.toString());
                }
                break;
            case "userknn":
                try {
                    algorithm = new UserKNN();
                } catch (SQLException e) {
                    LOGGER.info(e.toString());
                }
                break;
        }

    }
}
