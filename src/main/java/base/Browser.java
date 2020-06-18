package base;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ReadTestProperties;

public class Browser {

	Logger log = LogManager.getLogger();
	RemoteWebDriver driver;
	public final int LOADING_TIMEOUT = Integer.parseInt(ReadTestProperties.getProperties("loadingTimeout"));

	/**
	 * Method to set up the the driver with the chrome browser It initializes the
	 * driver with respective browser with WebDriver Manager class WebDriver Manager
	 * - responsible for downloading & configuring the driver.exe files for browser
	 * Note: This can be extended to support other browsers
	 */
	private void setupBrowser() {
		// Get default browser name from properties file
		String browserName = ReadTestProperties.getProperties("Browser");
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
			throw new IllegalStateException("Unsupported Browser : " + browserName);
		}

		// Get the execution server side local or Remote
		String server = System.getProperty("server");
		if (server == null || server.isEmpty() || server.equalsIgnoreCase("local")) {
			driver = new ChromeDriver(options);
		} else if (server.equalsIgnoreCase("Remote")) {
			String NodeURL = ReadTestProperties.getProperties("NodeURL");
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

	public void launchURL(String url) {
		setupBrowser();
		driver.get(url);
		log.info("Successfully launched URL : {}", url);
	}

	public void waitForVisibilty(By locator, String elementName, int timeOutInSeconds) {
		new WebDriverWait(driver, timeOutInSeconds)
		.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}
}
