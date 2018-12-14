package ua.rassokha.util;

import ua.rassokha.exeption.SentiReadPropertyException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static Configuration INSTANSE;
    private DataSourse dataSourse;
    private Package aPackage;
    private FileLocation fileLocation;

    private Configuration() {
        init();
    }

    public static Configuration getConfiguration() {
        if (INSTANSE == null) {
            INSTANSE = new Configuration();
        }
        return INSTANSE;
    }

    private static FileLocation initFileLocation(Properties properties) {
        return new FileLocation(properties.getProperty(Constants.DICTIONARY_LOCATION),
                properties.getProperty(Constants.STOP_WORDS_LOCATION));
    }

    public DataSourse getDataSourse() {
        return dataSourse;
    }

    public void setDataSourse(DataSourse dataSourse) {
        this.dataSourse = dataSourse;
    }

    public Package getaPackage() {
        return aPackage;
    }

    public void setaPackage(Package aPackage) {
        this.aPackage = aPackage;
    }

    public FileLocation getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(FileLocation fileLocation) {
        this.fileLocation = fileLocation;
    }

    private void init() {
        Properties properties = new Properties();
        InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE);
        try {
            properties.load(inputStream);
        } catch (IOException | NullPointerException e) {
            throw new SentiReadPropertyException("Can not load properties", e);
        }
        this.setDataSourse(initDataSourse(properties));
        this.setFileLocation(initFileLocation(properties));
        this.setaPackage(initPackage(properties));
    }

    private DataSourse initDataSourse(Properties properties) throws SentiReadPropertyException {
        return new DataSourse(properties.getProperty(Constants.DATASOURCE_URL),
                properties.getProperty(Constants.DATASOURCE_USER), properties.getProperty(Constants.DATASOURCE_PASSWORD),
                properties.getProperty(Constants.DATASOURCE_DRIVER));
    }

    private Package initPackage(Properties properties) {
        return new Package(properties.getProperty(Constants.PACKAGE_SCAN));
    }

    public static class DataSourse {
        String URL;
        String USER;
        String PASSWORD;
        String DRIVER;

        DataSourse(String url, String user, String password, String driver) {
            URL = url;
            USER = user;
            PASSWORD = password;
            DRIVER = driver;
        }

        public String getURL() {
            return URL;
        }

        public String getUSER() {
            return USER;
        }

        public String getPASSWORD() {
            return PASSWORD;
        }

        public String getDRIVER() {
            return DRIVER;
        }
    }

    public static class FileLocation {
        String DICTIONARY;
        String STOP_WORDS;

        FileLocation(String dictionary, String stop_words) {
            DICTIONARY = dictionary;
            STOP_WORDS = stop_words;
        }

        public String getDICTIONARY() {
            return DICTIONARY;
        }

        public String getSTOP_WORDS() {
            return STOP_WORDS;
        }
    }

    private static class Package {
        String PACKAGE_SCAN;

        Package(String packageScan) {
            PACKAGE_SCAN = packageScan;
        }
    }
}
