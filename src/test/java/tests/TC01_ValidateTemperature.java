package tests;

import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import base.Browser;
import base.TestBase;
import pages.WeatherPage;

public class TC01_ValidateTemperature extends TestBase {

	@Test
	public void TemperatureComparatorTest() {
		Browser browser = new Browser();
		try {
			browser.launchURL(URL);
			WeatherPage weatherPage = new WeatherPage(browser);
			weatherPage.getWeatherInfoFromUI();

		} catch (Exception e) {
			log.error("Exception !", e);
			fail("Exception occurred while in tempature comparator test.. !", e);
		} finally {
			browser.closeActiveBrowser();
		}
	}
}
