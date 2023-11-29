package com.watermelon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
public class AppConfig {
    private static final String PROPERTIES_FILE = "application.properties";

    private static Properties properties;

    public AppConfig() {
        loadProperties();
    }

    private void loadProperties() {
        properties = new Properties();
        InputStream input = null;

        try {
            // 试图使用 FileInputStream
            try {
                input = new FileInputStream(PROPERTIES_FILE);
            } catch (FileNotFoundException ignored) {
                // 如果文件不存在，尝试使用 getClass().getClassLoader().getResourceAsStream
                input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
                if (input == null) {
                    System.out.println("Sorry, unable to find " + PROPERTIES_FILE);
                    return;
                }
            }

            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭 InputStream
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
