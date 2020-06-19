package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentHTMLReporter {

	static Logger log = LogManager.getLogger();

	ExtentReports extentReports;
	public ExtentHTMLReporter() {
		extentReports = configureReporter();
	}

	ExtentSparkReporter extentSparkReporter;

	/**
	 * Method to configure the extent html report
	 * @return extentReports
	 */
	public ExtentReports configureReporter() {
		extentSparkReporter = new ExtentSparkReporter("test_results/result.html");
		extentSparkReporter.loadXMLConfig("./src/test/resources/extent-report-config.xml");
		extentReports = new ExtentReports();
		extentReports.attachReporter(extentSparkReporter);
		return extentReports;
	}

	/**
	 * Method to start the test case in report
	 * @param testName
	 * @param description
	 * @return ExtentTest
	 */
	public ExtentTest startTestCase(String testName, String description) {
		return extentReports.createTest(testName, description);
	}

	/**
	 * Method to log the messages in the report
	 * @param extentTest - ExtentTest
	 * @param status - Status
	 * @param message - Status
	 */
	public static void reportLog(ExtentTest extentTest, Status status, String message) {
		String logDescription = message.replaceAll("\\<.*?\\>", "");
		switch (status) {
		case PASS:
			extentTest.pass(message);
			log.info(logDescription);
			break;
		case FAIL:
			extentTest.fail(message);
			log.warn(logDescription);
			break;
		case WARNING:
			extentTest.warning(message);
			log.warn(logDescription);
			break;
		case ERROR:
			extentTest.error(message);
			log.error(logDescription);
			break;
		case SKIP:
			extentTest.skip(message);
			log.warn(logDescription);
			break;
		default:
			extentTest.info(message);
			log.info(logDescription);
			break;
		}

	}
	
	public void flushResult() {
		extentReports.flush();
	}
}
