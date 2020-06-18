package base;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentTest;

import utils.ExtentHTMLReporter;
import utils.TestProperties;

public class TestBase {
	
	protected final String URL = TestProperties.getProperties("URL");
	protected Logger log = LogManager.getLogger();
	protected ExtentHTMLReporter extentReporter = new ExtentHTMLReporter();
	protected ExtentTest extentTest;

}
