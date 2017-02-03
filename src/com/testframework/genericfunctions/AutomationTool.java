package com.testframework.genericfunctions;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Automation Tool Class. Base Class for others.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public abstract class AutomationTool {
/////////////////////////////////////////////////////////////////////////////////
// selenium Object.
/////////////////////////////////////////////////////////////////////////////////

    public static com.thoughtworks.selenium.Selenium selenium;
    protected ArrayList alWindowArray = new ArrayList();
    protected int iWindow_Counter = 0;

    /**
     * **********************************************************************************************
     */
    /* Helper Functions.                                                                    */
    /**
     * **********************************************************************************************
     */
    protected String addQuotesToObjectIdentifier(String strObjectName) {
        strObjectName = strObjectName.replace("=", "='");
        strObjectName = strObjectName.concat("'");

        return strObjectName;
    }

    protected abstract String getTableRow(String strTable, String strRowNumber);

    protected abstract Boolean isElementPresent(String strElement);

    /**
     * **********************************************************************************************
     */
    /* Setup Functions.                                                                    */
    /**
     * **********************************************************************************************
     */
    public abstract Boolean toolSetup(String strBrowser, String strURL);

    public abstract Boolean toolStart();

    public abstract Boolean toolOpenURL(String strURL);

    public abstract Boolean toolSleep(int iSeconds);

    /**
     * **********************************************************************************************
     */
    /* Selenium Window Functions.                                                                    */
    /**
     * **********************************************************************************************
     */
    public abstract Boolean window_AddWindow();

    public abstract Boolean window_DeleteWindow();

    public abstract Boolean window_DisplayWindow();

    /**
     * **********************************************************************************************
     */
    /* Generic Functions.                                                                            */
    /**
     * **********************************************************************************************
     */
    public abstract Boolean genfunc_SelectWindow(String strWindow);

    public abstract Boolean genfunc_Click(String strObject);

    public abstract Boolean genfunc_ClickAndHold(String strObject);

    public abstract Boolean genfunc_SetText(String strObject, String strValue);

    public abstract Boolean genfunc_SetPath(String strObject, String strValue);

    public abstract Boolean genfunc_GetText(String strObject, String strValue);

    public abstract Boolean genfunc_GetTextNoOutput(String strObject, String strValue);

    public abstract Boolean genfunc_SetComboboxValueByText(String strObject, String strValue);

    public abstract Boolean genfunc_IsTextOnPage(String strValue);

    public abstract Boolean genfunc_IsObjectVisible(String strObject);

    public abstract Boolean genfunc_ClickLink(String strObject);

    public abstract Boolean genfunc_ClickTab(String strObject);

    public abstract Boolean genfunc_ExplicitWait(String strObject);

    public abstract Boolean genfunc_MouseDownAndClick(String strObject);

    public abstract Boolean genfunc_MouseDownAtAndClickAt(String strObject, String strClickAt);

    public abstract Boolean genfunc_MouseDownAndDoubleClick(String strObject);

    public abstract Boolean genfunc_MouseDownAtAndDoubleClickAt(String strObject, String strClickAt);

    public abstract Boolean genfunc_SelectTableRow(String strObject, String strRowNumber);

    public abstract Boolean genfunc_ClickTableRow(String strObject, String strRowNumber);

    public abstract Boolean genfunc_DoubleClickTableRow(String strObject, String strRowNumber);

    public abstract Boolean genfunc_Highlight(String strObject);

    public abstract Boolean genfunc_isElementPresent(String strObject);

    public abstract Boolean genfunc_isElementPresentChk(String strObject);

    public abstract Boolean genfunc_ClickOKOnAlertBox();

    public abstract Boolean genfunc_ClickCancelOnAlertBox();

    public abstract Boolean genfunc_ClickOKOnAlertBoxSkip();

    public abstract Boolean genfunc_isAlertBoxPresent();

    public abstract Boolean genfunc_SendEnterKey(String strObject);

    public abstract Boolean genfunc_ClearText(String strObject);

    public abstract Boolean genfunc_SendControlA(String strObject);

    public abstract Boolean genfunc_SendControlC(String strObject);

    public abstract Boolean genfunc_SendControlV(String strObject);

    public abstract Boolean genfunc_TakeScreenshot(String strValue);

    public abstract Boolean genfunc_GetTextAlertBox(String strValue);

    public abstract Boolean genfunc_GetAttribute(String strObject, String strValue);

    public abstract Boolean genfunc_GetPlaceholder(String strObject, String strValue);

    public abstract Boolean genfunc_isElementEnabled(String strObject);

    public abstract Boolean genfunc_isElementDisabled(String strObject);

    public abstract Boolean genfunc_isElementSelected(String strObject);

    public abstract Boolean genfunc_isElementNotSelected(String strObject);

    public abstract Boolean genfunc_waitForPageToLoad(String strObject);

    public abstract Boolean genfunc_SaveText(String strObject);

    public abstract Boolean genfunc_CompareText(String strObject);

    public abstract Boolean genfunc_CheckFormat(String strObject, String strValue);

    public abstract Boolean genfunc_Quit();

    public abstract String genfunc_Txt();

    public abstract Boolean Notepad();

    public abstract Boolean CommandPrompt();

    public abstract Boolean genfunc_Close();

    public abstract Boolean genfunc_ScreenSize();

    public abstract Boolean genfunc_SwitchToNewWindow();

    public abstract Boolean genfunc_ScrollDown(String strObject, String strValue);

    public abstract Boolean genfunc_waitForAngular();

    public abstract Boolean genfunc_GetColour(String strObject, String strValue);

    public abstract Boolean genfunc_RefreshPage();

}
