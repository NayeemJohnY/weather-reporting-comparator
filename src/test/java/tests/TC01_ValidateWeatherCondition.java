package tests;

import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import base.API;
import base.Browser;
import base.TestBase;
import pages.WeatherPage;
import static utils.ExtentHTMLReporter.reportLog;

public class TC01_ValidateTemperature extends TestBase {

	@Test
	public void TemperatureComparatorTest() {
		Browser browser = new Browser();
		try {
			extentTest = extentReporter.startTestCase("Tempeature Comaparator Test", "Compares the Temperature from API & UI");
			browser.launchURL(URL);
			WeatherPage weatherPage = new WeatherPage(browser, extentTest);
			weatherPage.getWeatherInfoFromUI();
			API api = new API(extentTest);
			api.getWeatherInfoFromAPI();
		} catch (Exception e) {
			String message = "Exception occurred while in tempature comparator test :<br/>"+ e.getMessage();
			log.error(message, e);
			reportLog(extentTest, Status.ERROR, message);
			fail();
		} finally {
			browser.closeActiveBrowser();
			extentReporter.flushResult();
		}
	}
}
