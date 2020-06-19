package tests;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static utils.ExtentHTMLReporter.reportLog;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import base.API;
import base.Browser;
import base.TestBase;
import pages.WeatherPage;

public class TC01_ValidateWeatherCondition extends TestBase {

	/**
	 * Method to execute the test and compare the weather conditions for the city
	 * @param searchCity
	 */
	@Test(dataProvider = "Search City Names")
	public void weatherComparatorTest(String searchCity) {
		Browser browser = new Browser();
		ExtentTest childTest = extentTest.createNode(weatherConditionKey + " Test For City: " + searchCity);
		extentTest.assignCategory(weatherConditionKey + " Test For City:-" + searchCity);
		try {
			browser.launchURL(url);
			WeatherPage weatherPage = new WeatherPage(browser, childTest);

			// Retrieve the weather condition info from UI source for the city & key
			String weatherValueFromUISource = weatherPage.getWeatherInfoFromUISourceForCity(searchCity, weatherConditionKey);

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
			fail(e.getMessage(), e);
		} catch (Exception e) {
			String message = "Exception occurred while verifing " + weatherConditionKey + " Test :<br>" + BLACK_CIRCLE + e.getMessage();
			log.error(message, e);
			reportLog(childTest, Status.ERROR, e.getMessage());
			fail(message, e);
		} finally {
			browser.closeActiveBrowser();
			extentReporter.flushResult();
		}
	}

}
