package base;

import static utils.ExtentHTMLReporter.reportLog;

import org.apache.commons.lang3.Validate;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.TestProperties;

public class API {
	ExtentTest extentTest;

	public API(ExtentTest extentTest) {
		this.extentTest = extentTest;
	}

	String searchCity = System.getProperty("City");
	String apiKey = TestProperties.getProperties("API_Key");

	public Response getResponse() {

		RestAssured.baseURI = TestProperties.getProperties("BaseURI");
		RestAssured.basePath = TestProperties.getProperties("BasePath");
		if (searchCity == null || searchCity.isEmpty()) {
			searchCity = TestProperties.getProperties("city");
			reportLog(extentTest, Status.WARNING,
					"Search City is not provided, checking for default city : <b>" + searchCity + "</b>");
		} else {
			reportLog(extentTest, Status.PASS, "Search City is: " + searchCity);
		}
		RequestSpecification requestSpecification = new RequestSpecBuilder().addQueryParam("q", searchCity)
				.addQueryParam("appid", apiKey).build();

		Response response = RestAssured.given(requestSpecification).get();
		Validate.isTrue(response.statusCode() == 200,
				"The actual status code" + response.statusCode() + " does not match 200 code");
		reportLog(extentTest, Status.PASS, "Successfully retrieved the response from the API ");
		return response;
	}

	public void getWeatherInfoFromAPI() {
		getResponse();
	}

}
