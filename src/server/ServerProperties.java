package server;

import constants.GameConstants;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Emilyx3
 */
public class ServerProperties {

    private static final Properties props = new Properties();

    static {
        String toLoad = "channel.properties";
        loadProperties(toLoad);
        if (getProperty("GMS") != null) {
            GameConstants.GMS = Boolean.parseBoolean(getProperty("GMS"));
        }
        toLoad = GameConstants.GMS ? "server.properties" : "server.properties";
        loadProperties(toLoad);
    }

    private ServerProperties() {
    }

    public static void loadProperties(String s) {
        FileReader fr;
        try {
            fr = new FileReader(s);
            props.load(fr);
            fr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String s) {
        return props.getProperty(s);
    }

    public static void setProperty(String prop, String newInf) {
        props.setProperty(prop, newInf);
    }

    public static String getProperty(String s, String def) {
        return props.getProperty(s, def);
    }
}
