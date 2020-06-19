package base;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.TestProperties;

public class Browser {

	Logger log = LogManager.getLogger();
	public RemoteWebDriver driver;
	public final long LOADING_TIMEOUT = Integer.parseInt(TestProperties.getProperties("loadingTimeout"));

	/**
	 * Method to set up the the driver with the chrome browser It initializes the
	 * driver with respective browser with WebDriver Manager class
	 * WebDriver Manager- responsible for downloading & configuring the driver.exe files for browser
	 * Note: This can be extended to support other browsers
	 */
	private void setupBrowser() {
		// Get default browser name from properties file
		String browserName = TestProperties.getProperties("Browser");
		DesiredCapabilities capabilities;
		ChromeOptions options;
		if (browserName.equalsIgnoreCase("chrome")) {
			WebDriverManager.chromedriver().setup();
			options = new ChromeOptions();
			options.addArguments("--incognito");
			options.addArguments("--start-maximized");
			capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			options.merge(capabilities);
		} else {
			throw new IllegalArgumentException("Unsupported Browser : " + browserName);
		}

		// Get the execution server side local or Remote
		String server = System.getProperty("server");
		if (server == null || server.isEmpty() || server.equalsIgnoreCase("local")) {
			driver = new ChromeDriver(options);
		} else if (server.equalsIgnoreCase("Remote")) {
			String NodeURL = TestProperties.getProperties("NodeURL");
			try {
				driver = new RemoteWebDriver(new URL(NodeURL), capabilities);
			} catch (MalformedURLException e) {
				log.error("Exception in Node URL ! ", e);
			}
		} else {
			throw new IllegalStateException(
					"Unsupported Execution Server : " + server + " only values 'Local/Remote' are allowed");
		}
	}

	/**
	 * Method to set up the browser & launch the url
	 * @param url
	 */
	public void launchURL(String url) {
		setupBrowser();
		driver.get(url);
		log.info("Successfully launched URL : {}", url);
	}

	/**
	 * Wait for an element's presence and visibility for a timeout in seconds.
	 * @param locator - By
	 * @param elementName - Name of the element
	 * @param timeOutInSeconds - Wait time in seconds
	 */
	public void waitForVisibility(By locator, String elementName, long timeOutInSeconds) {
		log.info("Waiting for {} seconds for visibilty of element: {}", timeOutInSeconds, elementName);
		new WebDriverWait(driver, timeOutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
		log.info("{} element become visible ", elementName);
	}

	/**
	 * Waits for element to be present for time specified in properties
	 * and then clicks on all instances found 'by'
	 * @param locator - By
	 * @param elementName - Name of the element
	 * @param timeOutInSeconds - Wait time in seconds
	 */
	public void click(By locator, String elementName, long timeOutInSeconds) {
		log.info("Waiting for {} seconds for element to be clickable {}", timeOutInSeconds, elementName);
		WebElement element = new WebDriverWait(driver, timeOutInSeconds)
				.until(ExpectedConditions.elementToBeClickable(locator));
		try {
			element.click();
			log.info("Clikced on the Element: {}", elementName);
		} catch (ElementClickInterceptedException e) {
			driver.executeScript("arguments[0].scrollIntoView()", element);
			log.info("Javascript scroll into view to the element : {}", elementName);
			driver.executeScript("arguments[0].click()", element);
			log.info("Performed javascript click on the Element: {}", elementName);
		}
	}

	/**
	 * This method is used to close the active browser
	 */
	public void closeActiveBrowser() {
		try {
			if (driver != null) {
				driver.quit();
				log.info("Browser closed successfully");
			}
		} catch (Exception e) {
			log.error("Unable to close the browser. Exception: ", e);
		}
	}

	/**
	 * Method to send the keys to the element specified by locator
	 * @param locator - By
	 * @param keys - String keys
	 * @param elementName - Name of the element
	 * @param timeOutInSeconds - Wait time in seconds
	 */
	public void sendKeys(By locator, String keys, String elementName, long timeOutInSeconds) {
		waitForVisibility(locator, elementName, timeOutInSeconds);
		log.info("sending keys {} by locator {} for element {}", keys, locator, elementName);
		WebElement element = driver.findElement(locator);
		element.clear();
		element.sendKeys(keys);

	}

	/**
	 * Checks an elements presence on the page with wait time.
	 * @param locator - By
	 * @param elementName - Name of the element
	 * @param timeOutInSeconds - Wait time in seconds
	 * @return boolean - element presence
	 */
	public boolean isElementPresent(By locator, long timeOutInSeconds, String elementName) {
		boolean isPresent = !driver.findElements(locator).isEmpty();
		if (!isPresent) {
			try {
				new WebDriverWait(driver, timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
			} catch (Exception e) {
				log.error("Exception occurred while checking presence of Element : {}", elementName);
			}

		}

		if (isPresent) {
			log.info("Element found: {}", elementName);
		} else {
			log.warn("Expected element is not present in {} seconds, locator {}, element {}", timeOutInSeconds, locator,
					elementName);
		}

		return isPresent;
	}
}
