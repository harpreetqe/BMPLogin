package com.multisite.Library;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import junit.framework.Assert;

public class AppLibrary {

	public final long GLOBALTIMEOUT = 20;
	private WebDriver driver;
	private Configuration config;
	public String baseUrl;
	public String browser;
	public String device;
	private String currentTestName;
	private String currentSessionID;

	public AppLibrary(String testName) {
		this.currentTestName = testName;
		this.config = new Configuration();
	}

	public Configuration getConfig() {
		return config;
	}

	public WebDriver getDriverInstance() throws MalformedURLException {
		DesiredCapabilities caps = new DesiredCapabilities();
		String browserVersion, os, browserStackOSVersion, remoteGridUrl, environment;

		this.browser = config.getBrowserName();
		baseUrl = config.getURL();
		environment = config.getExecutionEnvironment();

		switch (environment) {

		case "browserstack":
			browserStackOSVersion = config.getBrowserStackOSVersion();
			browserVersion = config.getBrowserVersion();
			os = config.getOS();

			if (config.getBrowserName().equalsIgnoreCase("IE")) {
				caps.setCapability("browser", "IE");
			} else if (config.getBrowserName().equalsIgnoreCase("GCH")
					|| config.getBrowserName().equalsIgnoreCase("chrome")) {
				caps.setCapability("browser", "Chrome");
			} else if (config.getBrowserName().equalsIgnoreCase("safari")) {
				caps.setCapability("browser", "Safari");
			} else {
				caps.setCapability("browser", "Firefox");
			}

			if (browserVersion != null && !browserVersion.equals("") && !browserVersion.equals("latest")) {
				caps.setCapability("browser_version", browserVersion);
			}

			if (browserStackOSVersion != null) {
				caps.setCapability("os", os);
				if (os.toLowerCase().startsWith("win")) {
					caps.setCapability("os", "Windows");
				} else if (os.toLowerCase().startsWith("mac-") || os.toLowerCase().startsWith("os x-")) {
					caps.setCapability("os", "OS X");
				}

				if (os.equalsIgnoreCase("win7")) {
					browserStackOSVersion = "7";
				} else if (os.equalsIgnoreCase("win8")) {
					browserStackOSVersion = "8";
				} else if (os.equalsIgnoreCase("win8.1") || os.equalsIgnoreCase("win8_1")) {
					browserStackOSVersion = "8.1";
				} else if (os.toLowerCase().startsWith("mac-") || os.toLowerCase().startsWith("os x-")) {
					browserStackOSVersion = os.split("-")[1];
				}
				caps.setCapability("os_version", browserStackOSVersion);
			}
			caps.setCapability("resolution", "1920x1080");
			caps.setCapability("browserstack.debug", "true");
			caps.setCapability("build", System.getProperty("Build"));
			caps.setCapability("project", System.getProperty("Suite"));
			caps.setCapability("name", currentTestName);

			try {
				driver = new RemoteWebDriver(new URL("http://" + config.getBrowserStackUserName() + ":"
						+ config.getBrowserStackAuthKey() + "@hub.browserstack.com/wd/hub"), caps);
				((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			} catch (Exception e) {
				autoLogger("Issue creating new driver instance due to following error: " + e.getMessage() + "\n"
						+ e.getStackTrace());
				throw e;
			}

			break;

		case "seleniumgrid":
			autoLogger("Remote execution set up on URL: " + config.getRemoteGridUrl());
			remoteGridUrl = config.getRemoteGridUrl();
			caps.setBrowserName("chrome");
			caps.setPlatform(Platform.LINUX);
			String url = "http://" + remoteGridUrl + ":4444/wd/hub";
			autoLogger("===================================" + "\n" + "URL:" + url);
			driver = new RemoteWebDriver(new URL(url), caps);
			((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			break;

		case "local":

			if (config.getBrowserName().equalsIgnoreCase("IE")) {
				String driverPath = config.getDriverAgent();
				if ((driverPath == null) || (driverPath.trim().length() == 0)) {
					driverPath = "IEDriverServer.exe";
				}
				System.setProperty("webdriver.ie.driver", driverPath);
				DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
				capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
						false);
				capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
				capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
				capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
				capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);

				driver = new InternetExplorerDriver(new InternetExplorerOptions(capabilities));

			} else if (config.getBrowserName().equalsIgnoreCase("GCH")
					|| config.getBrowserName().equalsIgnoreCase("chrome")) {
				String driverPath = config.getDriverAgent();
				if ((driverPath == null) || (driverPath.trim().length() == 0)) {
					driverPath = "chromedriver.exe";
				}
				System.setProperty("webdriver.chrome.driver", driverPath);
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--test-type");
				options.addArguments("--disable-extensions");
				options.addArguments("--start-maximized");
				driver = new ChromeDriver(options);
			} else {
				System.setProperty("webdriver.firefox.profile", "default");
				driver = new FirefoxDriver();
			}
			break;
		}

		driver.manage().timeouts().implicitlyWait(GLOBALTIMEOUT, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}

	public void launchApp() {
		// Delete cookies and Launch the Application
		driver.manage().deleteAllCookies();
		baseUrl = config.getURL();
		driver.get(baseUrl);

		// Maximize the browser
		driver.manage().window().maximize();
		waitForPageToLoad();
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void launchApp(String url) {
		// Delete cookies and Launch the Application
		driver.manage().deleteAllCookies();

		driver.get(url);
		waitForPageToLoad();

		// Maximize the browser
		driver.manage().window().maximize();
	}

	/**
	 * Get Driver Instance
	 */
	public WebDriver getCurrentDriverInstance() {
		return driver;
	}

	/**
	 * Closes the Browser
	 */
	public void closeBrowser() {
		if (driver != null)
			driver.quit();
	}

	public By getLocatorBy(String locator) {
		By locatorBy = null;
		String string = locator;
		String[] parts = string.split(":");
		String type = parts[0]; // 004
		String object = parts[1];

		if (type.equals("id")) {
			locatorBy = By.id(object);
		} else if (type.equals("name")) {
			locatorBy = By.name(object);
		} else if (type.equals("class")) {
			locatorBy = By.className(object);
		} else if (type.equals("link")) {
			locatorBy = By.linkText(object);
		} else if (type.equals("partiallink")) {
			locatorBy = By.partialLinkText(object);
		} else if (type.equals("css")) {
			locatorBy = By.cssSelector(object);
		} else if (type.equals("xpath")) {
			locatorBy = By.xpath(object);
		} else {
			autoLogger("Please provide correct element locating strategy" + locator);
			throw new RuntimeException("Please provide correct element locating strategy" + locator);
		}
		return locatorBy;
	}

	public WebElement getElement(String locatorString, Boolean verifyAbsent, Boolean jsClick, Boolean visibility) {
		By locatorBy = null;
		WebDriverWait wait = new WebDriverWait(driver, 30);
		WebElement element = null;

		autoLogger("Finding element using: " + locatorString);
		locatorBy = getLocatorBy(locatorString);

		try {
			element = driver.findElement(locatorBy);

			if (jsClick)
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		} catch (Exception e) {
			if (verifyAbsent && element == null)
				autoLogger("Good that element is not found");
			else
				throw new RuntimeException("Element not found: " + locatorString);
		}

		if (verifyAbsent && element != null)
			org.testng.Assert.assertTrue(false, "Expected element to be absent, but it was found on the page");

		if (visibility)
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(locatorBy));

		return element;
	}

	public WebElement findElement(String locatorString) {
		WebElement element = getElement(locatorString, false, false, false);
		return element;
	}

	public void verifyAbsent(String locatorString, long timeOut) {
		driver.manage().timeouts().implicitlyWait(timeOut, TimeUnit.SECONDS);
		getElement(locatorString, true, false, false);
		driver.manage().timeouts().implicitlyWait(GLOBALTIMEOUT, TimeUnit.SECONDS);
	}

	public WebElement clickByJavascript(String objectLocator) throws Exception {
		WebElement element = getElement(objectLocator, false, true, false);
		return element;
	}

	public void waitUntilElementDisplayed(String locatorString) {
		getElement(locatorString, false, false, true);
	}

	public List<WebElement> findElements(String locatorString) {
		By locatorBy = null;
		List<WebElement> element = null;
		locatorBy = getLocatorBy(locatorString);
		element = driver.findElements(locatorBy);
		return element;
	}

	public void selectElement(WebElement element, String option) {
		Select select = new Select(element);
		select.selectByVisibleText(option);
	}

	public void selectByPartOfVisibleText(WebElement element, String value) {
		boolean flag = true;
		List<WebElement> optionElements = element.findElements(By.tagName("option"));
		Select select = new Select(element);
		for (WebElement optionElement : optionElements) {
			if (optionElement.getText().contains(value)) {
				String optionIndex = optionElement.getAttribute("index");
				select.selectByIndex(Integer.parseInt(optionIndex));
				flag = false;
				break;
			}
		}
		if (flag) {
			Assert.assertTrue("Option " + value + " was not found in the select", false);
		}
	}

	public void verifyElement(String locatorString, boolean checkVisibility, long timeOutInSeconds) throws Exception {
		if (checkVisibility) {
			boolean isDisplayed = (findElement(locatorString).isDisplayed());
			if (isDisplayed == false) {
				throw new ElementNotVisibleException("Element Not Visible Exception");
			}
		} else {
			driver.manage().timeouts().implicitlyWait(timeOutInSeconds, TimeUnit.SECONDS);
			findElement(locatorString);
		}
		driver.manage().timeouts().implicitlyWait(GLOBALTIMEOUT, TimeUnit.SECONDS);
	}

	public void sleep(long milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getCurrentSessionID() {
		return currentSessionID;
	}

	public void waitForElementClickable(WebElement element, long timeOut) {
		new WebDriverWait(driver, timeOut).until(ExpectedConditions.elementToBeClickable(element));
	}

	public void waitForElementVisible(WebElement element, long timeOut) {
		new WebDriverWait(driver, timeOut).until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForPageToLoad() {
		new WebDriverWait(driver, GLOBALTIMEOUT).until(webDriver -> ((JavascriptExecutor) webDriver)
				.executeScript("return document.readyState").equals("complete"));
	}

	public void selectDeselectCheckBox(String locator, boolean selectCheckBox) {

		if (selectCheckBox) {
			if (!findElement(locator).isSelected())
				findElement(locator).click();
		} else if (findElement(locator).isSelected())
			findElement(locator).click();
	}

	public void enterText(String locator, String text) throws Exception {
		findElement(locator).click();
		findElement(locator).clear();
		findElement(locator).sendKeys(text);
	}

	public boolean verifyCheckBox(String locator) {
		return findElement(locator).isSelected();
	}

	public void waitForNavigation(String url) {
		int counter = 10;
		for (; counter > 0; counter--) {
			if (driver.getCurrentUrl().contains(url)) {
				break;
			} else {
				sleep(10000);
			}
		}
	}

	public void switchToWindow(int windowNo) {
		Set<String> set = driver.getWindowHandles();
		String windowHandle = null;
		autoLogger("Current no. of windows are: " + set.size());
		if (windowNo <= set.size()) {
			ArrayList<String> windows = new ArrayList<String>(set);
			windowHandle = windows.get(windowNo - 1);
		}

		if (windowHandle != null) {
			driver.switchTo().window(windowHandle);
		} else {
			throw new RuntimeException("Specified window not available");
		}
	}

	public String getFormattedDate() {
		return getDate().replaceAll("/", "_").replaceAll(":", "_").replaceAll(" ", "_");
	}

	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
		Date date = new Date();
		autoLogger(dateFormat.format(date));
		return dateFormat.format(date);
	}

	public void verifyTextField(String locator, String value) {
		if (value.equalsIgnoreCase("NA")) {
			Assert.assertEquals(findElement(locator).getAttribute("value"), "");
		} else {
			Assert.assertEquals(findElement(locator).getAttribute("value"), value);
		}
	}

	public boolean waitTillElementLoaded(String locator) {
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		int counter = 10;
		do {
			try {
				if (findElement(locator) != null) {
					return true;
				} else {
					sleep(1000);
					counter--;
				}
			} catch (Exception e) {
				sleep(3000);
				counter--;
				continue;
			}
		} while (counter > 0);
		driver.manage().timeouts().implicitlyWait(GLOBALTIMEOUT, TimeUnit.SECONDS);
		throw new RuntimeException("element was not loaded:" + locator);
	}

	public static void autoLogger(String message) {
		Reporter.log(message, true);
	}

	public void getScreenshot(String name) throws IOException {
		driver = getCurrentDriverInstance();
		String path = "screenshots/" + name;
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(src, new File(path));
		autoLogger("screenshot at :" + path);
		autoLogger("screenshot for " + name + " available at :" + path);
	}
	public void clickElement(String locator) throws Exception {
		findElement(locator).click();
	}
}