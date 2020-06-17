package utils;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadTestProperties {
	private static Logger log = LogManager.getLogger();

	/**
	 * private constructor to not allow the instance creation of this class
	 */
	private ReadTestProperties() {
		
	}

	/**
	 * Method to get the property value of the key from GlobalTestProperties properties file
	 * @param key - String - key whose value is to be fetched
	 * @return propertyValue - String - value of the key
	 */
	public static String getProperties(String key) {
		String propertyValue = "";
		try {
			FileInputStream inStream = new FileInputStream("src/main/resources/GlobalTestProperties.properties");
			Properties prop = new Properties();
			prop.load(inStream);
			propertyValue = prop.getProperty(key);
			return propertyValue;
		} catch (Exception e) {
			log.error("Exception !", e);
			return null;
		}
	}

}
