package com.multisite.Library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	private String url;
	private String browserName;
	private String driverAgent;
	private String executionEnvironment;
	private String browserStackUserName;
	private String browserStackAuthKey;
	private String browserStackBrowserVersion;
	private String browserStackOSVersion;
	private String os;
	private String remoteGridUrl;
	private String browserVersion;
	private String deviceName;

	public Configuration() {
		final Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(new File("config.properties")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		browserName = System.getProperty("browserName") != null ? System.getProperty("browserName")
				: prop.getProperty("browserName");

		url = System.getProperty("instanceUrl") != null ? System.getProperty("instanceUrl")
				: prop.getProperty("instanceUrl");

		driverAgent = System.getProperty("driverAgent") != null ? System.getProperty("driverAgent")
				: prop.getProperty("driverAgent");

		executionEnvironment = System.getProperty("executionEnvironment") != null
				? System.getProperty("executionEnvironment") : prop.getProperty("executionEnvironment");

		remoteGridUrl = System.getProperty("remoteGridUrl") != null ? System.getProperty("remoteGridUrl")
				: prop.getProperty("remoteGridUrl");

		os = System.getProperty("os") != null ? System.getProperty("os") : prop.getProperty("os");

		browserStackUserName = System.getProperty("browserstackUsername") != null
				? System.getProperty("browserstackUsername") : prop.getProperty("browserstackUsername");

		browserStackAuthKey = System.getProperty("browserstackAuthkey") != null
				? System.getProperty("browserstackAuthkey") : prop.getProperty("browserstackAuthkey");

		browserStackBrowserVersion = System.getProperty("browserstackBrowserversion") != null
				? System.getProperty("browserstackBrowserversion") : prop.getProperty("browserstackBrowserversion");

		browserStackOSVersion = System.getProperty("browserstackOsversion") != null
				? System.getProperty("browserstackOsversion") : prop.getProperty("browserstackOsversion");

		AppLibrary.autoLogger("url:" + url);
		AppLibrary.autoLogger("browserName:" + browserName);
		AppLibrary.autoLogger("driverAgent:" + driverAgent);
		AppLibrary.autoLogger("executionEnvironment:" + executionEnvironment);
		AppLibrary.autoLogger("browserStackUserName:" + browserStackUserName);
		AppLibrary.autoLogger("browserStackAuthKey:" + browserStackAuthKey);
		AppLibrary.autoLogger("browserStackBrowserVersion:" + browserStackBrowserVersion);
		AppLibrary.autoLogger("browserStackOSVersion:" + browserStackOSVersion);
		AppLibrary.autoLogger("os:" + os);
	}

	public String getURL() {
		return url;
	}

	public String getBrowserName() {
		return browserName;
	}

	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	public String getBrowserStackUserName() {
		return browserStackUserName;
	}

	public String getBrowserStackAuthKey() {
		return browserStackAuthKey;
	}

	public String getBrowserStackBrowserVersion() {
		return browserStackBrowserVersion;
	}

	public String getBrowserStackOSVersion() {
		return browserStackOSVersion;
	}

	public String getRemoteGridUrl() {
		return remoteGridUrl;
	}

	public String getOS() {
		return os;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getDriverAgent() {
		return driverAgent;
	}
}