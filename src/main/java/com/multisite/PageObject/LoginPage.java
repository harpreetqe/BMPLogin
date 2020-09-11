package com.multisite.PageObject;

import com.multisite.Library.AppLibrary;

public class LoginPage
{
AppLibrary appLibrary;
public static String userNameInput="xpath://input[contains(@name,'Email')]";
public static String userPassInput="xpath://input[contains(@name,'Password')]";
public static String submitButton="xpath://button[contains(@class,'signInButton')]";
public static String homeMenu= "xpath:(//a[@class='nav-link dropdown-toggle'])[1]";

public LoginPage(AppLibrary appLibrary) 

{
this.appLibrary=appLibrary;
appLibrary.getCurrentDriverInstance();
}
public LoginPage login(String username, String password) throws Exception 
{
	appLibrary.enterText(userNameInput, username);
	appLibrary.enterText(userPassInput, password);
	appLibrary.clickElement(submitButton);
	return new LoginPage(appLibrary);

}
public LoginPage verifydashboard ()
{
	appLibrary.findElement(homeMenu);
	return new LoginPage(appLibrary);}
}
