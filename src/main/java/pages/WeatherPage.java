package pages;

import static utils.ExtentHTMLReporter.reportLog;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import base.Browser;

public class WeatherPage {

	protected Browser browser;
	protected Logger log = LogManager.getLogger();
	protected By pinYourCityElementBy = By.xpath("//*[text()='Pin your City']");
	protected By searchBoxBy = By.id("searchBox");
	protected By availableCitiesBy = By.xpath("//div[@class='message' and not(@style='display: none;')]//input");
	protected By weatherPopupContentsBy = By.xpath("//div[@class='leaflet-popup-content']//span[@class='heading']");
	protected ExtentTest extentTest;

	public WeatherPage(Browser browser, ExtentTest extentTest) {
		this.browser = browser;
		this.extentTest = extentTest;
		PageFactory.initElements(browser.driver, this);

	}

	/**
	 * Method to get the Weather value for the condition key for the city
	 * @param searchCity
	 * @param weatherKey
	 * @return weatherValue - value for the weatherKey
	 */
	public String getWeatherInfoFromUISourceForCity( String searchCity, String weatherKey) {
		String weatherValue = "";
		List<String> listOFWeatherInfo = getWeatherInfoForCity(searchCity);
		reportLog(extentTest, Status.PASS, "Weather Information for the City - " + searchCity);
		String[][] arrayOfWeatherData = new String[listOFWeatherInfo.size()][2];
		for (int i = 0; i < listOFWeatherInfo.size(); i++) {
			String[] weatherInfo = listOFWeatherInfo.get(i).split(":");
			arrayOfWeatherData[i][0] = weatherInfo[0];
			arrayOfWeatherData[i][1] = weatherInfo[1];

			// Get the value for the condition key
			if (weatherInfo[0].equalsIgnoreCase(weatherKey)) {
				weatherValue = weatherInfo[1].replace("%", "");
			}
		}
		// Log the weather conditions info from the UI
		extentTest.info(MarkupHelper.createTable(arrayOfWeatherData));
		return weatherValue;
	}

	/**
	 * Method to check the city is available in to select
	 * @param searchCity
	 */
	private void checkCityisAvailableToSelect(String searchCity) {
		HomePage homePage = new HomePage(browser, extentTest);
		homePage.navigateToWeatherPage();
		browser.waitForVisibility(pinYourCityElementBy, "Pin Your City", browser.LOADING_TIMEOUT);
		browser.sendKeys(searchBoxBy, searchCity, "Search Box", browser.LOADING_TIMEOUT);
		List<WebElement> availableCitiesElements = browser.driver.findElements(availableCitiesBy);
		if (availableCitiesElements.isEmpty()) {
			throw new IllegalArgumentException("No matching city is available with city Name : " + searchCity);
		} else if (availableCitiesElements.size() > 1) {
			reportLog(extentTest, Status.WARNING,
					"Multiple cities are matching with city name : <b>" + searchCity + "</b>. Selecting the 1st City");
		}
	}

	/**
	 * Method to select city from available cities & pin the City in Map
	 */
	private void pinCityinMap() {
		List<WebElement> availableCitiesElements = browser.driver.findElements(availableCitiesBy);
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
			throw new IllegalStateException("The city " + cityName + " is not dispalyed in the Map");
		}
	}

	/**
	 * Method to get the  All weather conditions values for the city
	 * @param searchCity
	 * @return listOfWeatherInfo - List of weather condition info
	 */
	private List<String> getWeatherInfoForCity(String searchCity) {
		checkCityisAvailableToSelect(searchCity);
		pinCityinMap();
		By cityInMapBy = By.xpath("//div[@class='outerContainer' and @title='" + searchCity + "']");
		browser.click(cityInMapBy, "City in Map", browser.LOADING_TIMEOUT);
		browser.waitForVisibility(weatherPopupContentsBy, "Weather Pop up Contents", browser.LOADING_TIMEOUT);
		List<String> listOfWeatherInfo = new ArrayList<>();
		for (WebElement weatherElement : browser.driver.findElements(weatherPopupContentsBy)) {
			listOfWeatherInfo.add(weatherElement.getAttribute("textContent"));
		}
		By closePopupBy = By.xpath("//a[@class='leaflet-popup-close-button']");
		browser.click(closePopupBy, "Close Weather Content Pop up", browser.LOADING_TIMEOUT);
		return listOfWeatherInfo;
	}
}
