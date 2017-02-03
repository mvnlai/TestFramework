package com.testframework.genericfunctions;

import com.testframework.fileio.*;
import com.testframework.scenarios.*;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GenericFunctions {

    public static AutomationTool automationObject;
    public static Subrogation subrogation;

    private ArrayList alElementMappingArray = new ArrayList();
    private ArrayList alDataMappingArray = new ArrayList();
    private ArrayList alDataSetArray = new ArrayList();
    private String strReturnedData = "";
    private ArrayList alXPathNameArray = new ArrayList();
    public static LogOutput logOutput = new LogOutput();

    public Boolean Set_AutomationTool(int iAutomationTool) {
        automationObject = new SeleniumRC();
        if (iAutomationTool == 1) // Selenium WebDriver
        {
            automationObject = new SeleniumWD();
        }
        if (iAutomationTool == 2) // Selenium Remote Control
        {
            automationObject = new SeleniumCore();
        }
        return true;
    }
/////////////////////////////////////////////////////////////////////////////////
// COMMAND: Find Real XPathName - more use when have Dictonaries setup.
/////////////////////////////////////////////////////////////////////////////////

    public Boolean Command_XPathNameArray_Clear() {
        alXPathNameArray.clear();
        return true;
    }

    public Boolean Command_XPathNameArray_AddRow(String PageName, String ObjectName, String XPathName) {
        XPathName_Object newXPathName = new XPathName_Object(PageName, ObjectName, XPathName);
        alXPathNameArray.add(newXPathName);

        newXPathName = null;
        System.gc();

        return true;
    }

    public String Command_Get_XPathName(String PageName, String ObjectName, String XPathName) {
        if (XPathName.isEmpty() == false) {
            return XPathName;
        }

        for (int i = 0; i < alXPathNameArray.size(); i++) {
            XPathName = ((XPathName_Object) alXPathNameArray.get(i)).returnMatchingXPathName(PageName, ObjectName);
            if (XPathName.isEmpty() == false) {
                return XPathName;
            }
        }

        return XPathName;
    }
/////////////////////////////////////////////////////////////////////////////////
// alElementMappingArray Functions.
/////////////////////////////////////////////////////////////////////////////////

    public Boolean clearElementMappingArray() {
        alElementMappingArray.clear();
        return true;
    }

    public Boolean addElementMappingArray(String strPageName, String strObjectName, String strElement) {
        ElementMapping_Object objElementMapping = new ElementMapping_Object(strPageName, strObjectName, strElement);
        alElementMappingArray.add(objElementMapping);

        objElementMapping = null;
        System.gc();

        return true;
    }

    public String getElementMapping(String PageName, String ObjectName, String Element) {
        if (Element.isEmpty() == false) {
            return Element;
        }

        for (int i = 0; i < alElementMappingArray.size(); i++) {
            Element = ((ElementMapping_Object) alElementMappingArray.get(i)).getElementName(PageName, ObjectName);
            if (Element.isEmpty() == false) {
                return Element;
            }
        }

        return Element;
    }
/////////////////////////////////////////////////////////////////////////////////
// alDataMappingArray Functions.
/////////////////////////////////////////////////////////////////////////////////

    public Boolean clearDataMappingArray() {
        alDataMappingArray.clear();
        return true;
    }

    public Boolean addDataMappingArray(String strName, String strData) {
        DataMapping_Object objDataMapping = new DataMapping_Object(strName, strData);
        alDataMappingArray.add(objDataMapping);

        objDataMapping = null;
        System.gc();

        return true;
    }

    public String getDataMapping(String Name) {
        // Expecting "$Value".
        if (Name.isEmpty() == true) {
            return "";
        }

        Name = getSubString(Name, "$", "");

        String Data = "";
        for (int i = 0; i < alDataMappingArray.size(); i++) {
            Data = ((DataMapping_Object) alDataMappingArray.get(i)).getData(Name);
            if (Data.isEmpty() == false) {
                return Data;
            }
        }

        return "";
    }
/////////////////////////////////////////////////////////////////////////////////
// alDataSetArray Functions.
/////////////////////////////////////////////////////////////////////////////////

    public Boolean clearDataSetArray() {
        alDataSetArray.clear();
        return true;
    }

    public Boolean addDataSetArray(String strName, int iRow, String[] strData) {
        DataSet_Object objDataSet = new DataSet_Object(strName, getiRowFromDataSetArray(strName), strData);
        alDataSetArray.add(objDataSet);

        objDataSet = null;
        System.gc();

        return true;
    }

    private int getiRowFromDataSetArray(String strName) {
        int iRow = 0;

        for (int i = 0; i < alDataSetArray.size(); i++) {
            if (((DataSet_Object) alDataSetArray.get(i)).getRow(strName) != -1) {
                iRow++;
            }
        }

        return iRow;
    }

    public String getDataSet(String Name) {
        // Expecting "$Value(i, i)".
        if ((Name.isEmpty() == true) || (Name.contains("(") == false) || (Name.contains(",") == false) || (Name.contains(")") == false)) {
            return "";
        }

        String strValue = getSubString(Name, "(", ",");

        int iRow = Integer.parseInt(strValue);
        strValue = getSubString(Name, ",", ")");

        int iPos = Integer.parseInt(strValue);

        if ((iRow < 0) || (iPos < 0)) {
            return "";
        }

        strValue = getSubString(Name, "$", "(");

        String Data = "";
        for (int i = 0; i < alDataSetArray.size(); i++) {
            Data = ((DataSet_Object) alDataSetArray.get(i)).getDataItem(strValue, iRow, iPos);
            if (Data.isEmpty() == false) {
                if (Data.contains("$$")) {
                    String newstr = Data;
                    newstr = newstr.replace("$$", "$");
                    String Data1 = getDataMapping(newstr);
                    return Data1;
                }
                return Data;
            }
        }
        return "";
    }
/////////////////////////////////////////////////////////////////////////////////
// strReturnedData Functions.
/////////////////////////////////////////////////////////////////////////////////

    private void setReturnedData(String strValue) {
        strReturnedData = strValue;
    }

    public String getReturnedData() {
        return strReturnedData;
    }

    private void clearReturnedData() {
        setReturnedData("");
    }

////////////////////////////////////////////////////////////////////////////////
// Useful Functions.
////////////////////////////////////////////////////////////////////////////////
    public String getSubString(String strValue, String strCharacter_Start, String strCharacter_End) {
        if (strValue.isEmpty() == true) {
            return "";
        }

        int iPos_Start = strValue.indexOf(strCharacter_Start) + 1;

        int iPos_End = strValue.indexOf(strCharacter_End);
        if (strCharacter_End.isEmpty() == true) {
            iPos_End = -1;
        }

        if (iPos_End < 0) {
            iPos_End = strValue.length();
        }

        strValue = strValue.substring(iPos_Start, iPos_End);
        strValue = strValue.trim();

        return strValue;
    }
/////////////////////////////////////////////////////////////////////////////////
// COMMAND: Call Command Function.
/////////////////////////////////////////////////////////////////////////////////

    public Boolean Command_Call_Function(String strCommand, String[] strInputValues, String strXPathName) throws Exception {
        //System.out.println("CALL COMMAND FUNC");

        clearReturnedData();

        ////////////////////////////////////////////////////////////
        // genfunc_Selenium functions.
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("genfunc_Setup")) {
            return automationObject.toolSetup(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("genfunc_Start")) {
            return automationObject.toolStart();
        }
        if (strCommand.equalsIgnoreCase("genfunc_OpenURL")) {
            return automationObject.toolOpenURL(get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_Sleep")) {
            return automationObject.toolSleep(Integer.parseInt(get_InputParametersValue(strInputValues, 0)));
        }
        if (strCommand.equalsIgnoreCase("genfunc_Highlight")) {
            return automationObject.genfunc_Highlight(get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_isElementPresent")) {
            System.out.println(get_InputParametersValue(strInputValues, 0));
            if (get_InputParametersValue(strInputValues, 0).equalsIgnoreCase("No")) {
                if (automationObject.genfunc_isElementPresent(strXPathName) == true) {
                    System.out.println("FAILED - Element is present");
                    return false;
                }
                System.out.println("PASSED - Element is NOT present");
                return true;
            }

            if (automationObject.genfunc_isElementPresent(strXPathName) == true) {
                System.out.println("PASSED - Element is present");
                return true;
            }
            System.out.println("FAILED - Element is NOT present");
            return false;
        }
        if (strCommand.equalsIgnoreCase("genfunc_DisplayWindow")) {
            return automationObject.window_DisplayWindow();
        }

        if (strCommand.equalsIgnoreCase("genfunc_SelectWindow")) {
            return automationObject.genfunc_SelectWindow(get_InputParametersValue(strInputValues, 0));
        }
        ////////////////////////////////////////////////////////////
        // genfunc_ functions.
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("genfunc_Click")) {

            return automationObject.genfunc_Click(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_SetText")) {

            return automationObject.genfunc_SetText(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_GetText")) {

            if (get_InputParametersValue(strInputValues, 0).equalsIgnoreCase("#N/A")) {
                return true;
            }

            return automationObject.genfunc_GetText(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_SetPath")) {
            return automationObject.genfunc_SetPath(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_SetComboboxValueByText")) {
            return automationObject.genfunc_SetComboboxValueByText(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_IsTextOnPage")) {
            return automationObject.genfunc_IsTextOnPage(get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_IsObjectVisible")) {
            return automationObject.genfunc_IsObjectVisible(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_ClickLink")) {
            return automationObject.genfunc_ClickLink(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_ClickTab")) {
            return automationObject.genfunc_ClickTab(
                    strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_MouseDownAndClick")) {
            return automationObject.genfunc_MouseDownAndClick(
                    strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_MouseDownAtAndClickAt")) {
            return automationObject.genfunc_MouseDownAtAndClickAt(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_MouseDownAndDoubleClick")) {
            return automationObject.genfunc_MouseDownAndDoubleClick(
                    strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_MouseDownAtAndDoubleClickAt")) {
            return automationObject.genfunc_MouseDownAtAndDoubleClickAt(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_SelectTableRow")) {
            return automationObject.genfunc_SelectTableRow(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_ClickTableRow")) {
            return automationObject.genfunc_ClickTableRow(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_DoubleClickTableRow")) {
            return automationObject.genfunc_DoubleClickTableRow(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_SetVariable")) {
            return addDataMappingArray(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("genfunc_GetVariable")) {
            setReturnedData(get_InputParametersValue(strInputValues, 0));

            return (!getReturnedData().isEmpty());
        }
        if (strCommand.equalsIgnoreCase("genfunc_GetDataMapped")) {
            setReturnedData(get_InputParametersValue(strInputValues, 0));

            return (!getReturnedData().isEmpty());
        }
        if (strCommand.equalsIgnoreCase("genfunc_GetDataSet")) {
            setReturnedData(get_InputParametersValue(strInputValues, 0));

            return (!getReturnedData().isEmpty());
        }
        if (strCommand.equalsIgnoreCase("genfunc_ClickOKOnAlertBox")) {
            return automationObject.genfunc_ClickOKOnAlertBox();
        }
        if (strCommand.equalsIgnoreCase("genfunc_ClickOKOnAlertBoxSkip")) {
            return automationObject.genfunc_ClickOKOnAlertBoxSkip();
        }
        if (strCommand.equalsIgnoreCase("genfunc_ClickCancelOnAlertBox")) {
            return automationObject.genfunc_ClickCancelOnAlertBox();
        }
        if (strCommand.equalsIgnoreCase("genfunc_isAlertBoxPresent")) {
            return automationObject.genfunc_isAlertBoxPresent();
        }
        if (strCommand.equalsIgnoreCase("genfunc_SendEnterKey")) {
            return automationObject.genfunc_SendEnterKey(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_ClearText")) {
            return automationObject.genfunc_ClearText(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_SendControlA")) {
            return automationObject.genfunc_SendControlA(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_SendControlC")) {
            return automationObject.genfunc_SendControlC(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_SendControlV")) {
            return automationObject.genfunc_SendControlV(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_GetAttribute")) {
            return automationObject.genfunc_GetAttribute(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_GetPlaceholder")) {
            return automationObject.genfunc_GetPlaceholder(strXPathName,
                    get_InputParametersValue(strInputValues, 0));

        }
        if (strCommand.equalsIgnoreCase("genfunc_isElementEnabled")) //Inputs of either Yes, No or N/A are required in the test scripts.
        {
            if (get_InputParametersValue(strInputValues, 0).equalsIgnoreCase("No")) {
                if (automationObject.genfunc_isElementEnabled(strXPathName) == true) {
                    System.out.println("FAILED - Element is enabled");
                    return false;
                }
                System.out.println("PASSED - Element is disabled");
                return true;
            } else if (get_InputParametersValue(strInputValues, 0).equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementEnabled(strXPathName) == true) {
                    System.out.println("PASSED - Element is enabled");
                    return true;
                }
                System.out.println("FAILED - Element is disabled");
                return false;
            } else if (get_InputParametersValue(strInputValues, 0).equalsIgnoreCase("#N/A")) {
                return true;
            }
            System.out.println("FAILED - No iput given");
            return false;
        }
        if (strCommand.equalsIgnoreCase("genfunc_isElementDisabled")) {
            return automationObject.genfunc_isElementDisabled(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_isElementSelected")) {
            if (get_InputParametersValue(strInputValues, 0).equalsIgnoreCase("No")) {
                if (automationObject.genfunc_isElementSelected(strXPathName) == true) {
                    System.out.println("FAILED - Element is selected");
                    return false;
                }
                System.out.println("PASSED - Element is not selected");
                return true;
            } else if (get_InputParametersValue(strInputValues, 0).equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected(strXPathName) == true) {
                    System.out.println("PASSED - Element is selected");
                    return true;
                }
                System.out.println("FAILED - Element is not selected");
                return false;
            } else if (get_InputParametersValue(strInputValues, 0).equalsIgnoreCase("#N/A")) {
                return true;
            }

            return false;

        }
        if (strCommand.equalsIgnoreCase("genfunc_isElementNotSelected")) {
            return automationObject.genfunc_isElementNotSelected(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_TakeScreenshot")) {
            return automationObject.genfunc_TakeScreenshot(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_GetTextAlertBox")) {
            return automationObject.genfunc_GetTextAlertBox(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_waitForPageToLoad")) {
            return automationObject.genfunc_waitForPageToLoad(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_SaveText")) {
            return automationObject.genfunc_SaveText(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_CompareText")) {
            return automationObject.genfunc_CompareText(strXPathName);
        }
        if (strCommand.equalsIgnoreCase("genfunc_CheckFormat")) {
            return automationObject.genfunc_CheckFormat(
                    strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_Quit")) {
            return automationObject.genfunc_Quit();
        }
        if (strCommand.equalsIgnoreCase("Notepad")) {
            return automationObject.Notepad();
        }
        if (strCommand.equalsIgnoreCase("CommandPrompt")) {
            return automationObject.CommandPrompt();
        }
        if (strCommand.equalsIgnoreCase("genfunc_Close")) {
            return automationObject.genfunc_Close();
        }
        if (strCommand.equalsIgnoreCase("genfunc_ScreenSize")) {
            return automationObject.genfunc_ScreenSize();
        }
        if (strCommand.equalsIgnoreCase("genfunc_SwitchToNewWindow")) {
            return automationObject.genfunc_SwitchToNewWindow();
        }
        if (strCommand.equalsIgnoreCase("genfunc_ScrollDown")) {
            return automationObject.genfunc_ScrollDown(strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_waitForAngular")) {
            return automationObject.genfunc_waitForAngular();
        }
        if (strCommand.equalsIgnoreCase("genfunc_clickByName")) {
            return genfunc_clickByName(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        if (strCommand.equalsIgnoreCase("genfunc_GetTextMultipleInputs")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return genfunc_GetTextMultipleInputs(strXPathName, a);
        }
        if (strCommand.equalsIgnoreCase("genfunc_ifYesGetText")) {
            ArrayList<String> a = new ArrayList();
            int i = 1;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return genfunc_ifYesGetText(strXPathName,
                    get_InputParametersValue(strInputValues, 0),
                    a);
        }
        if (strCommand.equalsIgnoreCase("genfunc_SetTextCopyAndPaste")) {
            return genfunc_SetTextCopyAndPaste(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        if (strCommand.equalsIgnoreCase("genfunc_isElementSelectedOutput")) {
            return genfunc_isElementSelectedOutput(strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_isElementPresentOutput")) {
            return genfunc_isElementPresentOutput(strXPathName,
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("genfunc_SelectTickBoxes")) {
            ArrayList<String> a = new ArrayList();
            int i = 1;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return genfunc_SelectTickBoxes(
                    get_InputParametersValue(strInputValues, 0),
                    a);
        }
        if (strCommand.equalsIgnoreCase("genfunc_SetSavedText")) {
            return genfunc_SetSavedText(strXPathName);
        }
        return false;
    }

    private void getInput(String[] strInputValues) {
        int i = 0;
        while (get_InputParametersValue(strInputValues, i) != null) {
            get_InputParametersValue(strInputValues, i);

            i = i++;
        }
    }

    private String get_InputParametersValue(String[] strInputValues, int iPos) {
        if (iPos < 0 || iPos >= strInputValues.length) {
            return "";
        }
        String strValue = strInputValues[iPos];

        if (strValue.contains("$") == true) {
// getDataSet - $Name(1,1)
            if ((strValue.contains("(") == true) && (strValue.contains(",") == true) && (strValue.contains(")") == true)) {
                strValue = getDataSet(strValue);
                if (strValue.isEmpty() != true) {
                    return strValue;
                }
            }
        }

        if (strValue.contains("$") == true) {
// getDataMapping - $Name
            strValue = getDataMapping(strValue);
            if (strValue.isEmpty() != true) {
                return strValue;
            }
        }

        return strInputValues[iPos];
    }

    /////////////////////////////////////////////////////////////////////////////////
    // General Scenarios
    /////////////////////////////////////////////////////////////////////////////////
    public Boolean genfunc_clickByName(String strXPath1, String strValue, String strXPath2) {
        if (automationObject.genfunc_isElementPresent(strXPath1) == true) {
            if (automationObject.genfunc_GetText(strXPath1, strValue) == true) {
                automationObject.genfunc_Click(strXPath2);
                return true;
            }
        }
        return true;
    }

    public Boolean genfunc_GetTextMultipleInputs(String strXPath, ArrayList<String> al) {
        int u = 0;
        String b = "";
        int size = al.size();

        for (int i = 0; i < size; i++) {
            String a = al.get(u);
            u = u + 1;
            if (a.equalsIgnoreCase("#N/A") == true) {
            } else {
                b = b + a;
            }
        }
        automationObject.genfunc_GetText(strXPath, b);
        return true;
    }

    public Boolean genfunc_ifYesGetText(String strXPath, String strValue, ArrayList<String> al) {
        if (strValue.equalsIgnoreCase("Yes")) {
            genfunc_GetTextMultipleInputs(strXPath, al);
            //automationObject.genfunc_GetText(strXPath, strValue2);
            return true;
        }
        return true;
    }

    public Boolean genfunc_SetTextCopyAndPaste(String SetTextAndCopyXPath, String strValue, String PasteToXPath) {
        automationObject.genfunc_SetText(SetTextAndCopyXPath, strValue);
        automationObject.genfunc_SendControlA(SetTextAndCopyXPath);
        automationObject.genfunc_SendControlC(SetTextAndCopyXPath);
        automationObject.genfunc_SendControlV(PasteToXPath);
        automationObject.genfunc_ClearText(SetTextAndCopyXPath);
        return true;
    }

    public Boolean genfunc_isElementSelectedOutput(String strXPath, String strValue) {
        if (strValue.equalsIgnoreCase("No")) {
            if (automationObject.genfunc_isElementSelected(strXPath) == true) {
                System.out.println("FAILED - Element is selected");
                return false;
            }
            System.out.println("PASSED - Element is not selected");
            return true;
        } else if (strValue.equalsIgnoreCase("Yes")) {
            if (automationObject.genfunc_isElementSelected(strXPath) == true) {
                System.out.println("PASSED - Element is selected");
                return true;
            }
            System.out.println("FAILED - Element is not selected");
            return false;
        } else if (strValue.equalsIgnoreCase("#N/A")) {
            return true;
        }
        System.out.println("FAILED - No iput given");
        return false;
    }

    public Boolean genfunc_isElementPresentOutput(String strXPath, String strValue) {
        if (strValue.equalsIgnoreCase("Yes")) {
            if (automationObject.genfunc_isElementPresent(strXPath) == true) {
                System.out.println("PASSED - Element is present");
                return true;
            }
            System.out.println("FAILED - Element is NOT present");
            return false;
        } else if (strValue.equalsIgnoreCase("No")) {
            if (automationObject.genfunc_isElementPresent(strXPath) == true) {
                System.out.println("FAILED - Element is present");
                return false;
            }
            System.out.println("PASSED - Element is NOT present");
            return true;
        }
        return false;
    }

    public Boolean genfunc_SelectTickBoxes(String iTickBoxes, ArrayList<String> al) {
        int N = Integer.parseInt(iTickBoxes);
        ArrayList<String> a = new ArrayList();
        ArrayList<String> b = new ArrayList();
        int x = 0;
        int y = N;

        for (int i = 0; i < N; i++) {
            a.add(al.get(x));
            b.add(al.get(y));
            x = x + 1;
            y = y + 1;
        }

        int i = 0;
        while (i < N) {   //if input is "Yes" it will ensure the current additional treatment tick box is ticked
            if (a.get(i).equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected(b.get(i)) == false) {
                    automationObject.genfunc_Click(b.get(i));
                }
            } //if input is "No" it will ensure current additional treatment tick box is unticked
            else if (a.get(i).equalsIgnoreCase("No")) {
                if (automationObject.genfunc_isElementSelected(b.get(i)) == true) {
                    automationObject.genfunc_Click(b.get(i));
                }
            }
            i++;
        }
        return true;
    }

    public Boolean genfunc_SetSavedText(String strXPath) {
        //Sets text from genfunc_SaveText into the specified xpath
        automationObject.genfunc_SetText(strXPath, automationObject.genfunc_Txt());
        return true;
    }

}
