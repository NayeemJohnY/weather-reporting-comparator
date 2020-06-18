package tests;

import org.testng.annotations.Test;

import base.Browser;
import base.TestBase;

public class TC01_TemperatureComparatorTest extends TestBase {

	@Test
	public void verifyTemperature() {
		Browser br = new Browser();
		br.launchURL(URL);
	}
}
