package base;

import static utils.ExtentHTMLReporter.reportLog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import utils.ExtentHTMLReporter;
import utils.TestProperties;

public class TestBase {

	protected String url = TestProperties.getProperties("URL");
	protected Logger log = LogManager.getLogger();
	protected static ExtentHTMLReporter extentReporter  = new ExtentHTMLReporter();;
	protected ExtentTest extentTest;
	protected String searchCity = System.getProperty("City");
	protected double variance = 0;
	protected String weatherConditionKey = "";
	public static final String BLACK_CIRCLE = "&#9673;";
	/**
	 * Method to set up start a report, weather condition key & variance
	 * @param testCaseName
	 * @param testDescription
	 */
	@BeforeClass
	@Parameters({ "testCaseName", "testDescription" })
	public void beforeClassMethod(String testCaseName, String testDescription) {
		extentTest = extentReporter.startTestCase(testCaseName, testDescription);
		if (testCaseName.contains("Temperature In Degress")) {
			weatherConditionKey = WeatherConstants.TEMP_IN_DEGREES;
			setupVariance("Temperature_variance");
		} else if (testCaseName.contains("Temperature In Fahrenheit")) {
			weatherConditionKey = WeatherConstants.TEMP_IN_FAHRENHEIT;
			setupVariance("Temperature_variance");
		} else if (testCaseName.contains("Humidity")) {
			weatherConditionKey = WeatherConstants.HUMIDITY;
			setupVariance("Humidity_variance");
		}
	}

	/**
	 * Method to data of cities for Test
	 * @return Object[] - Array of Objects of cities
	 */
	@DataProvider(name = "Search City Names")
	public Object[] getCityNames() {
		if (searchCity == null || searchCity.isEmpty()) {
			searchCity = TestProperties.getProperties("City");
			reportLog(extentTest, Status.WARNING,
					"Search City is not provided, checking for default cities : <b>" + searchCity + "</b>");
		} else {
			reportLog(extentTest, Status.PASS, "Search city/cities: " + searchCity);
		}
		return searchCity.split(",");
	}

	/**
	 * Method to check the difference of values in specified variance range
	 * @param valueString1
	 * @param valueString2
	 * @return boolean true if difference in specified range;
	 * @throws IllegalStateException with Matcher Exception
	 */
	protected boolean valueComparatorWithVariance(String valueString1, String valueString2) {
		double value1 = Double.parseDouble(valueString1);
		double value2 = Double.parseDouble(valueString2);
		double actualVariance = Math.abs(value1 - value2);
		if (actualVariance <= variance) {
			return true;
		} else {
			throw new IllegalStateException(
					"Matcher Exception: The values difference is not in the specified range<br>." +
							BLACK_CIRCLE + "  Specifed variance range: " + variance + "<br>" +
							BLACK_CIRCLE + "  Actual variance: " + actualVariance);
		}
	}

	/**
	 * Method to set up the variance
	 * @param varianceKey
	 * @throws IllegalArgumentException - if values are not decimal
	 */
	protected void setupVariance(String varianceKey) {
		String varianceValue = System.getProperty(varianceKey);
		if (varianceValue == null || varianceValue.isEmpty()) {
			varianceValue = TestProperties.getProperties(varianceKey);
			reportLog(extentTest, Status.WARNING, varianceKey + " is not provided, checking for default " + varianceKey
					+ " value : <b>" + varianceValue + "</b>");
		}
		try {
			variance = Double.parseDouble(varianceValue);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Variance Value should only contains the decimal values. But current "
					+ varianceKey + " contains other than decimals: " + varianceValue);
		}
	}

}
