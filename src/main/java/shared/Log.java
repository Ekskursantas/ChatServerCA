//***************************************

// Class made by Zyggi(Zygimantas Pranka)

//***************************************
package shared;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    private static String logFileName = "myLogger";

    public static void setLogger(String fileName, String logName) {
        try {
            logFileName = fileName;
            Logger logger = Logger.getLogger(logFileName);
            FileHandler fileHandler = new FileHandler(logFileName);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }

    public static void closeLogger() {
        for (Handler h : Logger.getLogger(logFileName).getHandlers()) {
            h.close();
        }
    }

}
