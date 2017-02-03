package com.testframework.genericfunctions;

import static com.testframework.genericfunctions.AutomationTool.selenium;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.*;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * *************************************************************************************
 */
/*                                                                                      */
 /* Class:   SeleniumWD.                                                                 */
 /* Purpose: Extends base AutomationTool Class, for GenericFunctions implementation in   */
 /*          Selenium WebDriver.                                                         */
 /* Author:  Mark Chatham.                                                               */
/**
 * *************************************************************************************
 */
class SeleniumWD extends AutomationTool {
/////////////////////////////////////////////////////////////////////////////////
// selenium Object.
/////////////////////////////////////////////////////////////////////////////////

    public static org.openqa.selenium.WebDriver selenium;
    public static org.openqa.selenium.WebElement webElement;

//    public String SavedTxt = "";
    /**
     * **********************************************************************************************
     */
    /* Helper Functions.                                                                             */
    /**
     * **********************************************************************************************
     */
    protected String getTableRow(String strTable, String strRowNumber) {
        return "";
    }

    protected Boolean isElementPresent(String strElement) {
        String strCopy = "";
        //  System.out.println("Point 0." + strElement);
        webElement = null;
        // strip []
        //strip @
        // strip //
        // strip *
        //stip "
        //strip '
        // strip /p
        //strip /a
        //strip div[?]

        // id, name, etc. -> "<--->" OR '<--->' 
        try {
            strCopy = strElement.replace("name=", "");
            webElement = selenium.findElement(By.name(strCopy));
        } catch (org.openqa.selenium.NoSuchElementException e1) {
            //       System.out.println("Point 1." + e1.getMessage());
            try {
                strCopy = strElement.replace("id=", "");
                webElement = selenium.findElement(By.id(strCopy));
            } catch (org.openqa.selenium.NoSuchElementException e2) {
                //                      System.out.println("Point 2." + e2.getMessage());

                try {
                    webElement = selenium.findElement(By.xpath(strElement));
                } catch (org.openqa.selenium.NoSuchElementException e3) {
                    //                         System.out.println("Point 3." + e3.getMessage());

                    try {
                        webElement = selenium.findElement(By.linkText(strElement));
                    } catch (org.openqa.selenium.NoSuchElementException e4) {

                        //                            System.out.println("Point 4." + e4.getMessage());
                        try {
                            webElement = selenium.findElement(By.tagName(strElement));
                        } catch (org.openqa.selenium.NoSuchElementException e5) {
                            //                               System.out.println("Point 5." + e5.getMessage());

                            try {
                                //                      System.out.println("Point 5.1.");
                                webElement = selenium.findElement(By.cssSelector(strElement));
                                //                        System.out.println("Point 5.2.");
                            } catch (org.openqa.selenium.NoSuchElementException e6) {
                                //                                     System.out.println("Point 6." + e6.getMessage());

                                return false;
                            }
                        }
                    }
                }
            }
        }
        //  System.out.println("Point 7. Returning True.");
        return true;
    }

    /**
     * **********************************************************************************************
     */
    /* Selenium object Functions.                                                                    */
    /**
     * **********************************************************************************************
     */
    public Boolean toolSetup(String strBrowser, String strURL) {
        switch (strBrowser) {
            case "*iexplore": {
                try {
                String ie = "./IEDriverServer.exe";
                FileInputStream fis = new FileInputStream(ie);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                System.setProperty("webdriver.ie.driver", ie);

                DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                selenium = new InternetExplorerDriver(capabilities);
                }
                catch(FileNotFoundException fn) {
                    Logger.getLogger(SeleniumWD.class.getName()).log(Level.SEVERE, null, fn);
                }
                //       selenium = new InternetExplorerDriver();
            }
            case "*iexploreproxy":
            case "*iehta":
            case "*piiexplore":
                break;
            case "*firefox": {
                selenium = new FirefoxDriver();
            }
            case "*firefox2":
            case "*firefox3":
            case "*firefoxproxy":
            case "*pifirefox":
            case "*firefoxchrome": {
                final String firebugPath = "./firebug-2.0.3.xpi";
                FirefoxProfile profile = new FirefoxProfile();
                try {
                    profile.addExtension(new File(firebugPath));
                    profile.setPreference("extensions.firebug.currentVersion", "2.0.3");
                } catch (IOException ex) {
                    Logger.getLogger(SeleniumWD.class.getName()).log(Level.SEVERE, null, ex);
                }
                selenium = new FirefoxDriver(profile);
            }
            break;
            case "*googlechrome":
            case "*chrome": {
                try {
                String chrome = "./chromedriver.exe";
                FileInputStream fis = new FileInputStream(chrome);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                
                System.setProperty("webdriver.chrome.driver", chrome);
                ChromeOptions options = new ChromeOptions();
                options.addArguments("test-type");
                selenium = new ChromeDriver(options);
                }
                catch(FileNotFoundException fn) {
                    Logger.getLogger(SeleniumWD.class.getName()).log(Level.SEVERE, null, fn);
                }
            }
            break;
            case "*konqueror": {
                // Currently no Selenium WebDriver Driver available.
            }
            break;
            case "*opera": {
                // selenium = new OperaDriver();
            }
            case "*safari":
            case "*safariproxy": {
                selenium = new SafariDriver();
            }
            break;
            case "*custom":
            case "*html-unit":
            case "*mock": {
                //  selenium = new HtmlUnitDriver(true);
            }
            break;
            default: {
                // selenium = new FirefoxDriver();
            }
            break;
        }

        return true;
    }

    public Boolean toolStart() {
        // Not necessary in Selenium WebDriver.
        return true;
    }

    public Boolean toolOpenURL(String strURL) {
        if (selenium == null) {
            return false;
        }

        selenium.get(strURL);

        window_AddWindow();

        return true;
    }

    public Boolean toolSleep(int iSeconds) {
        for (int i = 0; i <= iSeconds; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }

        return true;
    }

    /**
     * **********************************************************************************************
     */
    /* Selenium Window Functions.                                                                    */
    /**
     * **********************************************************************************************
     */
    public Boolean window_AddWindow() {
        String WindowTitles = selenium.getWindowHandle();

        // System.out.println(WindowTitles);
        SeleniumWindow_Object newWindow = new SeleniumWindow_Object(String.valueOf(iWindow_Counter), WindowTitles);
        alWindowArray.add(newWindow);
        //   System.out.println(alWindowArray);

        newWindow = null;
        System.gc();

        iWindow_Counter++;
        // System.out.println(iWindow_Counter);

        return true;
    }

    public Boolean window_DeleteWindow() {
        alWindowArray.remove(iWindow_Counter - 1);
        iWindow_Counter--;
        //   System.out.println(iWindow_Counter);
        return true;
    }

    public Boolean window_DisplayWindow() {

        for (int i = 0; i < iWindow_Counter; i++) {
            System.out.println("Display All Windows:" + ((SeleniumWindow_Object) alWindowArray.get(i)).toString());
        }

        return true;
    }

    /**
     * **********************************************************************************************
     */
    /* Generic Functions.                                                                            */
    /**
     * **********************************************************************************************
     */
    public Boolean genfunc_SelectWindow(String strWindow) {
        System.out.println(strWindow);
        //System.out.println(String.valueOf(alWindowArray.get(Integer.parseInt(strWindow))));
        SeleniumWindow_Object newWindow = (SeleniumWindow_Object) alWindowArray.get(Integer.parseInt(strWindow));
        //  System.out.println(newWindow.WindowTitle);
        //selenium.switchTo().frame(newWindow.WindowTitle);

        //String handle = "";
        Set<String> windows = selenium.getWindowHandles();
        for (String handle : windows) {
            if (handle.equals(newWindow.WindowTitle)) {
                // System.out.println("found");
                selenium.switchTo().window(handle);
            }
        }

        //newWindow = null;
        //System.gc();
        return true;
    }

    public Boolean genfunc_Click(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();

        return true;
    }

    public Boolean genfunc_SetText(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        if (strValue.equalsIgnoreCase("#N/A") || strValue.equalsIgnoreCase("#Blank")) {
            return true;
        }

        webElement.sendKeys(strValue);

        return true;
    }

    public Boolean genfunc_SetPath(String strObject, String strValue) {
        try {
            Keyboard keyboard = new Keyboard();
            keyboard.type(strValue);
            keyboard.type("\n");
            return true;
        } catch (Exception ex) {
            Logger.getLogger(SeleniumWD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public Boolean genfunc_GetText(String strObject, String strValue) {

        if (isElementPresent(strObject) == false) {
            System.out.println("element not present");
            return false;
        }
        if (strValue.equalsIgnoreCase("#Blank")) {
            String strElementText = webElement.getText();
            strElementText = strElementText.trim();

            String strElementValue = "";

            if (webElement.getAttribute("value") != null) {
                strElementValue = webElement.getAttribute("value");
                strElementValue = strElementValue.trim();
            }

            if (strElementText.contentEquals("")) {
                System.out.println("PASSED - " + " '" + strElementText + "' MATCHES '" + "'");
                return true;
            } else if (strElementValue.contentEquals("")) {
                System.out.println("PASSED - " + " '" + strElementValue + "' MATCHES '" + "'");
                return true;
            } else {
                System.out.println("FAILED - " + " '" + strElementText + "' DOES NOT MATCH '" + "'");
                return false;
            }
        }

        String strElementText = webElement.getText();
        strElementText = strElementText.trim();

        String strElementValue = "";

        if (webElement.getAttribute("value") != null) {
            strElementValue = webElement.getAttribute("value");
            strElementValue = strElementValue.trim();
        }

        if (strElementText.contentEquals(strValue)) {
            System.out.println("PASSED - " + " '" + strElementText + "' MATCHES '" + strValue + "'");
            return true;
        } else if (strElementValue.contentEquals(strValue)) {
            System.out.println("PASSED - " + " '" + strElementValue + "' MATCHES '" + strValue + "'");
            return true;
        } else {
            System.out.println("FAILED - " + " '" + strElementText + "' DOES NOT MATCH '" + strValue + "'");
            return false;
        }

    }

    public Boolean genfunc_GetTextNoOutput(String strObject, String strValue) {

        if (isElementPresent(strObject) == false) {
            System.out.println("not present");
            return false;
        }

        String strElementText = webElement.getText();
        strElementText = strElementText.trim();

        String strElementValue = "";

        if (webElement.getAttribute("value") != null) {
            strElementValue = webElement.getAttribute("value");
            strElementValue = strElementValue.trim();
        }

        if (strElementText.contentEquals(strValue)) {
            return true;
        } else if (strElementValue.contentEquals(strValue)) {
            return true;
        } else {
            return false;
        }

    }

    public Boolean genfunc_SetComboboxValueByText(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();

        return true;
    }

    public Boolean genfunc_IsTextOnPage(String strValue) {
        return true; //selenium.isTextPresent(strValue);
    }

    public Boolean genfunc_IsObjectVisible(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.isDisplayed();

        return true; //selenium.isElementPresent(strObject);
    }

    public Boolean genfunc_ClickLink(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();

        return true;
    }

    public Boolean genfunc_ClickTab(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        Actions ChangeTab = new Actions(selenium);
        ChangeTab.sendKeys(Keys.chord(Keys.CONTROL, Keys.TAB)).perform();
        //new Actions(selenium).sendKeys(selenium.findElement(By.tagName("html")), Keys.CONTROL).sendKeys(selenium.findElement(By.tagName("html")),Keys.NUMPAD2).build().perform();

        return true;
    }

    public Boolean genfunc_MouseDownAndClick(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();

        return true;
    }

    public Boolean genfunc_MouseDownAtAndClickAt(String strObject, String strClickAt) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();

        return true;
    }

    public Boolean genfunc_MouseDownAndDoubleClick(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();
        webElement.click();

        return true;
    }

    public Boolean genfunc_MouseDownAtAndDoubleClickAt(String strObject, String strClickAt) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();
        webElement.click();

        return true;
    }

    public Boolean genfunc_SelectTableRow(String strObject, String strRowNumber) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();

        return true;
    }

    public Boolean genfunc_ClickTableRow(String strObject, String strRowNumber) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();

        return true;
    }

    public Boolean genfunc_DoubleClickTableRow(String strObject, String strRowNumber) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.click();
        webElement.click();

        return true;
    }

    public Boolean genfunc_ExplicitWait(String strObject) {
        try {
            WebDriverWait waitElement = new WebDriverWait(selenium, 300);
            waitElement.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(strObject)));
        } catch (org.openqa.selenium.TimeoutException v) {
            return false;
        }
        //WebElement waitElement = (new WebDriverWait(selenium, 10)).until(ExpectedConditions.elementToBeClickable(webElement));

        return true;
    }

    public Boolean genfunc_Highlight(String strObject) {
        return true;
    }

    public Boolean genfunc_ClickAndHold(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        Actions ClickHold = new Actions(selenium);
        ClickHold.keyDown(Keys.LEFT_CONTROL)
                .click(webElement)
                .keyUp(Keys.LEFT_CONTROL)
                .perform();

        return true;
    }

    public Boolean genfunc_isElementPresentChk(String strObject) {
        try {
            webElement = selenium.findElement(By.xpath(strObject));

        } catch (org.openqa.selenium.InvalidSelectorException e) {
            return false;
        } catch (org.openqa.selenium.NoSuchElementException f) {
            return false;
        }

        if (webElement.isDisplayed() == true) {
            return true;
        } else {

            return false;
        }
    }

    public Boolean genfunc_isElementPresent(String strObject) {
        try {
            webElement = selenium.findElement(By.xpath(strObject));
            //    isElementPresent(strObject);
        } catch (org.openqa.selenium.InvalidSelectorException e) {
            return false;
        } catch (org.openqa.selenium.NoSuchElementException f) {
            return false;
        }

        if (webElement.isDisplayed() == true) {
            return true;
        } else {

            return false;
        }
    }

    public Boolean genfunc_ClickOKOnAlertBox() {

        for (int i = 0; i < 15; i++) {
            try {
                selenium.switchTo().alert().accept();
                return true;
            } catch (NoAlertPresentException e) {
                this.toolSleep(1);
                // System.out.println("AAAAA");
            }
        }
        return false;
    }

    public Boolean genfunc_ClickOKOnAlertBoxSkip() {

        selenium.switchTo().alert().accept();
        return true;

    }

    public Boolean genfunc_ClickCancelOnAlertBox() {
        selenium.switchTo().alert().dismiss();

        return true;
    }

    public Boolean genfunc_isAlertBoxPresent() {
        try {
            selenium.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public Boolean genfunc_SendEnterKey(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.sendKeys("\13");

        return true;
    }

    public Boolean genfunc_ClearText(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.clear();

        return true;
    }

    public Boolean genfunc_SendControlA(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.sendKeys(Keys.LEFT_CONTROL + "a");

        return true;
    }

    public Boolean genfunc_SendControlC(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.sendKeys(Keys.LEFT_CONTROL + "c");

        return true;
    }

    public Boolean genfunc_SendControlV(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        webElement.sendKeys(Keys.LEFT_CONTROL + "v");

        return true;
    }

    public Boolean genfunc_waitForPresent(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        System.out.println("atart");
        //   webElement = selenium.findElement(By.xpath(strObject));

        System.out.println("pre-if");

//    if (webElement == null)
//            {
//                System.out.println("find by xpath null");
//            }
//                
//              //      webElement = selenium.findElement(By.xpath(strObject));
//                
//              //  catch (org.openqa.selenium.NoSuchElementException e3)
//    
//     {
        int i = 1;
        while (i > 0) // 
        {
            if (webElement.isDisplayed() != true) {
                i++;
            } else {
                i = 0;
            }
        }
        //}

        return true;

    }

    public Boolean genfunc_GetAttribute(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        String strElementValue = webElement.getAttribute("value");
        strElementValue = strElementValue.trim();

        if (strValue.equalsIgnoreCase("#N/A") || strValue.equalsIgnoreCase("#Blank")) {
            strValue = "";
        }

        if (strElementValue.contentEquals(strValue)) {
            System.out.println("PASSED - " + " '" + strElementValue + "' MATCHES '" + strValue + "'");
            return true;
        }
        System.out.println("FAILED - " + " '" + strElementValue + "' DOES NOT MATCH '" + strValue + "'");

        return false;

    }

    public Boolean genfunc_TakeScreenshot(String strValue) {
        try {
            File scrFile = ((TakesScreenshot) selenium).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(strValue)); //"C:\\Screenshots\\screenshot.png"
        } catch (Exception e) {
            System.out.println("Screenshot not captured.");
        }
        return true;
    }

    public Boolean genfunc_GetTextAlertBox(String strValue) {
        for (int i = 0; i < 15; i++) {
            try {
                selenium.switchTo().alert();
                break;
            } catch (NoAlertPresentException e) {
                this.toolSleep(1);
            }
        }
        String strAlertText = selenium.switchTo().alert().getText();
        strAlertText = strAlertText.trim();

        if (strAlertText.contentEquals(strValue)) {
            System.out.println("PASSED - " + " '" + strAlertText + "' MATCHES '" + strValue + "'");
            return true;
        } else {
            System.out.println("FAILED - " + " '" + strAlertText + "' DOES NOT MATCH '" + strValue + "'");
            return false;
        }
    }

    public Boolean genfunc_GetPlaceholder(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        String strElementValue = webElement.getAttribute("placeholder");
        strElementValue = strElementValue.trim();

        if (strElementValue.contentEquals(strValue)) {
            System.out.println("PASSED - " + " '" + strElementValue + "' MATCHES '" + strValue + "'");
            return true;
        }
        System.out.println("FAILED - " + " '" + strElementValue + "' DOES NOT MATCH '" + strValue + "'");

        return false;
    }

    //Used to test if buttons/textboxes are enabled/disabled
    public Boolean genfunc_isElementEnabled(String strObject) {

        if (isElementPresent(strObject) == false) {
            return false;
        }
        if ((webElement.isEnabled())) {
            try {
                if (webElement.getAttribute("disabled").equalsIgnoreCase("disabled")) {
                    return false;
                }
            } catch (java.lang.NullPointerException e) {
            }

            try {
                if (webElement.getAttribute("readonly").equalsIgnoreCase("true")) {
                    return false;
                }
            } catch (java.lang.NullPointerException f) {
            }
            if (webElement.isDisplayed()) {
                return true;
            }
        }
        return false;
    }

    public Boolean genfunc_isElementDisabled(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        if (webElement.isEnabled()) {
            System.out.println("FAILED - Element is enabled");
            return false;
        } else {
            System.out.println("PASSED - Element is disabled");
            return true;
        }
    }

    public Boolean genfunc_isElementSelected(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        if (webElement.isSelected()) {
            return true;
        }
        return false;

    }

    public Boolean genfunc_isElementNotSelected(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        if (webElement.isSelected() == false) {
            System.out.println("PASSED - Element is not selected");
            return true;
        } else {
            System.out.println("FAILED - Element is selected");
            return false;
        }
    }

    public Boolean genfunc_waitForPageToLoad(String strObject) {
        try {
            this.toolSleep(1);
            WebDriverWait wait = new WebDriverWait(selenium, 60);
            webElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(strObject)));

            return true;
        } catch (org.openqa.selenium.TimeoutException d) {
            return false;
        } catch (org.openqa.selenium.InvalidSelectorException e) {
            return false;
        } catch (org.openqa.selenium.NoSuchElementException f) {
            return false;
        }
    }
    ;

    //Save text function, gets text from an object and saves it to a variable. Compare text function below compares another objects text to this variable.  WD
    public String SavedTxt = "";

    public String genfunc_Txt() {
        return SavedTxt;
    }

    public Boolean genfunc_SaveText(String strObject) {
        if (isElementPresent(strObject) == false) {
            System.out.println("first if");
            return false;
        }

        String strElementText = webElement.getText();
        strElementText = strElementText.trim();

        SavedTxt = strElementText;

        return true;
    }

    //Compares and objects text with a previous objects text that has been saved using the saveText function.  WD
    public Boolean genfunc_CompareText(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        String strElementText = webElement.getText();
        strElementText = strElementText.trim();
        SavedTxt.trim();
        strElementText.trim();

        String strElementValue = "";
        if (webElement.getAttribute("value") != null) {
            strElementValue = webElement.getAttribute("value");
            strElementValue = strElementValue.trim();
        }
        if (strElementText.equals(SavedTxt)) {
            System.out.println("PASSED - " + " '" + strElementText + "' MATCHES '" + SavedTxt + "'");
            return true;
        } else if (strElementValue.contentEquals(SavedTxt)) {
            System.out.println("PASSED - " + " '" + strElementValue + "' MATCHES '" + SavedTxt + "'");
            return true;
        } else {
            System.out.println("FAILED - " + " '" + strElementText + "' DOES NOT MATCH '" + SavedTxt + "'");
            return false;
        }
    }

    public Boolean genfunc_CheckFormat(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        String strElementText = webElement.getText();
        strElementText = strElementText.trim();

        //strValue must be in regex form e.g. if strValue = VOX[0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9][0-9] will match VOX1502241014515
        if (strElementText.matches(strValue)) {
            System.out.println("PASSED - " + " '" + strElementText + "' MATCHES '" + strValue + "'");
            return true;
        }
        System.out.println("FAILED - " + " '" + strElementText + "' DOES NOT MATCHES '" + strValue + "'");
        return false;
    }

    public Boolean genfunc_Quit() {
        window_DeleteWindow();
        selenium.quit();
        return true;
    }

    @Override
    public Boolean Notepad() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean CommandPrompt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean genfunc_Close() {
        window_DeleteWindow();
        selenium.close();
        return true;
    }

    public Boolean genfunc_ScreenSize() {
        selenium.manage().window().maximize();
        return true;
    }

    public Boolean genfunc_SwitchToNewWindow() {
        SeleniumWindow_Object parentWindow = (SeleniumWindow_Object) alWindowArray.get(Integer.parseInt("0"));

        Set<String> windows = selenium.getWindowHandles();
        for (String handle : windows) {
            if (!handle.equals(parentWindow.WindowTitle)) {
                selenium.switchTo().window(handle);
            }
        }

        window_AddWindow();

        return true;
    }

    public Boolean genfunc_ScrollDown(String strObject, String strValue) {
        //((JavascriptExecutor)selenium).executeScript("window.scrollBy(0,500)", "");

        //((JavascriptExecutor)selenium).executeScript("window.scrollBy(0,document.body.scrollHeight);");
        // webElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.ARROW_DOWN));
        new Actions(selenium).sendKeys(Keys.PAGE_DOWN).perform();

        //selenium.findElement(By.xpath("//*[@id=\"pawsforvox-813161917\"]/div/div[2]/div/div[2]/div/div/div/div[2]/div[2]/input")).sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
        //webElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
        //JavascriptExecutor js = (JavascriptExecutor) selenium;
        // js.executeScript("document.body.style.zoom='90%'");
        return true;
    }

    public Boolean genfunc_waitForAngular() {

        selenium.manage().timeouts().setScriptTimeout(1800, TimeUnit.SECONDS);

        final String script = "var callback = arguments[arguments.length - 1];\n"
                + "var rootSelector = \'body\';\n"
                + "var el = document.querySelector(rootSelector);\n"
                + "\n"
                + "try {\n"
                + "    if (angular) {\n"
                + "        window.angular.getTestability(el).whenStable(callback);\n"
                + "    }\n"
                + "    else {\n"
                + "        callback();\n"
                + "    }\n"
                + "} catch (err) {\n"
                + "    callback(err.message);\n"
                + "}";

        ((JavascriptExecutor) selenium).executeAsyncScript(script, new Object[0]);
        return true;
    }

    @Override
    public Boolean genfunc_GetColour(String strObject, String strValue) {

        if (isElementPresent(strObject) == false) {
            System.out.println("element not present");
            return false;
        }

        String strElementColour = webElement.getCssValue("background-color");
        if (strValue.equalsIgnoreCase(strElementColour)) {
            System.out.println(strElementColour + " matches " + strValue);
            return true;
        }
        System.out.println(strElementColour + " does not match " + strValue);
        return false;
    }

    public Boolean genfunc_RefreshPage() {

        selenium.navigate().refresh();
        return true;
    }
}
