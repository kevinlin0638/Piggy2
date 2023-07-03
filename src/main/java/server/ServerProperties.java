package server;

import constants.ServerConfig;
import org.apache.commons.io.FileUtils;
import tools.config.ConfigurableProcessor;
import tools.config.PropertiesUtils;

import java.io.*;
import java.util.Collection;
import java.util.Properties;

/**
 * @author Emilyx3
 */
public class ServerProperties {
    private static final String dirpath = "./";
    private static final Properties[] props;

    static {
        try {
            props = PropertiesUtils.loadAllFromDirectory(dirpath);
        } catch (Exception e) {
            System.err.println("加載配置文件發生錯誤" + e.getMessage());
            throw new Error("加載配置文件發生錯誤.", e);
        }
    }

    private ServerProperties() {
    }

    public static void load() {
        ConfigurableProcessor.process(ServerConfig.class, props);
    }

    public static String getProperty(String key, String defaultValue) {
        String ret = defaultValue;
        for (Properties prop : props) {
            if (prop.containsKey(key)) {
                ret = prop.getProperty(key);
            }
        }
        return ret;
    }

    public static void setProperty(String key, String value) {
        for (Properties prop : props) {
            if (prop.containsKey(key)) {
                prop.setProperty(key, value);
                changeFiles(key, value);
            }
        }
    }

    private static void changeFiles(String key, String value) {
        File root = new File(dirpath);
        try {
            Collection<File> files = FileUtils.listFiles(root, new String[]{"properties"}, false);
            for (File file : files) {
                if (file.isFile()) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "BIG5"))) {
                        while ((line = br.readLine()) != null) {
                            if (line.startsWith(key)) {
                                sb.append(key);
                                sb.append("=");
                                sb.append(value);
                                sb.append("\r\n");
                                continue;
                            }
                            sb.append(line);
                            sb.append("\r\n");
                        }
                        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "BIG5"))) {
                            bw.write(sb.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
