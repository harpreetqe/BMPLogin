package com.multisite.Regression;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.multisite.Library.AppLibrary;
import com.multisite.Library.TestBase;
import com.multisite.PageObject.LoginPage;

public class LoginPageTest extends TestBase{
@BeforeClass
public void setUp()
{
appLibrary=new AppLibrary("LoginPage");

}
@Test
public void loginPage() throws Exception {
	appLibrary.getDriverInstance();
	appLibrary.launchApp();
	new LoginPage(appLibrary).login("harpreet.singh@thinkitive.com", "preet143");
	new LoginPage(appLibrary).verifydashboard();
	
}
}
