package pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import base.Browser;
import utils.TestProperties;
import static utils.ExtentHTMLReporter.reportLog;

public class WeatherPage {

	Browser browser;
	Logger log = LogManager.getLogger();
	By pinYourCityElementBy = By.xpath("//*[text()='Pin your City']");
	By searchBoxBy = By.id("searchBox");
	String searchCity = System.getProperty("City");
	By availableCitiesBy = By.xpath("//div[@class='message' and not(@style='display: none;')]//input");
	ExtentTest extentTest;

	public WeatherPage(Browser browser, ExtentTest extentTest) {
		this.browser = browser;
		this.extentTest = extentTest;
		PageFactory.initElements(browser.driver, this);

	}

	public void getWeatherInfoFromUI() {
		HomePage homePage = new HomePage(browser, extentTest);
		homePage.navigateToWeatherPage();
		browser.waitForVisibility(pinYourCityElementBy, "Pin Your City", browser.LOADING_TIMEOUT);
		if (searchCity == null || searchCity.isEmpty()) {
			searchCity = TestProperties.getProperties("city");
			reportLog(extentTest, Status.WARNING,
					"Search City is not provided, checking for default city : <b>" + searchCity + "</b>");
		} else {
			reportLog(extentTest, Status.PASS, "Search City is: " + searchCity);
		}
		browser.sendKeys(searchBoxBy, searchCity, "Search Box", browser.LOADING_TIMEOUT);
		List<WebElement> availableCitiesElements = browser.driver.findElements(availableCitiesBy);
		if (availableCitiesElements.isEmpty()) {
			throw new IllegalArgumentException(
					"No matching city available to pin for given city Name : " + searchCity);
		} else if (availableCitiesElements.size() > 1) {
			reportLog(extentTest, Status.WARNING,
					"Multiple cities available to pin for given city name : <b>" + searchCity + "</b>");
		}
		WebElement cityElement = availableCitiesElements.get(0);
		String cityName = cityElement.getAttribute("id");
		if (cityElement.isSelected()) {
			reportLog(extentTest, Status.INFO, "City <b>" + cityName + "</b> already selected.");
		} else {
			browser.click(availableCitiesBy, "Select Search City", browser.LOADING_TIMEOUT);
			reportLog(extentTest, Status.PASS, "City <b>" + cityName + "</b> is selected.");
		}
		By cityInMapBy = By.xpath("//div[@class='outerContainer' and @title='" + cityName + "']");
		if (browser.isElementPresent(cityInMapBy, browser.LOADING_TIMEOUT, "City in Map")) {
			reportLog(extentTest, Status.PASS, "City <b>" + cityName + "</b> is dispalyed in Map");
		} else {
			throw new IllegalArgumentException("The city " + searchCity + " was not dispalyed in the Map");
		}
		browser.click(cityInMapBy, "City in Map", browser.LOADING_TIMEOUT);
		By weatherPopupContentBy = By.xpath("//div[@class='leaflet-popup-content']//span[@class='heading']");
		browser.waitForVisibility(weatherPopupContentBy, "Weather Pop up Content", browser.LOADING_TIMEOUT);
		List<String> weatherStrings = new ArrayList<>();
		for (WebElement weatherElement : browser.driver.findElements(weatherPopupContentBy)) {
			weatherStrings.add(weatherElement.getAttribute("textContent"));
		}
		reportLog(extentTest, Status.PASS, "Weather Information for the City - " + cityName + " : " + weatherStrings);
		By closePopupBy = By.xpath("//a[@class='leaflet-popup-close-button']");
		browser.click(closePopupBy, "Close Weather Content Pop up", browser.LOADING_TIMEOUT);

	}
}
