package base;

import static utils.ExtentHTMLReporter.reportLog;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.TestProperties;

public class API {
	ExtentTest extentTest;

	public API(ExtentTest extentTest) {
		this.extentTest = extentTest;
	}

	String apiKey = TestProperties.getProperties("API_Key");

	/**
	 * Method to get the API response for the city
	 * @param searchCity
	 * @param weatherKey
	 * @return response
	 */
	public Response getResponse(String searchCity, String weatherKey) {
		Response response = RestAssured.given(setupAPISepecifcations(searchCity, weatherKey)).log().all().get();
		Validate.isTrue(response.statusCode() == 200,
				"The actual status code" + response.statusCode() + " does not match 200 code");
		return response;
	}

	/**
	 * Method to set up the specifications for the API request; baseURI, basePath and Query parameters
	 * @param searchCity
	 * @param weatherKey
	 * @return requestSpecification
	 */
	public RequestSpecification setupAPISepecifcations(String searchCity, String weatherKey) {
		String baseURI = TestProperties.getProperties("BaseURI");
		String basePath = TestProperties.getProperties("BasePath");
		Map<String, String> mapOfQueryParameters = new HashMap<>();
		mapOfQueryParameters.put("q", searchCity);
		mapOfQueryParameters.put("appid", apiKey);
		if (weatherKey.equals(WeatherConstants.TEMP_IN_DEGREES)) {
			mapOfQueryParameters.put("units", "metric");
		} else if (weatherKey.equals(WeatherConstants.TEMP_IN_FAHRENHEIT)) {
			mapOfQueryParameters.put("units", "imperial");
		}
		RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(baseURI).setBasePath(basePath)
				.addQueryParams(mapOfQueryParameters).build();
		reportLog(extentTest, Status.PASS,
				"Request specifications were set up for the API Request for city : " + searchCity);
		return requestSpecification;
	}

	/**
	 * Method to get the value of weather condition for the city & weather key
	 * @param searchCity
	 * @param weatherKey
	 * @return weatherValue - value for the weatherKey
	 */
	public String getWeatherInfoFromAPISourceForCity(String searchCity, String weatherKey) {
		String weatherValue = "";
		Response response = getResponse(searchCity, weatherKey);
		JsonPath jsonPath = response.body().jsonPath();
		String actualCityName = jsonPath.getString("name");
		Validate.isTrue(actualCityName.equals(searchCity),
				"The response retrieved for the city : " + actualCityName + ".But given city is : " + searchCity);
		reportLog(extentTest, Status.PASS,
				"Successfully retrieved the response from the API for the city :<b> " + searchCity);
		if (weatherKey.equals(WeatherConstants.TEMP_IN_DEGREES)
				|| weatherKey.equals(WeatherConstants.TEMP_IN_FAHRENHEIT)) {
			weatherValue = jsonPath.getString("main.temp");
		} else if (weatherKey.equals(WeatherConstants.HUMIDITY)) {
			weatherValue = jsonPath.getString("main.humidity");
		} else {
			throw new IllegalArgumentException("Unsupported Weather Paramater: " + weatherKey);
		}
		return weatherValue;
	}
}
