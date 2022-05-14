package sampleOAuth2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import sailpoint.integration.Base64;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	public static String Log4JPath = null;
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		initApp();
		logger.info("APP_STARTED");
		Client client = ClientBuilder.newClient();
		String clientID = "8kGIvO6EhgTSZ5gbzJngPlGMp1Nc78X2";
		String clientSecret = "VfauMBmMJOvsMWs3";
		String tokenURL = "http://192.168.134.150:8080/identityiq/oauth2/oauth2/generateToken";
		String grantType = "client_credentials";
		MultivaluedMap<String, String> formData = new MultivaluedHashMap();

		formData.add("grant_type", grantType);

		String secret = "Basic " + Base64.encodeBytes(new String(clientID + ":" + clientSecret).getBytes()); 
		// we	// should 	// use
																												// Base64
																												// encode
																												// to
																												// encode
																												// client
																												// id
																												// and
																												// client
																												// secret
		Response response = (Response) client.target(tokenURL). // token URL to get access token
				request("application/json"). // JSON Request Type
				accept("application/json"). // Response access type - application/scim+json
				header("Authorization", secret) // Authorization header goes here
				.post(Entity.json(null)); // body with grant type

		String token = response.readEntity(String.class); // reading response as string format

		JSONObject jsonObject = new JSONObject(token);
		String accessToken = "Bearer " + jsonObject.getString("access_token");

		String apiURL = "http://192.168.134.150:8080/identityiq/oauth2/myOAuthAPI/test2";
		Response response1 = (Response) client.target(apiURL). // API URL goes here (e.g.
																// http://localhost:8080/identityiq/scim/v2/Applications/​
				request("application/json"). // Request type
				accept("application/json"). // Response access type - application/scim+json
				header("Authorization", accessToken).get(); // header with access token as authorization value

		String output = response1.readEntity(String.class); // reading response as string format
		logger.info("output:" + output);

		logger.info("APP_FINISHED");
	}

	public static String processJson(String token) {
		String clientID = "8kGIvO6EhgTSZ5gbzJngPlGMp1Nc78X2";
		JSONObject jsonToken = new JSONObject(token);
		String tokenString = "";
		// String tokenString = "";
		tokenString += clientID;
		tokenString += "." + jsonToken.getString("access_token");
		// tokenString+="."+jsonToken.getString("token_type");
		tokenString += "." + "bearer";

		tokenString += "." + jsonToken.getString("expires_in");

		logger.info("updated output:" + tokenString);

		String encodedString = Base64.encodeBytes(new String(tokenString).getBytes());
		encodedString = "Bearer." + encodedString;
		logger.info("encoded string: " + encodedString);
		return encodedString;
	}

	public static void testString(String authMethod, String regExSeparator, String authHeader) {
		String encodedHeader = authHeader.substring(authMethod.length() + 1);
		logger.info("encodedHeader:" + encodedHeader);
		// byte[] decodedBytes =
		// Base64.decode("OGtHSXZPNkVoZ1RTWjVnYnpKbmdQbEdNcDFOYzc4WDIuWTg2SkZrU0RRWmdBazhxMWczOVZXUHYvL0hoYzFHOFdrV2VUZDdpcysyNCtnTVc2YUpMcExUWDQ3UHFXWmpmZ2xpV0tNNDFJbTVvZAowMEZBemNaZVFVR3RnczRvdFpMdnNJU0s4L3FCZng4bzZCd1MxRUhVL0tsb0Vjc0lMT3hGSnMwN0dncFRJQlNCTS9UZUdZbG42VWJRCnQwZE1pL2NVVWhjR21lcnlNMDA9");
		byte[] decodedBytes = Base64.decode(encodedHeader);
		try {
			String decodedAuthHeader = new String(decodedBytes, "UTF-8");
			logger.info("decodedHeader:" + decodedAuthHeader);
			String[] parts = decodedAuthHeader.split(regExSeparator, 2);
			logger.info("check_post_6.3");
			if (null != parts) {
				logger.info("Parts length:" + parts.length);
				for (int i = 0; i < parts.length; i++) {
					logger.info("Part :" + i + " " + parts[i]);
				}
			} else {
				logger.info("parts is null");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getLog4JPath() {
		return Log4JPath;
	}

	public static void setLog4JPath(String log4jPath) {
		Log4JPath = log4jPath;
	}

	public static Properties getApplicationProperties() {
		App application = new App();

		InputStream inputStream = null;
		Properties applicationProp = null;
		try {
			inputStream = new FileInputStream(new File("./resources/application.properties"));
			applicationProp = new Properties();
			applicationProp.load(inputStream);
			inputStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return applicationProp;
	}

	public static void initApp() {
		Properties applicationProperties = getApplicationProperties();
		setLog4JPath(applicationProperties.getProperty("LOG4J_PATH"));
		loadLog4j();
	}

	private static void loadLog4j() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(new File(getLog4JPath()).toURI());
	}
}