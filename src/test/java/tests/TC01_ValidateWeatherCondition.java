package tests;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static utils.ExtentHTMLReporter.reportLog;

import java.util.Map;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import base.API;
import base.Browser;
import base.TestBase;
import base.WeatherConstants;
import pages.WeatherPage;
import utils.TestProperties;

public class TC01_ValidateWeatherCondition extends TestBase {

	protected String weatherConditions = System.getProperty("WeatherConditions");
	/**
	 * Method to set up start a report, weather condition key & variance
	 */
	@BeforeTest
	public void beforeTestMethod() {
		if (weatherConditions == null || weatherConditions.isEmpty()) {
			weatherConditions = TestProperties.getProperties("WeatherConditions");
			log.info("Default Weather Conditions to be validated  : {}", weatherConditions);
		} else {
			log.info("Weather Conditions to be validateds: {}", weatherConditions);
		}
	}

	/**
	 * Method to execute the test and compare the weather conditions for the city
	 * @param searchCity
	 */
	@Test(dataProvider = "Search City Names")
	public void weatherComparatorTest(String searchCity) {
		Browser browser = new Browser();
		ExtentTest extentTest = extentReporter.startTestCase("Weather Test For City " + searchCity , "Validate Weather condtions for the city");
		browser.launchURL(url);
		WeatherPage weatherPage = new WeatherPage(browser, extentTest);
		Map<String, String> mapOfWeatherInfo = weatherPage.getWeatherInfoForCity(searchCity);
		try {

			String weatherValueFromUISource = "";
			for (String weatherConditionKey : weatherConditions.split(",")) {
				ExtentTest childTest = extentTest.createNode(weatherConditionKey + " Test For City: " + searchCity);
				extentTest.assignCategory(weatherConditionKey);
				try {
					if (weatherConditionKey.contains("Temperature In Degrees")) {
						weatherConditionKey = WeatherConstants.TEMP_IN_DEGREES;
						setupVariance(childTest, "Temperature_variance");
						weatherValueFromUISource = mapOfWeatherInfo.get(WeatherConstants.TEMP_IN_DEGREES);
					} else if (weatherConditionKey.contains("Temperature In Fahrenheit")) {
						weatherConditionKey = WeatherConstants.TEMP_IN_FAHRENHEIT;
						setupVariance(childTest, "Temperature_variance");
						weatherValueFromUISource = mapOfWeatherInfo.get(WeatherConstants.TEMP_IN_FAHRENHEIT);
					} else if (weatherConditionKey.contains("Humidity")) {
						weatherConditionKey = WeatherConstants.HUMIDITY;
						setupVariance(childTest, "Humidity_variance");
						weatherValueFromUISource = mapOfWeatherInfo.get(WeatherConstants.HUMIDITY).replace("%", "");
					}
					API api = new API(childTest);

					// Retrieve the weather condition info from API source for the city & key
					String weatherValueFromAPISource = api.getWeatherInfoFromAPISourceForCity(searchCity, weatherConditionKey);

					assertTrue(valueComparatorWithVariance(weatherValueFromUISource, weatherValueFromAPISource));
					reportLog(childTest, Status.PASS,
							"The values from the sources are matching within the specified range for Test: <b>"
									+ weatherConditionKey + "</b>.<br>" + BLACK_CIRCLE + "  The " + weatherConditionKey + " in API : "
									+ weatherValueFromAPISource + "<br>" + BLACK_CIRCLE +"  The " + weatherConditionKey + " in UI : "
									+ weatherValueFromUISource + "<br>" + BLACK_CIRCLE + "  The variance range : " + variance);
				} catch (IllegalStateException | IllegalArgumentException e) {
					reportLog(childTest, Status.FAIL, e.getMessage());
					log.error(e.getMessage(), e);
				}catch (Exception e) {
					String message = "Exception occurred while verifing weather Condition- " + weatherConditionKey + " for City :<br>" + searchCity + BLACK_CIRCLE + e.getMessage();
					log.error(message, e);
					reportLog(extentTest, Status.ERROR, e.getMessage());
				}
			}
		} catch (IllegalStateException | IllegalArgumentException e) {
			reportLog(extentTest, Status.FAIL, e.getMessage());
			log.error(e.getMessage(), e);
			fail(e.getMessage(), e);
		} catch (Exception e) {
			String message = "Exception occurred while verifing weather Conditions for City :<br>" + searchCity + BLACK_CIRCLE + e.getMessage();
			log.error(message, e);
			reportLog(extentTest, Status.ERROR, e.getMessage());
			fail(message, e);
		} finally {
			browser.closeActiveBrowser();
			extentReporter.flushResult();
		}
	}

}
