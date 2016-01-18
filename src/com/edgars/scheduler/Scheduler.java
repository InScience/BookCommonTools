package com.edgars.scheduler;

import com.edgars.algorithm.Algorithm;
import com.edgars.algorithm.MostPopular;
import com.edgars.algorithm.UserKNN;

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Scheduler {

    public static void main(String[] args) {

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        Logger logger = Logger.getLogger(Scheduler.class.getSimpleName());
        String logPath = FileSystemView.getFileSystemView().getDefaultDirectory() +
                "/scheduler/log/scheduler.log";
        FileHandler fh;
        try {
            System.out.println(Scheduler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            fh = new FileHandler(logPath);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        logger.info(timeStamp + " : " + "Scheduler launched. Calling: " + args[0]);

        Algorithm algorithm = null;

        switch (args[0]) {
            case "mostpopular":
                try {
                    algorithm = new MostPopular();
                } catch (SQLException e) {
                    logger.info(e.toString());
                }
                break;
            case "userknn":
                try {
                    algorithm = new UserKNN();
                } catch (SQLException e) {
                    logger.info(e.toString());
                }
                break;
        }

    }
}
