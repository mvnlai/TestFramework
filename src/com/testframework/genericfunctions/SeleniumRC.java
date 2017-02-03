package com.testframework.genericfunctions;

import static com.testframework.genericfunctions.AutomationTool.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

class SeleniumRC extends AutomationTool {
/////////////////////////////////////////////////////////////////////////////////
// selenium Object.
/////////////////////////////////////////////////////////////////////////////////

    /* public static com.thoughtworks.selenium.Selenium selenium;
    protected ArrayList alSeleniumWindowArray   = new ArrayList();
    protected ArrayList alXPathNameArray        = new ArrayList();
    protected int       iSeleniumWindow_Counter = 0;*/

    /* Helper Functions.                                                                    */

    protected String getTableRow(String strTable, String strRowNumber) {
        if (selenium.isElementPresent(strTable) == false) {
            return "";
        }
// If the Table is empty this      
        strTable = addQuotesToObjectIdentifier(strTable);
        if (selenium.isElementPresent("//div[@" + strTable + "]/descendant::div[contains(@class, 'x-grid3-body')][text()='Empty Text Node']") == true) {
            // Case 0: Table is empty
            return "";
        }

        String strTableObject = "";
// CASE 2: Multiple Rows returned. Look for Class "x-grid3-row-first" and rowIndex="".
        if (selenium.isElementPresent("//div[@" + strTable + "]/descendant::div[contains(@class, 'x-grid3-row') and contains(@rowIndex, '" + strRowNumber + "')]") == true) {
            strTableObject = "//div[@" + strTable + "]/descendant::div[contains(@class, 'x-grid3-row') and contains(@rowIndex, '" + strRowNumber + "')]";
        }
// CASE 3: 1 Row returned. Look for Class "x-grid3-row-first x-grid3-row-last"
        if (selenium.isElementPresent("//div[@" + strTable + "]/descendant::div[contains(@class, 'x-grid3-row-first x-grid3-row-last')]") == true) {
            strTableObject = "//div[@" + strTable + "]/descendant::div[contains(@class, 'x-grid3-row-first x-grid3-row-last')]";
        }
        return strTableObject;
    }

    protected Boolean isElementPresent(String strElement) {
        if (selenium.isElementPresent(strElement) == false) {
            System.out.println("Could not find Element - '" + strElement + "'.");
            return false;
        }

        return true;
    }

    /* Selenium object Functions.                                                                    */

    public Boolean toolSetup(String strBrowser, String strURL) {
        selenium = new DefaultSelenium("localhost", 4444, strBrowser, strURL);

        return true;
    }

    public Boolean toolStart() {
        if (selenium == null) {
            return false;
        }

        selenium.start();

        return true;
    }

    public Boolean toolOpenURL(String strURL) {
        if (selenium == null) {
            return false;
        }

        selenium.open(strURL);

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

    /* Selenium Window Functions.                                                                    */

    public Boolean window_AddWindow() {
        String[] WindowTitles = selenium.getAllWindowTitles();

        SeleniumWindow_Object newWindow = new SeleniumWindow_Object(String.valueOf(iWindow_Counter), WindowTitles[iWindow_Counter]);
        alWindowArray.add(newWindow);

        newWindow = null;
        System.gc();

        iWindow_Counter++;

        return true;
    }

    public Boolean window_DeleteWindow() {
        alWindowArray.remove(iWindow_Counter - 1);
        iWindow_Counter--;

        return true;
    }

    public Boolean window_DisplayWindow() {

        for (int i = 0; i < iWindow_Counter; i++) {
            System.out.println("Display All Windows:" + ((SeleniumWindow_Object) alWindowArray.get(i)).toString());
        }

        return true;
    }

    /* Generic Functions.                                                                            */

    public Boolean genfunc_SelectWindow(String strWindow) {
        selenium.selectWindow(strWindow);

        return true;
    }

    public Boolean genfunc_Click(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        selenium.click(strObject);

        return true;
    }

    public Boolean genfunc_SetText(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        selenium.type(strObject, strValue);

        return true;
    }

    public Boolean genfunc_SetPath(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        selenium.type(strObject, strValue);

        return true;
    }

    public Boolean genfunc_GetText(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        if (strValue.equalsIgnoreCase("#Blank")) {
            String strElementText = selenium.getText(strObject);
            strElementText = strElementText.trim();

            String strElementValue = "";

            /*            if (selenium.getAttribute("value") != null)
            {
                strElementValue = selenium.getAttribute("value");
                strElementValue = strElementValue.trim();
            }
             */
            if (strElementText.contentEquals("")) {
                System.out.println("PASSED - " + " '" + strElementText + "' MATCHES '" + "'");
                return true;
            } /*            else if (strElementValue.contentEquals(""))
            {
                System.out.println("PASSED - " + " '" + strElementValue + "' MATCHES '" + "'");
                return true;
            }
             */ else {
                System.out.println("FAILED - " + " '" + strElementText + "' DOES NOT MATCH '" + "'");
                return false;
            }
        }

        String strElementText = selenium.getText(strObject);
        strElementText = strElementText.trim();

        String strElementValue = "";

        /*        if (selenium.getAttribute("value") != null)
        {
            strElementValue = selenium.getAttribute("value");
            strElementValue = strElementValue.trim();
        }
         */
        if (strElementText.contentEquals(strValue)) {
            System.out.println("PASSED - " + " '" + strElementText + "' MATCHES '" + strValue + "'");
            return true;
        } /*        else if (strElementValue.contentEquals(strValue))
        {
           System.out.println("PASSED - " + " '" + strElementValue + "' MATCHES '" + strValue + "'");
            return true;
        }
         */ else {
            System.out.println("FAILED - " + " '" + strElementText + "' DOES NOT MATCH '" + strValue + "'");
            return false;
        }

    }

    public Boolean genfunc_SetComboboxValueByText(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        strObject = addQuotesToObjectIdentifier(strObject);
        selenium.click("//input[@" + strObject + "]/following-sibling::img[contains(@class, 'x-form-arrow-trigger')]");
        selenium.click("//div[contains(@class, 'x-combo-list')]/descendant::div[contains(@class, 'x-combo-list-item')][text()='" + strValue + "']");

        return true;
    }

    public Boolean genfunc_IsTextOnPage(String strValue) {
        return selenium.isTextPresent(strValue);
    }

    public Boolean genfunc_IsObjectVisible(String strObject) {
        return selenium.isElementPresent(strObject);
    }

    public Boolean genfunc_ClickLink(String strObject) {
        if (isElementPresent("link" + strObject) == false) {
            return false;
        }

        selenium.click("link=" + strObject);

        return true;
    }

    public Boolean genfunc_ClickTab(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        strObject = addQuotesToObjectIdentifier(strObject);
        selenium.mouseDownAt("//li[@" + strObject + "]/a[2]/em/span/span", "10,10");
        selenium.clickAt("//li[@" + strObject + "]/a[2]/em/span/span", "10,10");

        return true;
    }

    public Boolean genfunc_MouseDownAndClick(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        //strObject = addQuotesToObjectIdentifier(strObject);
        selenium.mouseDown(strObject);
        selenium.click(strObject);

        return true;
    }

    public Boolean genfunc_MouseDownAtAndClickAt(String strObject, String strClickAt) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        //strObject = addQuotesToObjectIdentifier(strObject);
        selenium.mouseDownAt(strObject, strClickAt);
        selenium.clickAt(strObject, strClickAt);

        return true;
    }

    public Boolean genfunc_MouseDownAndDoubleClick(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        //strObject = addQuotesToObjectIdentifier(strObject);
        selenium.mouseDown(strObject);
        selenium.doubleClick(strObject);

        return true;
    }

    public Boolean genfunc_MouseDownAtAndDoubleClickAt(String strObject, String strClickAt) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        //strObject = addQuotesToObjectIdentifier(strObject);
        selenium.mouseDownAt(strObject, strClickAt);
        selenium.doubleClickAt(strObject, strClickAt);

        return true;
    }

    public Boolean genfunc_SelectTableRow(String strObject, String strRowNumber) {
        String strTableObject = getTableRow(strObject, strRowNumber);
        selenium.mouseDownAt(strTableObject, "10, 10");

        return true;
    }

    public Boolean genfunc_ClickTableRow(String strObject, String strRowNumber) {
        String strTableObject = getTableRow(strObject, strRowNumber);
        selenium.mouseDownAt(strTableObject, "10, 10");
        selenium.clickAt(strTableObject, "10, 10");

        return true;
    }

    public Boolean genfunc_DoubleClickTableRow(String strObject, String strRowNumber) {
        String strTableObject = getTableRow(strObject, strRowNumber);
        selenium.mouseDownAt(strTableObject, "10, 10");
        selenium.doubleClickAt(strTableObject, "10, 10");

        return true;
    }

    public Boolean genfunc_Highlight(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        selenium.highlight(strObject);

        return true;
    }

    public Boolean genfunc_isElementPresent(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        selenium.isElementPresent(strObject);
        return true;
    }

    public Boolean genfunc_ClickOKOnAlertBox() {
        selenium.chooseOkOnNextConfirmation();
        selenium.click("confirm");
        String strConfirmation = selenium.getConfirmation();

        return true;
    }

    public Boolean genfunc_ClickCancelOnAlertBox() {
        selenium.chooseCancelOnNextConfirmation();
        selenium.click("confirm");
        String strConfirmation = selenium.getConfirmation();

        return true;
    }

    public Boolean genfunc_SendEnterKey(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        selenium.keyPress(strObject, "\13");
        return true;
    }

    public Boolean genfunc_ClearText(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        selenium.type(strObject, "");
        return true;
    }

    public Boolean genfunc_SendControlA(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        selenium.keyDown(strObject, "\17");
        selenium.keyPress(strObject, "\65");
        selenium.keyUp(strObject, "\17");
        return true;
    }

    public Boolean genfunc_SendControlC(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        selenium.keyDown(strObject, "\17");
        selenium.keyPress(strObject, "\67");
        selenium.keyUp(strObject, "\17");
        return true;
    }

    public Boolean genfunc_SendControlV(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        selenium.keyDown(strObject, "\17");
        selenium.keyPress(strObject, "\118");
        selenium.keyUp(strObject, "\17");

        return true;
    }

    public Boolean genfunc_GetAttribute(String strObject, String strValue) //Doesn't seem to work at the moment
    {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        String strElementValue = selenium.getAttribute(strObject + "@value");
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

    @Override
    public Boolean genfunc_ClickAndHold(String strObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean genfunc_GetTextNoOutput(String strObject, String strValue) {
        if (isElementPresent(strObject) == false) {
            System.out.println("not present");
            return false;
        }
        String strElementText = selenium.getText(strObject);
        strElementText = strElementText.trim();
        String strElementValue = "";
        /*        if (selenium.getAttribute(strObject + "@value") != null)
        {
            strElementValue = selenium.getAttribute(strObject + "@value");
            strElementValue = strElementValue.trim();
        }
         */
        if (strElementText.contentEquals(strValue)) {
            return true;
        } /*        else if (strElementValue.contentEquals(strValue))
        {
            return true;
        }
         */ else {
            return false;
        }
    }

    @Override
    public Boolean genfunc_ExplicitWait(String strObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean genfunc_isElementPresentChk(String strObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean genfunc_ClickOKOnAlertBoxSkip() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean genfunc_isAlertBoxPresent() {
        selenium.isAlertPresent();
        return true;
    }

    public Boolean genfunc_TakeScreenshot(String strValue) {
        selenium.captureScreenshot(strValue);
        return true;
    }

    @Override
    public Boolean genfunc_GetTextAlertBox(String strValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean genfunc_GetPlaceholder(String strObject, String strValue) //Doesn't seem to work at the moment
    {
        if (isElementPresent(strObject) == false) {
            return false;
        }

        String strElementValue = selenium.getAttribute(strObject + "@placeholder");
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

    @Override
    public Boolean genfunc_isElementEnabled(String strObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean genfunc_isElementDisabled(String strObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean genfunc_isElementSelected(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        if (selenium.isSomethingSelected(strObject)) {
            return true;
        }
        return false;
    }

    public Boolean genfunc_isElementNotSelected(String strObject) {
        if (isElementPresent(strObject) == false) {
            return false;
        }
        if (selenium.isSomethingSelected(strObject)) {
            return false;
        }
        return true;
    }

    public Boolean genfunc_waitForPageToLoad(String strObject) {
        selenium.waitForPageToLoad("60000");//wait for 60 sec.
        return true;
    }

    @Override
    public Boolean genfunc_SaveText(String strObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean genfunc_CompareText(String strObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean genfunc_CheckFormat(String strObject, String strValue) {
        return true; //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String genfunc_Txt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean genfunc_Quit() {
        //May not close driver
        selenium.close();
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
        return true;
    }

    public Boolean genfunc_ScreenSize() {
        return true;
    }

    public Boolean genfunc_SwitchToNewWindow() {
        return true;
    }

    public Boolean genfunc_ScrollDown(String strObject, String strValue) {
        selenium.getEval("scrollBy(0, 250)");
        return true;
    }

    public Boolean genfunc_waitForAngular() {

        return true;
    }

    @Override
    public Boolean genfunc_GetColour(String strObject, String strValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean genfunc_RefreshPage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
