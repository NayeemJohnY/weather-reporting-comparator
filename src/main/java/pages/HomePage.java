package pages;

import org.openqa.selenium.By;

import base.Browser;

public class HomePage {

	By notificationPopupBy = By.xpath("//div[@class='noti_wrap']");
	By noThanksLinkBy = By.xpath("//a[text()='No Thanks']");

	Browser browser;

	public HomePage(Browser browser) {
		this.browser = browser;
	}

	public void navigateToWeatherPage() {
		browser.waitForVisibilty(notificationPopupBy, "Notification Pop up", browser.LOADING_TIMEOUT);
	}
}
