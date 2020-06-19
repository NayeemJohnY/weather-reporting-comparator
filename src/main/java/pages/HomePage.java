package pages;

import static utils.ExtentHTMLReporter.reportLog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import base.Browser;

public class HomePage {

	Logger log = LogManager.getLogger();
	By notificationPopupBy = By.xpath("//div[@class='noti_wrap']");
	By noThanksLinkBy = By.xpath("//a[text()='No Thanks']");
	By subMenuBy = By.id("h_sub_menu");
	By weatherMenuBy = By.xpath("//a[text()='WEATHER']");

	Browser browser;
	ExtentTest extentTest;

	public HomePage(Browser browser, ExtentTest extentTest) {
		this.browser = browser;
		this.extentTest = extentTest;
		PageFactory.initElements(browser.driver, this);
	}

	/**
	 * Method to navigate to the Weather page from the Home page
	 * @throws IllegalStateException - if title is mismatches
	 */
	public void navigateToWeatherPage() {
		browser.waitForVisibility(notificationPopupBy, "Notification Pop up", browser.LOADING_TIMEOUT);
		browser.click(noThanksLinkBy, "No Thanks Link Option", browser.LOADING_TIMEOUT);
		browser.click(subMenuBy, "Sub Menu", browser.LOADING_TIMEOUT);
		browser.click(weatherMenuBy, "WEATHER Menu", browser.LOADING_TIMEOUT);
		String title = browser.driver.getTitle();
		if (title.contains("NDTV Weather")) {
			reportLog(extentTest, Status.PASS,"Successfully navigated to NDTV Weather page");
		} else {
			throw new IllegalStateException("Failed to naviagate NDTV Weather page, the current page title is: " + title);
		}
	}
}
