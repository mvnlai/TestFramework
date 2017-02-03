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
        if (iAutomationTool == 3) // Vaadin TestBench
        {
            automationObject = new VaadinTestBench();
        }
        if (iAutomationTool == 4) // EngineTester
        {
            automationObject = new STSEngineTester();
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
        ////////////////////////////////////////////////////////////
        // PAWS
        ////////////////////////////////////////////////////////////

        if (strCommand.equalsIgnoreCase("PAWS_PolicySearch")) {
            return PAWS_PolicySearch(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }

        if (strCommand.equalsIgnoreCase("Scenario_PAWS2384_Screenshots")) {
            return Scenario_PAWS2384_Screenshots(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6));
        }
        if (strCommand.equalsIgnoreCase("PAWS_CreateClaims")) {
            return PAWS_CreateClaims(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7),
                    get_InputParametersValue(strInputValues, 8),
                    get_InputParametersValue(strInputValues, 9),
                    get_InputParametersValue(strInputValues, 10),
                    get_InputParametersValue(strInputValues, 11),
                    get_InputParametersValue(strInputValues, 12),
                    get_InputParametersValue(strInputValues, 13),
                    get_InputParametersValue(strInputValues, 14),
                    get_InputParametersValue(strInputValues, 15),
                    get_InputParametersValue(strInputValues, 16),
                    get_InputParametersValue(strInputValues, 17),
                    get_InputParametersValue(strInputValues, 18),
                    get_InputParametersValue(strInputValues, 19),
                    get_InputParametersValue(strInputValues, 20),
                    get_InputParametersValue(strInputValues, 21),
                    get_InputParametersValue(strInputValues, 22),
                    get_InputParametersValue(strInputValues, 23),
                    get_InputParametersValue(strInputValues, 24),
                    get_InputParametersValue(strInputValues, 25),
                    get_InputParametersValue(strInputValues, 26),
                    get_InputParametersValue(strInputValues, 27),
                    get_InputParametersValue(strInputValues, 28),
                    get_InputParametersValue(strInputValues, 29),
                    get_InputParametersValue(strInputValues, 30),
                    get_InputParametersValue(strInputValues, 31),
                    get_InputParametersValue(strInputValues, 32),
                    get_InputParametersValue(strInputValues, 33)
            );
        }

        ////////////////////////////////////////////////////////////
        // VOX
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("vox_CreatePreAuthStart")) {
            return vox_CreatePreAuthStart(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_CreatePreAuth")) {
            return vox_CreatePreAuth(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7),
                    get_InputParametersValue(strInputValues, 8),
                    get_InputParametersValue(strInputValues, 9),
                    get_InputParametersValue(strInputValues, 10),
                    get_InputParametersValue(strInputValues, 11),
                    get_InputParametersValue(strInputValues, 12),
                    get_InputParametersValue(strInputValues, 13));
        }
        if (strCommand.equalsIgnoreCase("vox_UnderReviewPreAuth")) {
            return vox_UnderReviewPreAuth(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7),
                    get_InputParametersValue(strInputValues, 8),
                    get_InputParametersValue(strInputValues, 9),
                    get_InputParametersValue(strInputValues, 10),
                    get_InputParametersValue(strInputValues, 11),
                    get_InputParametersValue(strInputValues, 12),
                    get_InputParametersValue(strInputValues, 13));
        }
        if (strCommand.equalsIgnoreCase("vox_AfterResubmitting")) {
            return vox_AfterResubmitting();
        }
        if (strCommand.equalsIgnoreCase("vox_ClearTextPreAuth")) {
            return vox_ClearTextPreAuth(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_ClaimFormChooseType")) {
            return vox_ClaimFormChooseType(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_CreateClaimFormStart")) {
            return vox_CreateClaimFormStart(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("vox_CreateClaimForm")) {
            ArrayList<String> a = new ArrayList();
            int i = 16;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }

            return vox_CreateClaimForm(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7),
                    get_InputParametersValue(strInputValues, 8),
                    get_InputParametersValue(strInputValues, 9),
                    get_InputParametersValue(strInputValues, 10),
                    get_InputParametersValue(strInputValues, 11),
                    get_InputParametersValue(strInputValues, 12),
                    get_InputParametersValue(strInputValues, 13),
                    get_InputParametersValue(strInputValues, 14),
                    get_InputParametersValue(strInputValues, 15), a);
        }
        if (strCommand.equalsIgnoreCase("vox_ClaimFormSaved")) {
            ArrayList<String> a = new ArrayList();
            int i = 16;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }

            return vox_ClaimFormSaved(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7),
                    get_InputParametersValue(strInputValues, 8),
                    get_InputParametersValue(strInputValues, 9),
                    get_InputParametersValue(strInputValues, 10),
                    get_InputParametersValue(strInputValues, 11),
                    get_InputParametersValue(strInputValues, 12),
                    get_InputParametersValue(strInputValues, 13),
                    get_InputParametersValue(strInputValues, 14),
                    get_InputParametersValue(strInputValues, 15), a);
        }
        if (strCommand.equalsIgnoreCase("vox_AddCostItem")) {
            return vox_AddCostItem();
        }
        if (strCommand.equalsIgnoreCase("vox_AddCostItemSaved")) {
            return vox_AddCostItemSaved();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckCostItemBasic")) {
            return vox_CheckCostItemBasic(get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_CheckCostItemSaved")) {
            return vox_CheckCostItemSaved();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckCostItemHistorical")) {
            return vox_CheckCostItemHistorical();
        }
        if (strCommand.equalsIgnoreCase("vox_deleteCostItemByCostName")) {
            return vox_DeleteCostItemByCostName(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_deleteAllCostItems")) {
            return vox_DeleteAllCostItems();
        }
        if (strCommand.equalsIgnoreCase("vox_Submit")) {
            return vox_Submit();
        }
        if (strCommand.equalsIgnoreCase("vox_waitForPageToLoad")) {
            return vox_waitForPageToLoad();
        }

        if (strCommand.equalsIgnoreCase("vox_AddNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_AddNotes(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    a);
        }

        if (strCommand.equalsIgnoreCase("vox_CheckNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_CheckNotes(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2), a);
        }
        if (strCommand.equalsIgnoreCase("vox_AddPolicyNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_AddPolicyNotes(a);
        }
        if (strCommand.equalsIgnoreCase("vox_CheckPolicyNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_CheckPolicyNotes(a);
        }
        if (strCommand.equalsIgnoreCase("vox_AddPreAuthNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_AddPreAuthNotes(a);
        }
        if (strCommand.equalsIgnoreCase("vox_CheckPreAuthNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_CheckPreAuthNotes(a);
        }
        if (strCommand.equalsIgnoreCase("vox_AddClaimFormNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_AddClaimFormNotes(a);
        }
        if (strCommand.equalsIgnoreCase("vox_CheckClaimFormNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_CheckClaimFormNotes(a);
        }
        if (strCommand.equalsIgnoreCase("vox_CheckTableNotes")) {
            ArrayList<String> a = new ArrayList();
            int i = 0;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_CheckTableNotes(a);
        }
        if (strCommand.equalsIgnoreCase("vox_CheckPreAuthHistoricalValues")) {
            return vox_CheckPreAuthHistoricalValues();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckClaimFormCreateValues")) {
            return vox_CheckClaimFormCreateValues();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckClaimFormSavedValues")) {
            return vox_CheckClaimFormSavedValues();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckClaimFormHistoricalValues")) {
            return vox_CheckClaimFormHistoricalValues();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckPreAuthURTable_FirstRow")) {
            return vox_CheckPreAuthURTable_FirstRow();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckPreAuthHistTable_FirstRow")) {
            return vox_CheckPreAuthHistTable_FirstRow(get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_PreAuthValidationMessages")) {
            return vox_PreAuthValidationMessages(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7),
                    get_InputParametersValue(strInputValues, 8),
                    get_InputParametersValue(strInputValues, 9),
                    get_InputParametersValue(strInputValues, 10));
        }
        if (strCommand.equalsIgnoreCase("vox_ClaimValidationMessages")) {
            return vox_ClaimValidationMessages();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckPreAuthResultsMessages")) {
            return vox_CheckPreAuthResultsMessages();
        }

        if (strCommand.equalsIgnoreCase("vox_UploadDocuments")) {
            return vox_UploadDocuments();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckDocuments")) {
            return vox_CheckDocuments();
        }
        if (strCommand.equalsIgnoreCase("vox_DeleteDocuments")) {
            return vox_DeleteDocuments();
        }

        if (strCommand.equalsIgnoreCase("vox_Login")) {
            return vox_Login(get_InputParametersValue(strInputValues, 0));
        }

        if (strCommand.equalsIgnoreCase("vox_CheckNotesInPaws")) {
            ArrayList<String> a = new ArrayList();
            int i = 1;
            while (!get_InputParametersValue(strInputValues, i).equalsIgnoreCase("")) {
                a.add(get_InputParametersValue(strInputValues, i));
                i++;
            }
            return vox_CheckNotesInPaws(get_InputParametersValue(strInputValues, 0), a);
        }

        if (strCommand.equalsIgnoreCase("vox_CycleThroughComboboxes")) {
            return vox_CycleThroughComboboxes(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("vox_PolicySearchCombinations")) {
            return vox_PolicySearchCombinations();
        }
        if (strCommand.equalsIgnoreCase("vox_CheckPreAuthURTable")) {
            return vox_CheckPreAuthURTable(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_CheckPreAuthHistTable")) {
            return vox_CheckPreAuthHistTable(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_CheckClaimsSavedTable")) {
            return vox_CheckClaimsSavedTable(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_CheckClaimsHistTable")) {
            return vox_CheckClaimsHistTable(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_PreAuthUR_Reordering")) {
            return vox_Scenario_PreAuthUR_Reordering();
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_PreAuthHist_Reordering")) {
            return vox_Scenario_PreAuthHist_Reordering();
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_ClaimsSaved_Reordering")) {
            return vox_Scenario_ClaimsSaved_Reordering();
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_ClaimsHist_Reordering")) {
            return vox_Scenario_ClaimsHist_Reordering();
        }

        ////////////////////////////////////////////////////////////
        // vox_BusinessMethod_ functions - ML
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("vox_ForceLogin_User")) {
            return vox_ForceLogin_User(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("vox_ForceLogin_MultiUser")) {
            return vox_ForceLogin_MultiUser(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        if (strCommand.equalsIgnoreCase("vox_CogMenuSelect")) {
            return vox_CogMenuSelect(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_ChangeUserDetails")) {
            return vox_ChangeUserDetails(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3));
        }
        if (strCommand.equalsIgnoreCase("vox_ChangeUserPassword")) {
            return vox_ChangeUserPassword(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        if (strCommand.equalsIgnoreCase("vox_QuickSearchPolicy")) {
            return vox_QuickSearchPolicy(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_QuickSearchOrganisation")) {
            return vox_QuickSearchOrganisation(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_QuickSearchUser")) {
            return vox_QuickSearchUser(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_SearchOrganisation")) {
            return vox_SearchOrganisation(
                    get_InputParametersValue(strInputValues, 0));

        }
        if (strCommand.equalsIgnoreCase("vox_CheckResults")) {
            return vox_CheckResults(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_Search")) {
            return vox_Scenario_Search(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6));
        }
        if (strCommand.equalsIgnoreCase("vox_SearchUser")) {
            return vox_SearchUser(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        if (strCommand.equalsIgnoreCase("vox_PolicySearch")) {
            return vox_PolicySearch(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3));
        }
        if (strCommand.equalsIgnoreCase("vox_ConsentForm")) {
            return vox_ConsentForm(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("vox_SelectResult")) {
            return vox_SelectResult();
        }
        if (strCommand.equalsIgnoreCase("vox_CreateUser")) {
            return vox_CreateUser(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        if (strCommand.equalsIgnoreCase("vox_EditUser")) {
            return vox_EditUser(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("vox_EditOrganisation")) {
            return vox_EditOrganisation(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5));
        }
        if (strCommand.equalsIgnoreCase("vox_ReportListboxSelect")) {
            return vox_ReportListboxSelect();
        }
        if (strCommand.equalsIgnoreCase("vox_GenerateReport")) {
            return vox_GenerateReport(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3));
        }
        if (strCommand.equalsIgnoreCase("vox_AddFixedRateCard")) {
            return vox_AddFixedRateCard(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }
        if (strCommand.equalsIgnoreCase("vox_AddPercentRateCard")) {
            return vox_AddPercentRateCard(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3));
        }
        if (strCommand.equalsIgnoreCase("vox_CreateVetMessage")) {
            return vox_CreateVetMessage(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("vox_DeleteVetMessage")) {
            return vox_DeleteVetMessage();
        }
        if (strCommand.equalsIgnoreCase("vox_EditVetMessage")) {
            return vox_EditVetMessage(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("vox_ReorderVetMessage")) {
            return vox_ReorderVetMessage(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("vox_CheckValidation")) {
            return vox_CheckValidation(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        ////////////////////////////////////////////////////////////
        //VOX Validation Commands.
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("vox_Validate_Reporting")) {
            return vox_Validate_Reporting();
        }
        if (strCommand.equalsIgnoreCase("vox_Validate_CreateUser")) {
            return vox_Validate_CreateUser();
        }
        if (strCommand.equalsIgnoreCase("vox_Validate_AccountDetails")) {

            return vox_Validate_AccountDetails();
        }
        if (strCommand.equalsIgnoreCase("vox_Validate_PasswordDetails")) {
            return vox_Validate_PasswordDetails();
        }
        if (strCommand.equalsIgnoreCase("vox_Validate_EditUser")) {
            return vox_Validate_EditUser();
        }
        if (strCommand.equalsIgnoreCase("vox_Validate_EditOrganisation")) {
            return vox_Validate_EditOrganisation();
        }
        if (strCommand.equalsIgnoreCase("vox_Validate_RegisterUser")) {
            return vox_Validate_RegisterUser();
        }
        if (strCommand.equalsIgnoreCase("vox_Validate_RateCard")) {
            return vox_Validate_RateCard();
        }
        if (strCommand.equalsIgnoreCase("vox_Validate_PolicySearch")) {
            return vox_Validate_PolicySearch();
        }
        ////////////////////////////////////////////////////////////
        //VOX Scenario Commands.
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("vox_Scenario_Login")) {
            return vox_Scenario_Login(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_ForgottenPassword")) {
            return vox_Scenario_ForgottenPassword(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_ForgottenUsername")) {
            return vox_Scenario_ForgottenUsername(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_VetMessages")) {
            return vox_Scenario_VetMessages(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_CreateUser")) {
            return vox_Scenario_CreateUser(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_Reporting")) {
            return vox_Scenario_Reporting(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7));
        }

        if (strCommand.equalsIgnoreCase("vox_Scenario_PolicySearch")) {
            return vox_Scenario_PolicySearch(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_PolicyConsent")) {
            return vox_Scenario_PolicyConsent(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_AccountDetails")) {
            return vox_Scenario_AccountDetails(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_PasswordDetails")) {
            return vox_Scenario_PasswordDetails(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_ViewPreAuth")) {
            return vox_Scenario_ViewPreAuth(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7),
                    get_InputParametersValue(strInputValues, 8),
                    get_InputParametersValue(strInputValues, 9),
                    get_InputParametersValue(strInputValues, 10));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_ViewClaimForm")) {
            return vox_Scenario_ViewClaimForm(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6),
                    get_InputParametersValue(strInputValues, 7),
                    get_InputParametersValue(strInputValues, 8),
                    get_InputParametersValue(strInputValues, 9),
                    get_InputParametersValue(strInputValues, 10));
        }
        if (strCommand.equalsIgnoreCase("vox_Scenario_EditUser")) {
            return vox_Scenario_EditUser(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4),
                    get_InputParametersValue(strInputValues, 5),
                    get_InputParametersValue(strInputValues, 6));
        }

        ////////////////////////////////////////////////////////////
        // ipmx_BusinessMethod_ functions.
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("ipmx_BusinessMethod_Login")) {
            return ipmx_BusinessMethod_Login(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("ipmx_BusinessMethod_Logout")) {
            return ipmx_BusinessMethod_Logout();
        }
        if (strCommand.equalsIgnoreCase("ipmx_BusinessMethod_AddUser")) {
            return ipmx_BusinessMethod_AddUser(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    Integer.parseInt(get_InputParametersValue(strInputValues, 3)),
                    Integer.parseInt(get_InputParametersValue(strInputValues, 4)),
                    get_InputParametersValue(strInputValues, 5));
        }
        if (strCommand.equalsIgnoreCase("ipmx_BusinessMethod_SearchRepairerInvoice")) {
            return ipmx_BusinessMethod_SearchRepairerInvoice(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3));
        }
        if (strCommand.equalsIgnoreCase("ipmx_BusinessMethod_LoadRepairerInvoice")) {
            return ipmx_BusinessMethod_LoadRepairerInvoice(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }
        if (strCommand.equalsIgnoreCase("ipmx_BusinessMethod_ApproveRepairerInvoice")) {
            return ipmx_BusinessMethod_ApproveRepairerInvoice(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }
        if (strCommand.equalsIgnoreCase("ipmx_BusinessMethod_RejectRepairerInvoice")) {
            return ipmx_BusinessMethod_RejectRepairerInvoice(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }
        if (strCommand.equalsIgnoreCase("ipmx_BusinessMethod_RevalidateRepairerInvoice")) {
            return ipmx_BusinessMethod_RevalidateRepairerInvoice(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }
        ////////////////////////////////////////////////////////////
        // mrox_BusinessMethod_ functions.
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("mrox_BusinessMethod_Login")) {
            return mrox_BusinessMethod_Login(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("mrox_BusinessMethod_RFPSearch")) {
            return mrox_BusinessMethod_RFPSearch(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2));
        }
        ////////////////////////////////////////////////////////////
        // chox_BusinessMethod_ functions.
        ////////////////////////////////////////////////////////////
        if (strCommand.equalsIgnoreCase("chox_BusinessMethod_Login")) {
            return chox_BusinessMethod_Login(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("chox_BusinessMethod_Search")) {
            return chox_BusinessMethod_Search(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        ////////////////////////////////////////////////////////////
        // Subrogation
        ////////////////////////////////////////////////////////////

        if (strCommand.equalsIgnoreCase("sp_Login")) {
            return sp_Login(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("sp_Logout")) {
            return sp_Logout();
        }
        if (strCommand.equalsIgnoreCase("sp_SelectMenuQueue")) {
            return sp_SelectMenuQueue(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("sp_SelectClaim")) {
            return sp_SelectClaim(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_IsClaimPresent")) {
            return sp_IsClaimPresent(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("sp_IsClaimNotPresent")) {
            return sp_IsClaimNotPresent(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("sp_SubmitClaim")) {
            return sp_SubmitClaim();
        }
        if (strCommand.equalsIgnoreCase("sp_SaveClaim")) {
            return sp_SaveClaim();
        }
        if (strCommand.equalsIgnoreCase("sp_DeleteClaim")) {
            return sp_DeleteClaim();
        }
        if (strCommand.equalsIgnoreCase("sp_AcknowledgeClaim")) {
            return sp_AcknowledgeClaim();
        }
        if (strCommand.equalsIgnoreCase("sp_CloseClaim")) {
            return sp_CloseClaim(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_ResubmitClaim")) {
            return sp_ResubmitClaim(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_SendToFNOL")) {
            return sp_SendToFNOL();
        }
        if (strCommand.equalsIgnoreCase("sp_ReturnClaimFromFNOL")) {
            return sp_ReturnClaimFromFNOL();
        }
        if (strCommand.equalsIgnoreCase("sp_AssignWorkgroup")) {
            return sp_AssignWorkgroup(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_AssignOwner")) {
            return sp_AssignOwner(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_Reassign")) {
            return sp_Reassign(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("sp_AcceptRejection")) {
            return sp_AcceptRejection();
        }
        if (strCommand.equalsIgnoreCase("sp_AgreeQuantum")) {
            return sp_AgreeQuantum();
        }
        if (strCommand.equalsIgnoreCase("sp_ConfirmPayment")) {
            return sp_ConfirmPayment();
        }
        if (strCommand.equalsIgnoreCase("sp_AddInvoice")) {
            return sp_AddInvoice(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("sp_ReopenInvoice")) {
            return sp_ReopenInvoice(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_CloseInvoice")) {
            return sp_CloseInvoice(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_RejectInvoice")) {
            return sp_RejectInvoice(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1));
        }
        if (strCommand.equalsIgnoreCase("sp_DeleteInvoice")) {
            return sp_DeleteInvoice();
        }
        if (strCommand.equalsIgnoreCase("sp_SaveInvoice")) {
            return sp_SaveInvoice();
        }
        if (strCommand.equalsIgnoreCase("sp_ResubmitInvoice")) {
            return sp_ResubmitInvoice(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_SubmitInvoice")) {
            return sp_SubmitInvoice();
        }
        if (strCommand.equalsIgnoreCase("sp_ReconcileInvoice")) {
            return sp_ReconcileInvoice();
        }
        if (strCommand.equalsIgnoreCase("sp_AssignLiability")) {
            return sp_AssignLiability(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3));
        }
        if (strCommand.equalsIgnoreCase("sp_CompensatingAction")) {
            return sp_CompensatingAction(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_Action")) {
            return sp_Action(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }
        if (strCommand.equalsIgnoreCase("sp_PopulateNewDraft")) {
            return sp_PopulateNewDraft(
                    get_InputParametersValue(strInputValues, 0),
                    get_InputParametersValue(strInputValues, 1),
                    get_InputParametersValue(strInputValues, 2),
                    get_InputParametersValue(strInputValues, 3),
                    get_InputParametersValue(strInputValues, 4));
        }
        if (strCommand.equalsIgnoreCase("sp_Workflow_DataSetRunner")) {
            return sp_Workflow_DataSetRunner(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_BulkUpload_DataSetRunner")) {
            return sp_BulkUpload_DataSetRunner(
                    get_InputParametersValue(strInputValues, 0));
        }
        if (strCommand.equalsIgnoreCase("sp_Tasks_DataSetRunner")) {
            return sp_Tasks_DataSetRunner(
                    get_InputParametersValue(strInputValues, 0));
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

    /////////////////////////////////////////////////////////////////////////////////
    // PAWS Functions
    /////////////////////////////////////////////////////////////////////////////////
    public Boolean PAWS_PolicySearch(String PolicyTextbox, String PolicySearchButton, String Policy, String PolicyNumber, String PetName) {
        automationObject.genfunc_ClearText(PolicyTextbox);
        automationObject.genfunc_SetText(PolicyTextbox, PolicyNumber);
        automationObject.genfunc_Click(PolicySearchButton);
        automationObject.toolSleep(5);
        try {
            int x = 1;
            //Boolean PolicyNameCheck = automationObject.genfunc_GetText(Policy + "tr[" + x + "]/td[1]/div/div/span/span", PolicyNumber);
            //Boolean PetNameCheck = automationObject.genfunc_GetText(Policy + "tr[" + x + "]/td[3]/div", PetName);
            Boolean PolicyNameCheck = automationObject.genfunc_GetTextNoOutput(Policy + "tr[" + x + "]/td[1]/div/div/span/span", PolicyNumber);
            Boolean PetNameCheck = automationObject.genfunc_GetTextNoOutput(Policy + "tr[" + x + "]/td[3]/div", PetName);
            while (PolicyNameCheck == false || PetNameCheck == false) {
                //System.out.println(x);
                x++;
                //PolicyNameCheck = automationObject.genfunc_GetText(Policy + "tr[" + x + "]/td[1]/div/div/span/span", PolicyNumber);
                //PetNameCheck = automationObject.genfunc_GetText(Policy + "tr[" + x + "]/td[3]/div", PetName);
                PolicyNameCheck = automationObject.genfunc_GetTextNoOutput(Policy + "tr[" + x + "]/td[1]/div/div/span/span", PolicyNumber);
                PetNameCheck = automationObject.genfunc_GetTextNoOutput(Policy + "tr[" + x + "]/td[3]/div", PetName);
                if (x > 15) {
                    System.out.println("FAILED - Policy " + PolicyNumber + " with pet " + PetName + " not found.");
                    return false;
                }
            }
            if (x <= 15) {
                automationObject.genfunc_Click(Policy + "tr[" + x + "]/td[1]/div/div/span/span");
                automationObject.toolSleep(5);
                automationObject.genfunc_SwitchToNewWindow();
            }
        } catch (org.openqa.selenium.InvalidElementStateException e2) {
            System.out.println("FAILED - Policy " + PolicyNumber + " with pet " + PetName + " not found.");
            return false;
        }
        return true;
    }

    public Boolean Scenario_PAWS2384_Screenshots(String PolicyTextbox, String PolicySearchButton, String Policy, String ClaimsHistorical, String Payments,
            String BenefitSummary, String PolicyPeriodCombo) {

        //Example data sets are found in this location: Q:\QA and Testing\#Automation\AutomationScripts\VOX\PAWSforVOX
        int iPolicies = Integer.parseInt(getDataSet("$iPolicies(0,1)"));
        for (int i = 0; i < iPolicies; i++) {
            logOutput.LogStep(1015, "Scenario_PAWS2384_Screenshots", "PAWS2384_DataSet row:" + i, null);
            String PolicyNumber = getDataSet("$PAWS2384_DataSet(" + i + ",1)");
            String PetName = getDataSet("$PAWS2384_DataSet(" + i + ",2)");
            String ScreenshotLocation = getDataSet("$PAWS2384_DataSet(" + i + ",4)");
            String Run = getDataSet("$PAWS2384_DataSet(" + i + ",5)");

            PolicyNumber = PolicyNumber.trim();
            PetName = PetName.trim();

            if (Run.equalsIgnoreCase("Yes")) {
                try {

                    if (PAWS_PolicySearch(PolicyTextbox, PolicySearchButton, Policy, PolicyNumber, PetName) == true) {

                        //policy summary
                        automationObject.toolSleep(1);
                        automationObject.genfunc_TakeScreenshot(ScreenshotLocation + PolicyNumber + "_" + PetName + "_PolicySummary.png\\");
                        //claims History
                        automationObject.genfunc_Click(ClaimsHistorical);
                        automationObject.toolSleep(1);
                        automationObject.genfunc_TakeScreenshot(ScreenshotLocation + PolicyNumber + "_" + PetName + "_ClaimsHistorical" + ".png\\");
                        //Payments
                        automationObject.genfunc_Click(Payments);
                        automationObject.toolSleep(1);
                        automationObject.genfunc_TakeScreenshot(ScreenshotLocation + PolicyNumber + "_" + PetName + "_Payments" + ".png\\");
                        //Benefits
                        automationObject.genfunc_Click(BenefitSummary);
                        automationObject.toolSleep(1);
                        automationObject.genfunc_ScrollDown(PolicyPeriodCombo, "");
                        automationObject.toolSleep(1);
                        int iPolicyPeriods = Integer.parseInt(getDataSet("$PAWS2384_DataSet(" + i + ",3)"));
                        for (int n = 1; n <= iPolicyPeriods; n++) {
                            if (automationObject.genfunc_isElementPresent(PolicyPeriodCombo + "/option[" + n + "]") == false) {
                                break;
                            }
                            try {
                                automationObject.genfunc_Click(PolicyPeriodCombo + "/option[" + n + "]");
                            } catch (org.openqa.selenium.InvalidElementStateException e1) {
                                break;
                            }
                            automationObject.toolSleep(1);
                            automationObject.genfunc_TakeScreenshot(ScreenshotLocation + PolicyNumber + "_" + PetName + "_PolicyPeriod" + n + ".png\\");
                        }
                        automationObject.genfunc_Close();
                        automationObject.toolSleep(1);
                        automationObject.genfunc_SelectWindow("0");

                        //System.out.println("PASSED- Screenshots taken for policy " + PolicyNumber + " with pet " + PetName + ".");
                    } else {
                        logOutput.LogStep(1016, "Scenario_PAWS2384_Screenshots", "Screenshots NOT taken for policy " + PolicyNumber + " with pet " + PetName + ".", null);
                        //System.out.println("FAILED - Screenshots NOT taken for policy " + PolicyNumber + " with pet " + PetName + ".");
                    }
                } catch (org.openqa.selenium.InvalidElementStateException e2) {
                    logOutput.LogStep(1016, "Scenario_PAWS2384_Screenshots", "Screenshots NOT taken for policy " + PolicyNumber + " with pet " + PetName + ".", null);
                    // System.out.println("FAILED - Screenshots NOT taken for policy " + PolicyNumber + " with pet " + PetName + ".");
                }
            }
        }
        return true;
    }

    public Boolean PAWS_CreateClaims(String PolicyTextbox, String PolicySearchButton, String Policy,
            String StartClaimCapture,
            String StartClaimCaptureOkButton,
            String NumberOfIncidents,
            String NextButton,
            String Checkbox,
            String MoreParticipantsButton,
            String MoreParticipants_SearchTextbox,
            String MoreParticipants_SearchButton,
            String MoreParticipants_Checkbox,
            String MoreParticipants_SelectButton,
            String Subtype,
            String NewClaimButton,
            String BenefitNameCombobox,
            String illnessButton,
            String injuryButton,
            String IncidentNameCombobox,
            String FirstClinicalSignsTextbox,
            String TotalCostTextbox,
            String DateFromTextbox,
            String DateToTextbox,
            String PaidCostButton,
            String CostEntryDateFromTextbox,
            String CostEntryDateToTextbox,
            String CostEntryValueTextbox,
            String RunFraudCheck,
            String PayeeCombobox,
            String LetterCodeCombobox,
            String LetterPreviewButton,
            String LetterPreviewCloseButton,
            String SubmitButton,
            String ConfirmButton
    ) {
        int iClaims = Integer.parseInt(getDataSet("$PAWS_Create_iClaims(0,1)"));

        for (int i = 0; i < iClaims; i++) {
            String PolicyNumber = getDataSet("$PAWS_CreateClaim(" + i + ",1)");
            String PetName = getDataSet("$PAWS_CreateClaim(" + i + ",2)");
            String Participant = getDataSet("$PAWS_CreateClaim(" + i + ",3)");
            String VetSubtype = getDataSet("$PAWS_CreateClaim(" + i + ",4)");
            String BenefitName = getDataSet("$PAWS_CreateClaim(" + i + ",5)");
            String IncidentType = getDataSet("$PAWS_CreateClaim(" + i + ",6)");
            String IncidentName = getDataSet("$PAWS_CreateClaim(" + i + ",7)");
            String FirstClinicalSigns = getDataSet("$PAWS_CreateClaim(" + i + ",8)");
            String TotalCost = getDataSet("$PAWS_CreateClaim(" + i + ",9)");
            String DateFrom = getDataSet("$PAWS_CreateClaim(" + i + ",10)");
            String DateTo = getDataSet("$PAWS_CreateClaim(" + i + ",11)");
            String Payee = getDataSet("$PAWS_CreateClaim(" + i + ",12)");

            if (PAWS_PolicySearch(PolicyTextbox, PolicySearchButton, Policy, PolicyNumber, PetName) == true) {
                automationObject.toolSleep(1);

                automationObject.genfunc_Click(StartClaimCapture);
                automationObject.toolSleep(1);

                if (automationObject.genfunc_isElementPresent(StartClaimCaptureOkButton) == true) {
                    automationObject.genfunc_Click(StartClaimCaptureOkButton); //if there are aready inflight claims
                }

                automationObject.genfunc_waitForPageToLoad(NextButton);

                automationObject.genfunc_Click(NumberOfIncidents); //at present time is always set to 1
                automationObject.toolSleep(1);
                automationObject.genfunc_Click(NextButton);
                automationObject.toolSleep(1);

                if (automationObject.genfunc_isElementPresent(Checkbox) == true) {
                    automationObject.genfunc_Click(Checkbox);
                } else {
                    automationObject.genfunc_Click(MoreParticipantsButton);
                    automationObject.genfunc_waitForPageToLoad(MoreParticipants_SearchTextbox);
                    automationObject.genfunc_SetText(MoreParticipants_SearchTextbox, Participant);
                    automationObject.genfunc_Click(MoreParticipants_SearchButton);
                    automationObject.genfunc_waitForPageToLoad(MoreParticipants_Checkbox);
                    automationObject.genfunc_Click(MoreParticipants_Checkbox);
                    automationObject.genfunc_Click(MoreParticipants_SelectButton);
                    automationObject.toolSleep(1);
                }
                automationObject.genfunc_Click(Subtype + "/option[contains(., \"" + VetSubtype + "\")]");
                automationObject.genfunc_Click(NextButton);
                automationObject.genfunc_waitForPageToLoad(NewClaimButton);
                automationObject.genfunc_Click(NewClaimButton);
                automationObject.toolSleep(2);

                automationObject.genfunc_Click(BenefitNameCombobox + "/option[contains(., \"" + BenefitName + "\")]");
                if (BenefitName.equalsIgnoreCase("Vet fees")) {
                    if (IncidentType.equalsIgnoreCase("illness")) {
                        automationObject.genfunc_Click(illnessButton);
                    } else {
                        automationObject.genfunc_Click(injuryButton);
                    }
                    automationObject.genfunc_Click(IncidentNameCombobox + "/option[contains(., \"" + IncidentName + "\")]");
                }

                automationObject.genfunc_SetText(FirstClinicalSignsTextbox, FirstClinicalSigns);
                automationObject.genfunc_SetText(TotalCostTextbox, TotalCost);
                automationObject.genfunc_SetText(DateFromTextbox, DateFrom);
                automationObject.genfunc_SetText(DateToTextbox, DateTo);

                automationObject.genfunc_Click(NextButton);

                if (automationObject.genfunc_isElementPresent(" ")) {

                    System.out.println("FAILED - Claim NOT created for policy " + PolicyNumber + " with pet " + PetName + ".");
                } else {
                    automationObject.genfunc_waitForPageToLoad(PaidCostButton);

                    automationObject.genfunc_Click(PaidCostButton);
                    automationObject.genfunc_waitForPageToLoad(CostEntryDateFromTextbox);
                    automationObject.genfunc_SetText(CostEntryDateFromTextbox, DateFrom);
                    automationObject.genfunc_SetText(CostEntryDateToTextbox, DateTo);
                    automationObject.genfunc_ClearText(CostEntryValueTextbox);
                    automationObject.genfunc_SetText(CostEntryValueTextbox, TotalCost);

                    automationObject.genfunc_Click(NextButton);
                    automationObject.toolSleep(2);

                    automationObject.genfunc_Click(NextButton);
                    automationObject.genfunc_waitForPageToLoad(RunFraudCheck);

                    automationObject.genfunc_Click(RunFraudCheck);
                    automationObject.toolSleep(2);
                    automationObject.genfunc_Click(NextButton);
                    automationObject.toolSleep(2);

                    automationObject.genfunc_Click(NextButton);
                    automationObject.genfunc_waitForPageToLoad(PayeeCombobox);

                    automationObject.genfunc_Click(PayeeCombobox + "/option[contains(., \"" + Payee + "\")]");
                    automationObject.toolSleep(1);

                    automationObject.genfunc_Click(LetterCodeCombobox);
                    automationObject.toolSleep(1);

                    automationObject.genfunc_Click(LetterPreviewButton);
                    automationObject.genfunc_waitForPageToLoad(LetterPreviewCloseButton);

                    automationObject.genfunc_Click(LetterPreviewCloseButton);
                    automationObject.toolSleep(2);

                    automationObject.genfunc_Click(SubmitButton);
                    automationObject.genfunc_waitForPageToLoad(ConfirmButton);

                    automationObject.genfunc_Click(ConfirmButton);
                    automationObject.toolSleep(2);

                    automationObject.genfunc_Close();
                    automationObject.toolSleep(1);
                    automationObject.genfunc_SelectWindow("0");
                    System.out.println("PASSED - Claim created for policy " + PolicyNumber + " with pet " + PetName + ".");
                }
            } else {
                System.out.println("FAILED - Claim NOT created for policy " + PolicyNumber + " with pet " + PetName + ".");
            }
        }
        return true;
    }

    /////////////////////////////////////////////////////////////////////////////////
    // VOX PreAuth & Claim Form & Notes Method
    /////////////////////////////////////////////////////////////////////////////////
    public Boolean vox_CreatePreAuthStart(String strValue1) {
        automationObject.genfunc_Click("//*[@id=\"createPreauthButton\"]");
        automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
        if (strValue1.equalsIgnoreCase("New")) {
            automationObject.genfunc_Click("//*[@id=\"add_condition_link_new\"]");
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
        } else if (strValue1.equalsIgnoreCase("Ongoing")) {
            automationObject.genfunc_Click("//*[@id=\"add_condition_link_onging_0\"]");
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
        } else if (strValue1.equalsIgnoreCase("Related")) {
            automationObject.genfunc_Click("//*[@id=\"add_condition_link_related_0\"]");
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
        } else {
            return false;
        }
        return true;
    }

    public Boolean vox_CreatePreAuth(String ClaimType, String ConditionName, String RelatedConditonName, String FirstSignsDate, String FirstSignsTime,
            String Cost, String TFD, String TTD, String IllnessButton, String InjuryButton, String AT_Complementary, String AT_Behavioural, String AT_Food, String AT_Dentistry) {
        if (ClaimType.equalsIgnoreCase("New")) {
            if (!ConditionName.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_Click("//*[@id=\"createPreauthFormCondition\"]/option[contains(.,\"" + ConditionName + "\")]");
            }
            automationObject.genfunc_SetText("//*[@id=\"createPreauthFormFirstSignsDate\"]", FirstSignsDate);
            automationObject.genfunc_SetText("//*[@id=\"createPreauthFormFirstSignsTime\"]", FirstSignsTime);
        } else if (ClaimType.equalsIgnoreCase("Ongoing")) {
        } else if (ClaimType.equalsIgnoreCase("Related")) {
            if (!RelatedConditonName.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_Click("//*[@id=\"createPreauthFormRelatedCondition\"]/option[contains(.,\"" + RelatedConditonName + "\")]");
            }
        } else {
            return false;
        }
        if (ClaimType.equalsIgnoreCase("New") || ClaimType.equalsIgnoreCase("Related")) {
            if (IllnessButton.equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"createPreauthFormIllness\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"createPreauthFormIllness\"]");
                }
            }
            if (InjuryButton.equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"createPreauthFormInjury\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"createPreauthFormInjury\"]");
                }
            }
        }
        automationObject.genfunc_SetText("//*[@id=\"createPreauthFormEstimatedCost\"]", Cost);
        automationObject.genfunc_SetText("//*[@id=\"createPreauthFormTreatmentFromDate\"]", TFD);
        automationObject.genfunc_SetText("//*[@id=\"createPreauthFormTreatmentToDate\"]", TTD);
        if (AT_Complementary.equalsIgnoreCase("Yes")) {
            automationObject.genfunc_Click("//*[@id=\"createPreauthFormAdditionalTreatmentComplementaryTreatment\"]");
        }
        if (AT_Behavioural.equalsIgnoreCase("Yes")) {
            automationObject.genfunc_Click("//*[@id=\"createPreauthFormAdditionalTreatmentBehaviouralTreatment\"]");
        }
        if (AT_Food.equalsIgnoreCase("Yes")) {
            automationObject.genfunc_Click("//*[@id=\"createPreauthFormAdditionalTreatmentFood\"]");
        }
        if (AT_Dentistry.equalsIgnoreCase("Yes")) {
            automationObject.genfunc_Click("//*[@id=\"createPreauthFormAdditionalTreatmentDentistry\"]");
        }
        return true;
    }

    public Boolean vox_ClearTextPreAuth(String strValue) {
        if (strValue.equalsIgnoreCase("New")) {
            automationObject.genfunc_ClearText("//*[@id=\"createPreauthFormFirstSignsDate\"]");
            automationObject.genfunc_ClearText("//*[@id=\"createPreauthFormFirstSignsTime\"]");
        }
        automationObject.genfunc_ClearText("//*[@id=\"createPreauthFormEstimatedCost\"]");
        automationObject.genfunc_ClearText("//*[@id=\"createPreauthFormTreatmentFromDate\"]");
        automationObject.genfunc_ClearText("//*[@id=\"createPreauthFormTreatmentToDate\"]");
        return true;
    }

    public Boolean vox_UnderReviewPreAuth(String ClaimType, String ConditionName, String RelatedConditonName, String FirstSignsDate, String FirstSignsTime,
            String Cost, String TFD, String TTD, String IllnessButton, String InjuryButton, String AT_Complementary, String AT_Behavioural, String AT_Food, String AT_Dentistry) {
        if (ClaimType.equalsIgnoreCase("New")) {
            if (!ConditionName.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_Click("//*[@id=\"viewPreauthFormCondition\"]/option[contains(.,\"" + ConditionName + "\")]");
            }
            if (!FirstSignsDate.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_ClearText("//*[@id=\"viewPreauthFormFirstSignsDate\"]");
                automationObject.genfunc_SetText("//*[@id=\"viewPreauthFormFirstSignsDate\"]", FirstSignsDate);
            }
            if (!FirstSignsTime.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_ClearText("//*[@id=\"viewPreauthFormFirstSignsTime\"]");
                automationObject.genfunc_SetText("//*[@id=\"viewPreauthFormFirstSignsTime\"]", FirstSignsTime);
            }
        } else if (ClaimType.equalsIgnoreCase("Ongoing")) {
        } else if (ClaimType.equalsIgnoreCase("Related")) {
            if (!RelatedConditonName.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_Click("//*[@id=\"viewPreauthFormRelatedCondition\"]/option[contains(.,\"" + RelatedConditonName + "\")]");
            }
        } else {
            return false;
        }
        if (ClaimType.equalsIgnoreCase("New") || ClaimType.equalsIgnoreCase("Related")) {
            if (IllnessButton.equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"viewPreauthFormIllness\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"viewPreauthFormIllness\"]");
                }
            }
            if (InjuryButton.equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"viewPreauthFormInjury\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"viewPreauthFormInjury\"]");
                }
            }
        }
        //estimated cost
        if (!Cost.equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_ClearText("//*[@id=\"viewPreauthFormEstimatedCost\"]");
            automationObject.genfunc_SetText("//*[@id=\"viewPreauthFormEstimatedCost\"]", Cost);
        }
        //treatment from
        if (!TFD.equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_ClearText("//*[@id=\"viewPreauthFormTreatmentFromDate\"]");
            automationObject.genfunc_SetText("//*[@id=\"viewPreauthFormTreatmentFromDate\"]", TFD);
        }
        //treatment to
        if (!TTD.equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_ClearText("//*[@id=\"viewPreauthFormTreatmentToDate\"]");
            automationObject.genfunc_SetText("//*[@id=\"viewPreauthFormTreatmentToDate\"]", TTD);
        }
        //additional treatments
        ArrayList<String> a = new ArrayList();
        a.add(AT_Complementary);
        a.add(AT_Behavioural);
        a.add(AT_Food);
        a.add(AT_Dentistry);
        a.add("//*[@id=\"viewPreauthFormAdditionalTreatmentComplementaryTreatment\"]");
        a.add("//*[@id=\"viewPreauthFormAdditionalTreatmentBehaviouralTreatment\"]");
        a.add("//*[@id=\"viewPreauthFormAdditionalTreatmentFood\"]");
        a.add("//*[@id=\"viewPreauthFormAdditionalTreatmentDentistry\"]");
        genfunc_SelectTickBoxes("4", a);
        //cancels the edit
        if (getDataSet("$PreAuthResub(0,10)").equalsIgnoreCase("Cancel")) {
            automationObject.genfunc_Click("//*[@id=\"cancelEditButton\"]");
            automationObject.genfunc_ClickOKOnAlertBox();
            vox_waitForPageToLoad();
        } //resubmits the preauth
        else if (getDataSet("$PreAuthResub(0,10)").equalsIgnoreCase("Resubmit")) {
            automationObject.genfunc_Click("//*[@id=\"resubmitButton\"]");
            automationObject.genfunc_ClickOKOnAlertBox();
            vox_waitForPageToLoad();
            //resubmit mandatory note message
            automationObject.genfunc_SetText("//*[@id=\"actionNotesFormNoteText\"]", getDataSet("$MandatoryNotes(0,3)"));
            automationObject.genfunc_Click("//*[@id=\"saveActionNotesButton\"]");
            automationObject.toolSleep(1);
            vox_waitForPageToLoad();
        }
        return true;
    }

    public Boolean vox_AfterResubmitting() {
        automationObject.genfunc_waitForPageToLoad("//*[@id=\"preauthResultsViewTable\"]/table/thead/tr/th");
        if (automationObject.genfunc_GetTextNoOutput("//*[@id=\"preauthResultsViewTable\"]/table/thead/tr/th", getDataMapping("$PreAuthResults_Label") + " " + getDataMapping("$PreAuthResults_PASSED_Label"))) {
            automationObject.genfunc_CheckFormat("//*[@id=\"preauthPageTrail\"]", getDataMapping("$HistoricalPreAuthBreadcrumbFormat"));
        } else if (automationObject.genfunc_GetTextNoOutput("//*[@id=\"preauthResultsViewTable\"]/table/thead/tr/th", getDataMapping("$PreAuthResults_Label") + " " + getDataMapping("$PreAuthResults_FAILED_Label"))) {
            automationObject.genfunc_CheckFormat("//*[@id=\"preauthPageTrail\"]", getDataMapping("$URPreAuthBreadcrumbFormat"));
            automationObject.genfunc_Click("//*[@id=\"approveButton\"]");
            automationObject.genfunc_ClickOKOnAlertBox();
            //approve mandatory note message
            automationObject.genfunc_SetText("//*[@id=\"actionNotesFormNoteText\"]", getDataSet("$MandatoryNotes(0,1)"));
            automationObject.genfunc_Click("//*[@id=\"saveActionNotesButton\"]");
            automationObject.toolSleep(1);
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"headercreated\"]");
        }
        return true;
    }

    public Boolean vox_CreateClaimFormStart(String strValue1, String strValue2) {
        if (strValue1.equalsIgnoreCase("New") || strValue1.equalsIgnoreCase("Ongoing") || strValue1.equalsIgnoreCase("Related")) {
            automationObject.genfunc_Click("//*[@id=\"link_create_new_claimform\"]/a");
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"cancelClaimFormButton\"]");
            automationObject.toolSleep(2);
            return true;
        } else if (strValue1.equalsIgnoreCase("NewConverted") || strValue1.equalsIgnoreCase("OngoingConverted") || strValue1.equalsIgnoreCase("RelatedConverted")) {
            if (automationObject.genfunc_isElementPresent("//*[@id=\"preauth_con_row_0_condition\"]") == false) {
                automationObject.toolSleep(2);
                vox_waitForPageToLoad();
            }
            //Converts the last PreAuth in the table
            int i = 0;
            while (automationObject.genfunc_isElementPresent("//*[@id=\"preauth_con_row_" + i + "_condition\"]") == true) {
                i++;
            }
            int j = i - 1;
            automationObject.genfunc_Click("//*[@id=\"preauth_con_row_" + j + "_alink\"]");
            automationObject.genfunc_ClickOKOnAlertBox();
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"cancelClaimFormButton\"]");
            automationObject.toolSleep(2);
            return true;
        }
        return false;
    }

    public Boolean vox_ClaimFormChooseType(String strValue1) {
        vox_waitForPageToLoad();
        if (strValue1.equalsIgnoreCase("New")) //if strValue1 = "New" choose New Link
        {
            automationObject.genfunc_Click("//*[@id=\"add_condition_link_new\"]");
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
        } else if (strValue1.equalsIgnoreCase("Ongoing")) //if strValue1 = "Ongoing" choose Ongoing Link
        {
            automationObject.genfunc_Click("//*[@id=\"add_condition_link_onging_0\"]");
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
        } else if (strValue1.equalsIgnoreCase("Related")) //if strValue1 = "Related" chooses Related Link
        {
            automationObject.genfunc_Click("//*[@id=\"add_condition_link_related_0\"]");
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
        } else if (strValue1.equalsIgnoreCase("NewConverted") || strValue1.equalsIgnoreCase("OngoingConverted") || strValue1.equalsIgnoreCase("RelatedConverted")) {
        } else {
            return false;
        }
        return true;
    }

    public Boolean vox_CreateClaimForm(String ClaimType, String ConditionName, String RelatedConditonName, String FirstSignsDate, String FirstSignsTime,
            String Cost, String TFD, String TTD, String IllnessButton, String InjuryButton, String PetReferred, String ReferredVet, String ReferredDate, String Weight, String PetDeath, String PetDeathDate, ArrayList<String> al) {
        if (ClaimType.equalsIgnoreCase("New")) //if strValue1 = "New" it inputs all relevant data for New Claim Forms
        {
            if (!ConditionName.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_Click("//*[@id=\"claimFormCondition\"]/option[contains(.,\"" + ConditionName + "\")]");
            }
            automationObject.genfunc_SetText("//*[@id=\"claimFormFirstSignsDate\"]", FirstSignsDate);
            automationObject.genfunc_SetText("//*[@id=\"claimFormFirstSignsTime\"]", FirstSignsTime);
        } else if (ClaimType.equalsIgnoreCase("Ongoing")) //if strValue1 = "Ongoing" it inputs all relevant data for Ongoing Claim Forms
        {
        } else if (ClaimType.equalsIgnoreCase("Related")) //if strValue1 = "Related" it inputs all relevant data for Related Claim Forms
        {
            if (!RelatedConditonName.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_Click("//*[@id=\"claimFormRelatedCondition\"]/option[contains(.,\"" + RelatedConditonName + "\")]");
            }
        } else if (ClaimType.equalsIgnoreCase("NewConverted") || ClaimType.equalsIgnoreCase("OngoingConverted") || ClaimType.equalsIgnoreCase("RelatedConverted")) {
        } else {
            return false;
        }
        //Illness or Injury
        if (ClaimType.equalsIgnoreCase("New") || ClaimType.equalsIgnoreCase("Related")) {
            if (IllnessButton.equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormIllness\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormIllness\"]");
                }
            }
            if (InjuryButton.equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormInjury\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormInjury\"]");
                }
            }
        }
        //Actual Costs, Treatment From, Treatment To
        if (ClaimType.equalsIgnoreCase("New") || ClaimType.equalsIgnoreCase("Ongoing") || ClaimType.equalsIgnoreCase("Related")) {
            automationObject.genfunc_SetText("//*[@id=\"claimFormEstimatedCost\"]", Cost);
            automationObject.genfunc_SetText("//*[@id=\"claimFormTreatmentFromDate\"]", TFD);
            automationObject.genfunc_SetText("//*[@id=\"claimFormTreatmentToDate\"]", TTD);
        } //if strValue1 = "NewConverted", "OngoingConverted" or "RelatedConverted" it either keeps the orginal data or clears the data and inputs new data
        else if (ClaimType.equalsIgnoreCase("NewConverted") || ClaimType.equalsIgnoreCase("OngoingConverted") || ClaimType.equalsIgnoreCase("RelatedConverted")) {
            if (!Cost.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_ClearText("//*[@id=\"claimFormEstimatedCost\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormEstimatedCost\"]", Cost);
            }
            if (!TFD.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_ClearText("//*[@id=\"claimFormTreatmentFromDate\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormTreatmentFromDate\"]", TFD);
            }
            if (!TTD.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_ClearText("//*[@id=\"claimFormTreatmentToDate\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormTreatmentToDate\"]", TTD);
            }
        }
        //Additional Treatment tick boxes
        genfunc_SelectTickBoxes("6", al);
        //referring vet
        if (PetReferred.equalsIgnoreCase("Yes")) {
            automationObject.genfunc_Click("//*[@id=\"claimFormPetReferredYes\"]");
            automationObject.genfunc_Click("//*[@id=\"claimFormReferringVetName\"]/option[contains(.,\"" + ReferredVet + "\")]");
            automationObject.genfunc_SetText("//*[@id=\"claimFormReferralDate\"]", ReferredDate);
        }
        //weight
        automationObject.genfunc_SetText("//*[@id=\"claimFormPetWeight\"]", Weight);
        //Pet Death
        if (PetDeath.equalsIgnoreCase("Yes")) {
            automationObject.genfunc_Click("//*[@id=\"claimFormPetLostYes\"]");
            automationObject.genfunc_SetText("//*[@id=\"claimFormDeathDate\"]", PetDeathDate);
        }
        return true;
    }

    public Boolean vox_ClaimFormSaved(String ClaimType, String ConditionName, String RelatedConditonName, String FirstSignsDate, String FirstSignsTime,
            String Cost, String TFD, String TTD, String IllnessButton, String InjuryButton, String PetReferred, String ReferredVet, String ReferredDate, String Weight, String PetDeath, String PetDeathDate, ArrayList<String> al) {
        //if input values are not #N/A then data in textboxes and checkboxes will be replaced with the new values.

        if (ClaimType.equalsIgnoreCase("New")) //if strValue1 = "New" it inputs all relevant data for New Claim Forms
        {
            if (!ConditionName.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_Click("//*[@id=\"claimFormCondition\"]/option[contains(.,\"" + ConditionName + "\")]");
            }
            if (!FirstSignsDate.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_ClearText("//*[@id=\"claimFormFirstSignsDate\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormFirstSignsDate\"]", FirstSignsDate);
            }
            if (!FirstSignsTime.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_ClearText("//*[@id=\"claimFormFirstSignsTime\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormFirstSignsTime\"]", FirstSignsTime);
            }
        } else if (ClaimType.equalsIgnoreCase("Ongoing")) //if strValue1 = "Ongoing" it inputs all relevant data for Ongoing Claim Forms
        {
        } else if (ClaimType.equalsIgnoreCase("Related")) //if strValue1 = "Related" it inputs all relevant data for Related Claim Forms
        {
            if (!RelatedConditonName.equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_Click("//*[@id=\"claimFormRelatedCondition\"]/option[contains(.,\"" + RelatedConditonName + "\")]");
            }
        } else if (ClaimType.equalsIgnoreCase("NewConverted") || ClaimType.equalsIgnoreCase("OngoingConverted") || ClaimType.equalsIgnoreCase("RelatedConverted")) {
        } else {
            return false;
        }
        //Illness or Injury
        if (ClaimType.equalsIgnoreCase("New") || ClaimType.equalsIgnoreCase("Related")) {
            if (IllnessButton.equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormIllness\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormIllness\"]");
                }
            }
            if (InjuryButton.equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormInjury\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormInjury\"]");
                }
            }
        }
        //Actual Costs
        if (!Cost.equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_ClearText("//*[@id=\"claimFormEstimatedCost\"]");
            automationObject.genfunc_SetText("//*[@id=\"claimFormEstimatedCost\"]", Cost);
        }
        //Treatment Date From
        if (!TFD.equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_ClearText("//*[@id=\"claimFormTreatmentFromDate\"]");
            automationObject.genfunc_SetText("//*[@id=\"claimFormTreatmentFromDate\"]", TFD);
        }
        //Treatment Date To
        if (!TTD.equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_ClearText("//*[@id=\"claimFormTreatmentToDate\"]");
            automationObject.genfunc_SetText("//*[@id=\"claimFormTreatmentToDate\"]", TTD);
        }
        //Additional Treatment tick boxes
        genfunc_SelectTickBoxes("6", al);
        //referring vet
        if (PetReferred.equalsIgnoreCase("Yes")) {
            if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormPetReferredYes\"]") == false) {
                automationObject.genfunc_Click("//*[@id=\"claimFormPetReferredYes\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormReferringVetName\"]", ReferredVet);
                automationObject.genfunc_SetText("//*[@id=\"claimFormReferralDate\"]", ReferredDate);
            } else {
                if (!ReferredVet.equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormReferringVetName\"]/option[contains(.,\"" + ReferredVet + "\")]");
                }
                if (!ReferredDate.equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_ClearText("//*[@id=\"claimFormReferralDate\"]");
                    automationObject.genfunc_SetText("//*[@id=\"claimFormReferralDate\"]", ReferredDate);
                }
            }
        }
        //weight
        if (!Weight.equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_ClearText("//*[@id=\"claimFormPetWeight\"]");
            automationObject.genfunc_SetText("//*[@id=\"claimFormPetWeight\"]", Weight);
        }
        //Pet Death
        if (PetDeath.equalsIgnoreCase("Yes")) {
            if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormPetLostYes\"]") == false) {
                automationObject.genfunc_Click("//*[@id=\"claimFormPetLostYes\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormDeathDate\"]", PetDeathDate);
            } else {
                automationObject.genfunc_ClearText("//*[@id=\"claimFormDeathDate\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormDeathDate\"]", PetDeathDate);
            }
        } else {
            if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormPetLostNo\"]") == false) {
                automationObject.genfunc_Click("//*[@id=\"claimFormPetLostNo\"]");
            }
        }
        //add cost item + delete existing cost items
        if (!getDataSet("$CostItemSaved(0,1)").equalsIgnoreCase("#N/A")) {
            vox_AddCostItemSaved();
        }
        if (getDataSet("$ClaimFormSaved(0,10)").equalsIgnoreCase("Cancel")) {
            automationObject.genfunc_Click("//*[@id=\"cancelClaimFormButton\"]");
            automationObject.genfunc_ClickOKOnAlertBox();
            vox_waitForPageToLoad();
            automationObject.toolSleep(5);
        } else if (getDataSet("$ClaimFormSaved(0,10)").equalsIgnoreCase("ExitWithoutSavingThenSubmit")) {
            automationObject.genfunc_Click("//*[@id=\"claimFormPageTrail\"]/a[2]");
            vox_waitForPageToLoad();
            automationObject.toolSleep(5);
            automationObject.genfunc_Click("//*[@id=\"resultsTbody\"]/tr[1]/td[contains(.,\"VOX\")]/a");
            vox_waitForPageToLoad();
            vox_Submit();

        } else if (getDataSet("$ClaimFormSaved(0,10)").equalsIgnoreCase("Submit")) {
            vox_Submit();
        }
        return true;
    }

    public Boolean vox_AddCostItem() {
        int i = 0;
        while (i < Integer.parseInt(getDataSet("$CostItem(0,1)"))) {
            automationObject.genfunc_Click("//*[@id=\"addCostItemButton\"]");
            //cost category
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + i + "_costCategory_selector\"]", getDataSet("$CostItem(" + i + ",2)"));
            //cost name
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + i + "_costName_selector\"]", getDataSet("$CostItem(" + i + ",3)"));

            if (automationObject.genfunc_isElementPresent("//*[@id=\"cost_row_" + i + "_quantity_editor\"]") == true) {//cost qunatity
                automationObject.genfunc_SetText("//*[@id=\"cost_row_" + i + "_quantity_editor\"]", getDataSet("$CostItem(" + i + ",4)"));
            }
            //cost date from
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + i + "_dateFrom_editor\"]", getDataSet("$CostItem(" + i + ",5)"));
            //cost date To
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + i + "_dateTo_editor\"]", getDataSet("$CostItem(" + i + ",6)"));
            //Cost
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + i + "_value_editor\"]", getDataSet("$CostItem(" + i + ",7)"));
            i++;
        }
        automationObject.genfunc_Click("//*[@id=\"calculateExcessButton\"]");
        Boolean blnFirst, blnSecond;
        blnFirst = false;
        blnSecond = false;
        int x = 0;
        while (blnFirst == false && blnSecond == false) {
            automationObject.toolSleep(1);
            try {
                blnFirst = automationObject.genfunc_Click("//*[@id=\"claimFormTreatmentFromDate\"]");
            } catch (org.openqa.selenium.WebDriverException ex) {
            }
            blnSecond = automationObject.genfunc_isAlertBoxPresent();
            if (blnSecond == true) {
                System.out.println("ERROR - Due to excess failing to calculate.");
                automationObject.genfunc_GetTextAlertBox("Excess cannot be calculated automatically. Claim form will be referred to a handler after submission.");
                automationObject.genfunc_ClickOKOnAlertBox();
            }
            x++;
            if (x >= 30) {
                return false;
            }
        }
        return true;
    }

    public Boolean vox_AddCostItemSaved() {
        int iExistingCostItems = Integer.parseInt(getDataSet("$CostItem(0,1)"));
        int iNewCostItems = Integer.parseInt(getDataSet("$CostItemSaved(0,1)"));
        //deletes cost items added in vox_AddCostItem 
        for (int k = 0; k < iExistingCostItems; k++) {
            if (automationObject.genfunc_isElementPresent("//*[@id=\"cost_row_" + k + "_remove\"]/span") == true) {
                automationObject.genfunc_Click("//*[@id=\"cost_row_" + k + "_remove\"]/span");
                automationObject.genfunc_ClickOKOnAlertBox();
                automationObject.toolSleep(1);
            }
        }
        int i = 0;
        int CostItemCount = iExistingCostItems + iNewCostItems;
        while (iExistingCostItems < CostItemCount) {
            automationObject.genfunc_Click("//*[@id=\"addCostItemButton\"]");
            //cost category
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + iExistingCostItems + "_costCategory_selector\"]", getDataSet("$CostItemSaved(" + i + ",2)"));
            //cost name
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + iExistingCostItems + "_costName_selector\"]", getDataSet("$CostItemSaved(" + i + ",3)"));

            if (automationObject.genfunc_isElementPresent("//*[@id=\"cost_row_" + i + "_quantity_editor\"]") == true) {//cost qunatity
                automationObject.genfunc_SetText("//*[@id=\"cost_row_" + iExistingCostItems + "_quantity_editor\"]", getDataSet("$CostItemSaved(" + i + ",4)"));
            }
            //cost date from
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + iExistingCostItems + "_dateFrom_editor\"]", getDataSet("$CostItemSaved(" + i + ",5)"));
            //cost date To
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + iExistingCostItems + "_dateTo_editor\"]", getDataSet("$CostItemSaved(" + i + ",6)"));
            //Cost
            automationObject.genfunc_SetText("//*[@id=\"cost_row_" + iExistingCostItems + "_value_editor\"]", getDataSet("$CostItemSaved(" + i + ",7)"));
            iExistingCostItems++;
            i++;
        }
        automationObject.genfunc_Click("//*[@id=\"calculateExcessButton\"]");
        Boolean blnFirst, blnSecond;
        blnFirst = false;
        blnSecond = false;
        int x = 0;
        while (blnFirst == false && blnSecond == false) {
            automationObject.toolSleep(1);
            try {
                blnFirst = automationObject.genfunc_Click("//*[@id=\"claimFormTreatmentFromDate\"]");
            } catch (org.openqa.selenium.WebDriverException ex) {
            }
            blnSecond = automationObject.genfunc_isAlertBoxPresent();
            if (blnSecond == true) {
                System.out.println("ERROR - Due to excess failing to calculate.");
                automationObject.genfunc_GetTextAlertBox("Excess cannot be calculated automatically. Claim form will be referred to a handler after submission.");
                automationObject.genfunc_ClickOKOnAlertBox();
            }
            x++;
            if (x >= 30) {
                return false;
            }
        }
        return true;
    }

    private Boolean vox_CheckCostItemBasic(String strDataSetItem) {
        //checks cost item rows
        int i = 0;
        while (i < Integer.parseInt(getDataSet(strDataSetItem + "(0,1)"))) {
            //genfunc_isElementPresentOutput("//*[@id=\"cost_row_" + i + "_remove\"]/span");

            //cost category
            automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_costCategory\"]", getDataSet(strDataSetItem + "(" + i + ",2)"));
            //cost name 
            automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_costName\"]", getDataSet(strDataSetItem + "(" + i + ",3)"));

            if (automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_quantity\"]", "") == false) {//cost qunatity
                automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_quantity\"]", getDataSet(strDataSetItem + "(" + i + ",4)"));
            }
            //cost date from
            automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_dateFrom\"]", getDataSet(strDataSetItem + "(" + i + ",5)"));
            //cost date To
            automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_dateTo\"]", getDataSet(strDataSetItem + "(" + i + ",6)"));
            //value
            automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_value\"]", getDataSet(strDataSetItem + "(" + i + ",7)"));
            //rate card details 
            automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_rateCardDetails\"]", getDataSet(strDataSetItem + "(" + i + ",8)"));
            i++;
        }
        return true;
    }

    private String vox_ClaimExpectedCost(String ExpectedCost) {

        if (!getDataSet("$ClaimForm(0,6)").equalsIgnoreCase("#N/A")) {
            ExpectedCost = getDataSet("$ClaimForm(0,6)");
        } else if (!getDataSet("$PreAuthResub(0,10)").equalsIgnoreCase("Cancel") && !getDataSet("$PreAuthResub(0,1)").equalsIgnoreCase("")) {
            if (!getDataSet("$PreAuthResub(0,6)").equalsIgnoreCase("#N/A")) {
                ExpectedCost = getDataSet("$PreAuthResub(0,6)");
            } else {
                ExpectedCost = getDataSet("$PreAuth(0,6)");
            }
        } else {
            ExpectedCost = getDataSet("$PreAuth(0,6)");
        }
        return ExpectedCost;
    }

    public Boolean vox_CheckCostItemSaved() {
        //Checks cost item values
        vox_CheckCostItemBasic("$CostItem");

        //Finds the expected total cost value
        String ExpectedCost = "0.01";
        ExpectedCost = vox_ClaimExpectedCost(ExpectedCost);

        //Checks if expected total cost matches the total cost in the cost item
        try {
            //saves the returned excess value 
            automationObject.genfunc_SaveText("//*[@id=\"cost_row_excess_value\"]");
            //Calculates the Total amounmt 
            BigDecimal Excess = new BigDecimal(automationObject.genfunc_Txt().replaceAll(",", ""));
            BigDecimal ActualCost = new BigDecimal(ExpectedCost.replaceAll(",", ""));
            BigDecimal Total = ActualCost.subtract(Excess);
            automationObject.genfunc_GetText("//*[@id=\"cost_paid_running_total_row\"]/td[8]", String.valueOf(Total));
        } catch (java.lang.NumberFormatException e) {
            System.out.println("ERROR - Due to excess failing to calculate.");
            automationObject.genfunc_GetText("//*[@id=\"cost_row_excess_value\"]", "-");
            automationObject.genfunc_GetText("//*[@id=\"cost_paid_running_total_row\"]/td[8]", ExpectedCost);
        }
        return true;
    }

    public Boolean vox_CheckCostItemHistorical() {
        //Finds when the cost items where added - create claim page or after altering claim on save page
        String strDataSetItem;
        if (!getDataSet("$ClaimFormSaved(0,10)").equalsIgnoreCase("ExitWithoutSavingThenSubmit") && !getDataSet("$ClaimFormSaved(0,1)").equalsIgnoreCase("")) {
            if (getDataSet("$CostItemSaved(0,1)").equalsIgnoreCase("#N/A") || getDataSet("$CostItemSaved(0,1)").equalsIgnoreCase("")) {
                strDataSetItem = "$CostItem";
            } else {
                strDataSetItem = "$CostItemSaved";
            }
        } else {
            strDataSetItem = "$CostItem";
        }

        vox_CheckCostItemBasic(strDataSetItem);

        //Finds expected total cost
        String ExpectedCost = "0.01";
        if (!getDataSet("$ClaimFormSaved(0,10)").equalsIgnoreCase("ExitWithoutSavingThenSubmit") && !getDataSet("$ClaimFormSaved(0,1)").equalsIgnoreCase("")) {
            if (!getDataSet("$ClaimFormSaved(0,6)").equalsIgnoreCase("#N/A")) {
                ExpectedCost = getDataSet("$ClaimFormSaved(0,6)");
            } else {
                ExpectedCost = vox_ClaimExpectedCost(ExpectedCost);
            }
        } else {
            ExpectedCost = vox_ClaimExpectedCost(ExpectedCost);
        }

        //Checks if expected total cost matches the total cost in the cost item    
        try {
            automationObject.genfunc_SaveText("//*[@id=\"cost_row_excess_value\"]");
            BigDecimal Excess = new BigDecimal(automationObject.genfunc_Txt().replaceAll(",", ""));
            BigDecimal ActualCost = new BigDecimal(ExpectedCost.replaceAll(",", ""));
            BigDecimal Total = ActualCost.subtract(Excess);

            automationObject.genfunc_GetText("//*[@id=\"cost_paid_running_total_row\"]/td[7]", String.valueOf(Total));
        } catch (java.lang.NumberFormatException e) {
            System.out.println("ERROR - Due to excess failing to calculate.");
            automationObject.genfunc_GetText("//*[@id=\"cost_row_excess_value\"]", "-");
            automationObject.genfunc_GetText("//*[@id=\"cost_paid_running_total_row\"]/td[7]", ExpectedCost);
        }

        return true;
    }

    public Boolean vox_DeleteCostItemByCostName(String strValue) {
        int i = 0;
        while (automationObject.genfunc_GetText("//*[@id=\"cost_row_" + i + "_costName_selector\"]", strValue) == false) {
            i++;
        }
        automationObject.genfunc_Click("//*[@id=\"cost_row_" + i + "_undefined\"]/span");
        automationObject.genfunc_ClickOKOnAlertBox();
        return true;
    }

    public Boolean vox_DeleteAllCostItems() {
        int i = 0;
        while (automationObject.genfunc_isElementPresent("//*[@id=\"cost_row_" + i + "_remove\"]/span") == true) {
            automationObject.genfunc_Click("//*[@id=\"cost_row_" + i + "_remove\"]/span");
            automationObject.genfunc_ClickOKOnAlertBox();
            automationObject.toolSleep(1);
            i++;
        }
        return true;
    }

    public Boolean vox_Submit() {
        automationObject.toolSleep(1);

        Boolean blnFirst, blnSecond;
        blnFirst = false;
        blnSecond = false;
        int i = 0;

        while (blnFirst == false || blnSecond == false) {
            automationObject.genfunc_Click("//*[@id=\"submitClaimFormButton\"]");
            automationObject.genfunc_GetTextAlertBox(getDataMapping("$AlertMessageCF_Submit"));
            blnFirst = automationObject.genfunc_ClickOKOnAlertBox();
            automationObject.genfunc_GetTextAlertBox(getDataMapping("$AlertMessageCF_Submitted"));
            blnSecond = automationObject.genfunc_ClickOKOnAlertBox();
            automationObject.toolSleep(5);
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
            i++;
            if (i >= 5) {
                return false;
            }
        }
        return true;
    }

    public Boolean vox_waitForPageToLoad() {
        automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");

        return true;
    }

    public Boolean vox_AddNotes(String IconXPath, String TypeXPath, String TextboxXPath, String SaveButtonXPath, String AlertMessage, ArrayList<String> al) {
        ArrayList<String> a = new ArrayList();
        ArrayList<String> b = new ArrayList();
        int t = 0;
        int u = 1;
        int size = al.size();

        for (int i = 0; i < (size / 2); i++) {
            a.add(al.get(t));
            b.add(al.get(u));
            t = t + 2;
            u = u + 2;
        }

        int i = 0;
        while (i < size / 2) {
            //Click Notes Icon
            automationObject.genfunc_Click(IconXPath);
            //Set Note Type
            automationObject.genfunc_Click(TypeXPath + a.get(i) + "\")]");
            //Set Note content
            automationObject.genfunc_SetText(TextboxXPath, b.get(i));
            //Click the Save button
            automationObject.genfunc_Click(SaveButtonXPath);
            automationObject.genfunc_GetTextAlertBox(AlertMessage);
            automationObject.genfunc_ClickOKOnAlertBox();
            vox_waitForPageToLoad();
            i++;
        }
        return true;
    }

    public Boolean vox_CheckNotes(String IconXPath, String CheckTypeXPath, String CheckContentXPath, ArrayList<String> al) {
        ArrayList<String> a = new ArrayList();
        ArrayList<String> b = new ArrayList();
        int t = 0;
        int u = 1;
        int size = al.size();
        for (int i = 0; i < (size / 2); i++) {
            a.add(al.get(t));
            b.add(al.get(u));
            t = t + 2;
            u = u + 2;
        }
        //Click Notes Icon
        automationObject.genfunc_Click(IconXPath);
        int i = 0;
        while (i < (size / 2)) {
            //Checks Note Type
            automationObject.genfunc_GetText(CheckTypeXPath + (i + 1) + "]/div/div[1]", a.get(i));
            //Checks Note Content
            automationObject.genfunc_GetText(CheckContentXPath + (i + 1) + "]/div/div[2]", b.get(i));
            i++;
        }
        //Click Notes Icon
        automationObject.genfunc_Click(IconXPath);
        return true;
    }

    public Boolean vox_AddPolicyNotes(ArrayList<String> al) {
        vox_AddNotes("//*[@id=\"policyNotesButton\"]/span",
                "//*[@id=\"policyNotesFormNoteType\"]/option[contains(.,\"",
                "//*[@id=\"policyNotesFormNoteText\"]",
                "//*[@id=\"policyNotesForm\"]/fieldset/div/div[3]/button",
                getDataMapping("$AlertMessagePolicyNote"), al);
        return true;
    }

    public Boolean vox_CheckPolicyNotes(ArrayList<String> al) {
        vox_CheckNotes("//*[@id=\"policyNotesButton\"]/span",
                "//*[@id=\"policyNotesRetrieved\"]/fieldset[",
                "//*[@id=\"policyNotesRetrieved\"]/fieldset[", al);
        return true;
    }

    public Boolean vox_AddPreAuthNotes(ArrayList<String> al) {
        vox_AddNotes("//*[@id=\"preauthNotesCount\"]",
                "//*[@id=\"notesFormNoteType\"]/option[contains(.,\"",
                "//*[@id=\"notesFormNoteText\"]",
                "//*[@id=\"saveNotesButton\"]",
                getDataMapping("$AlertMessagePreAuthNote"), al);
        return true;
    }

    public Boolean vox_CheckPreAuthNotes(ArrayList<String> al) {
        vox_CheckNotes("//*[@id=\"preauthNotesCount\"]",
                "//*[@id=\"preauthHistoricNotesDiv\"]/fieldset[",
                "//*[@id=\"preauthHistoricNotesDiv\"]/fieldset[", al);
        return true;
    }

    public Boolean vox_AddClaimFormNotes(ArrayList<String> al) {
        vox_AddNotes("//*[@id=\"claimFormNotesCount\"]",
                "//*[@id=\"claimFormNotesFormNoteType\"]/option[contains(.,\"",
                "//*[@id=\"claimFormNotesFormNoteText\"]",
                "//*[@id=\"saveNotesButton\"]",
                getDataMapping("$AlertMessageClaimFormNote"), al);
        return true;
    }

    public Boolean vox_CheckClaimFormNotes(ArrayList<String> al) {
        vox_CheckNotes("//*[@id=\"claimFormNotesCount\"]",
                "//*[@id=\"claimFormHistoricNotesDiv\"]/fieldset[",
                "//*[@id=\"claimFormHistoricNotesDiv\"]/fieldset[", al);
        return true;
    }

    public Boolean vox_CheckTableNotes(ArrayList<String> al) {
        ArrayList<String> a = new ArrayList();
        ArrayList<String> b = new ArrayList();
        int t = 0;
        int u = 1;
        int size = al.size();
        for (int i = 0; i < (size / 2); i++) {
            a.add(al.get(t));
            b.add(al.get(u));
            t = t + 2;
            u = u + 2;
        }
        int n = 4;
        if (automationObject.genfunc_isElementPresent("//*[@id=\"headerorganisationName\"]") == true) {
            n++;
        }
        //Saves the PreAuth Reference
        automationObject.genfunc_SaveText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + n + "]");
        String Ref = automationObject.genfunc_Txt();

        //Click Notes Icon in first row
        automationObject.genfunc_Click("//*[@id=\"resultsTbody\"]/tr[1]/td[1]/a");
        //Checks note title is correct
        automationObject.genfunc_GetText("//*[@id=\"consentLegend\"]", "View Notes For " + Ref);
        int i = 0;
        int j = 1;
        while (i < (size / 2)) {
            //Checks Each note legend is present
            genfunc_isElementPresentOutput("//*[@id=\"noteFieldset" + Ref + "\"]/fieldset[" + j + "]/legend", "Yes");
            //Checks Type Label
            automationObject.genfunc_GetText("//*[@id=\"noteFieldset" + Ref + "\"]/fieldset[" + j + "]/div/label[1]", "Type");
            //Checks Note Type
            automationObject.genfunc_GetText("//*[@id=\"noteFieldset" + Ref + "\"]/fieldset[" + j + "]/div/div[1]", a.get(i));
            //Checks Note Label
            automationObject.genfunc_GetText("//*[@id=\"noteFieldset" + Ref + "\"]/fieldset[" + j + "]/div/label[2]", "Note");
            //Checks Note Content
            automationObject.genfunc_GetText("//*[@id=\"noteFieldset" + Ref + "\"]/fieldset[" + j + "]/div/div[2]", b.get(i));
            i++;
            j++;
        }
        //Click cross on note window
        automationObject.genfunc_Click("//*[@id=\"voxInfoCloseButton\"]");
        return true;
    }

    private ArrayList<String> vox_OutputClaimsTextboxArray(String Area, ArrayList TextboxArray) {
        TextboxArray.add("//*[@id=\"" + Area + "FormCondition\"]");
        TextboxArray.add("//*[@id=\"" + Area + "FormRelatedCondition\"]");
        TextboxArray.add("//*[@id=\"" + Area + "FormFirstSignsDate\"]");
        TextboxArray.add("//*[@id=\"" + Area + "FormFirstSignsTime\"]");
        TextboxArray.add("//*[@id=\"" + Area + "FormEstimatedCost\"]");
        TextboxArray.add("//*[@id=\"" + Area + "FormTreatmentFromDate\"]");
        TextboxArray.add("//*[@id=\"" + Area + "FormTreatmentToDate\"]");
        return TextboxArray;
    }

    private ArrayList<String> vox_OutputClaimsCheckboxArray(String Area, ArrayList CheckboxArray) {
        String AreaXPath = "";
        if (Area.equals("claimSaved")) {
            AreaXPath = "claim";
        } else {
            AreaXPath = Area;
        }
        CheckboxArray.add("//*[@id=\"" + AreaXPath + "FormIllness\"]");
        CheckboxArray.add("//*[@id=\"" + AreaXPath + "FormInjury\"]");
        CheckboxArray.add("//*[@id=\"" + AreaXPath + "FormAdditionalTreatmentComplementaryTreatment\"]");
        CheckboxArray.add("//*[@id=\"" + AreaXPath + "FormAdditionalTreatmentBehaviouralTreatment\"]");
        CheckboxArray.add("//*[@id=\"" + AreaXPath + "FormAdditionalTreatmentFood\"]");
        CheckboxArray.add("//*[@id=\"" + AreaXPath + "FormAdditionalTreatmentDentistry\"]");
        if (AreaXPath.equals("claim")) {
            CheckboxArray.add("//*[@id=\"" + Area + "FormAdditionalTreatmentEuthanasia\"]");
            CheckboxArray.add("//*[@id=\"" + Area + "FormAdditionalTreatmentCremation\"]");
        }
        if (Area.equals("claimSaved")) {
            CheckboxArray.add("//*[@id=\"claimFormPetReferredYes\"]");
            CheckboxArray.add("//*[@id=\"claimFormPetReferredNo\"]");
            CheckboxArray.add("//*[@id=\"claimFormPetLostYes\"]");
            CheckboxArray.add("//*[@id=\"claimFormPetLostNo\"]");
        }
        return CheckboxArray;
    }

    private Boolean vox_PreAuthTextboxCheck(ArrayList<String> XPathArray, ArrayList<String> PreAuthArray, ArrayList<String> viewPreAuthArray, Integer ArrayItemNumber) {
        if (!getDataSet("$PreAuthResub(0,10)").equalsIgnoreCase("Cancel") && !getDataSet("$PreAuthResub(0,1)").equalsIgnoreCase("")) {
            if (!viewPreAuthArray.get(ArrayItemNumber).equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetAttribute(XPathArray.get(ArrayItemNumber), viewPreAuthArray.get(ArrayItemNumber)); //Compares text if value was entered at the PreAuth (Under Review) stage
            } else if (!PreAuthArray.get(ArrayItemNumber).equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetAttribute(XPathArray.get(ArrayItemNumber), PreAuthArray.get(ArrayItemNumber));  //Compares text if value was entered at the PreAuth (Create) stage
            }
        } else if (!PreAuthArray.get(ArrayItemNumber).equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_GetAttribute(XPathArray.get(ArrayItemNumber), PreAuthArray.get(ArrayItemNumber));  //Compares text if value was entered at the PreAuth (Create) stage
        }
        return true;
    }

    private Boolean vox_ClaimTextboxCheck(ArrayList<String> XPathArray, ArrayList<String> PreAuthArray, ArrayList<String> viewPreAuthArray, ArrayList<String> ClaimArray, Integer ArrayItemNumber) {
        if (!ClaimArray.get(ArrayItemNumber).equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_GetAttribute(XPathArray.get(ArrayItemNumber), ClaimArray.get(ArrayItemNumber)); //Compares text if value was entered at the Claim Form (Create) stage
        } else {
            vox_PreAuthTextboxCheck(XPathArray, PreAuthArray, viewPreAuthArray, ArrayItemNumber);
        }
        return true;
    }

    public Boolean vox_CheckPreAuthHistoricalValues() {   //Works out the expected value for each textbox/checkbox/radiobutton and checks if it matches the actual value for claims in PreAuths (Historical) queue
        ArrayList<String> viewPreAuthTextboxValues = new ArrayList();
        ArrayList<String> PreAuthTextboxValues = new ArrayList();
        ArrayList<String> textboxXPath = new ArrayList();

        //sets values for Array lists
        for (int i = 2; i < 9; i++) {
            viewPreAuthTextboxValues.add(getDataSet("$PreAuthResub(0," + i + ")"));
            PreAuthTextboxValues.add(getDataSet("$PreAuth(0," + i + ")"));
        }
        vox_OutputClaimsTextboxArray("viewPreauths", textboxXPath);

        //Checks all textbox values are correct in the Condition Details Section
        for (int i = 0; i < 7; i++) {
            vox_PreAuthTextboxCheck(textboxXPath, PreAuthTextboxValues, viewPreAuthTextboxValues, i);
        }

        ArrayList<String> CheckboxValues = new ArrayList();
        ArrayList<String> checkboxXPath = new ArrayList();

        //sets values for Array lists
        if (!getDataSet("$PreAuthResub(0,10)").equalsIgnoreCase("Cancel") && !getDataSet("PreAuthResub(0,1)").equalsIgnoreCase("")) {
            for (int i = 1; i < 7; i++) {
                CheckboxValues.add(getDataSet("PreAuthIsElementSelectedResub(0," + i + ")"));
            }
        } else {
            for (int i = 1; i < 7; i++) {
                CheckboxValues.add(getDataSet("PreAuthIsElementSelected(0," + i + ")"));
            }
        }
        vox_OutputClaimsCheckboxArray("viewPreauths", checkboxXPath);

        //Checks all checkbox and radio button values are correct in the Condition Details Section
        for (int i = 0; i < 6; i++) {
            genfunc_isElementSelectedOutput(checkboxXPath.get(i), CheckboxValues.get(i));
        }
        return true;
    }

    public Boolean vox_CheckClaimFormCreateValues() {   //Works out the expected value for each textbox/checkbox/radiobutton and checks if it matches the actual value at the start of creating a claim or just after its been converted
        //Creates Arrays
        ArrayList<String> claimTextboxValues = new ArrayList();
        ArrayList<String> viewPreAuthTextboxValues = new ArrayList();
        ArrayList<String> PreAuthTextboxValues = new ArrayList();
        ArrayList<String> textboxXPath = new ArrayList();
        for (int i = 2; i < 9; i++) {
            claimTextboxValues.add(getDataSet("$ClaimForm(0," + i + ")"));
            viewPreAuthTextboxValues.add(getDataSet("$PreAuthResub(0," + i + ")"));
            PreAuthTextboxValues.add(getDataSet("$PreAuth(0," + i + ")"));
        }
        vox_OutputClaimsTextboxArray("claim", textboxXPath);

        //Checks relevant textboxes are empty when a claim is first created
        if (getDataSet("$ClaimForm(0,1)").equalsIgnoreCase("New")) {
            for (int i = 0; i < 7; i++) {
                automationObject.genfunc_GetAttribute(textboxXPath.get(i), "");
            }
        }
        if (getDataSet("$ClaimForm(0,1)").equalsIgnoreCase("Ongoing")) {
            for (int i = 0; i < 4; i++) {
                automationObject.genfunc_GetAttribute(textboxXPath.get(i), claimTextboxValues.get(i));
            }
            for (int i = 4; i < 7; i++) {
                automationObject.genfunc_GetAttribute(textboxXPath.get(i), "");
            }
        }
        if (getDataSet("$ClaimForm(0,1)").equalsIgnoreCase("Related")) {
            automationObject.genfunc_GetAttribute(textboxXPath.get(0), claimTextboxValues.get(0));
            automationObject.genfunc_GetAttribute(textboxXPath.get(1), "");
            for (int i = 2; i < 4; i++) {
                automationObject.genfunc_GetAttribute(textboxXPath.get(i), claimTextboxValues.get(i));
            }
            for (int i = 4; i < 7; i++) {
                automationObject.genfunc_GetAttribute(textboxXPath.get(i), "");
            }
        }

        //Checks textbox values are correct when preauth is converted to a claim
        if (getDataSet("$ClaimForm(0,1)").matches("NewConverted|NewOngoing|NewRelated")) {
            //All textboxes in the Condition Details Section
            for (int i = 0; i < 7; i++) {
                vox_PreAuthTextboxCheck(textboxXPath, PreAuthTextboxValues, viewPreAuthTextboxValues, i);
            }
        }

        //Creates Arrays
        ArrayList<String> checkboxValues = new ArrayList();
        ArrayList<String> checkboxXPath = new ArrayList();
        if (!getDataSet("$PreAuthResub(0,10)").equalsIgnoreCase("Cancel")) {
            for (int i = 1; i < 9; i++) {
                checkboxValues.add(getDataSet("PreAuthIsElementSelectedResub(0," + i + ")"));
            }
        } else {
            for (int i = 1; i < 9; i++) {
                checkboxValues.add(getDataSet("PreAuthIsElementSelected(0," + i + ")"));
            }
        }
        vox_OutputClaimsCheckboxArray("claim", checkboxXPath);

        //Checks all checkbox and radio button values are correct when a claim is first created
        if (getDataSet("ClaimForm(0,1)").matches("New|Related")) {
            for (int i = 0; i < 8; i++) {
                genfunc_isElementSelectedOutput(checkboxXPath.get(i), "No");
            }
        }
        if (getDataSet("ClaimForm(0,1)").equalsIgnoreCase("Ongoing")) {
            genfunc_isElementSelectedOutput(checkboxXPath.get(0), getDataSet("$ClaimIsElementSelected(0,1)"));
            genfunc_isElementSelectedOutput(checkboxXPath.get(1), getDataSet("$ClaimIsElementSelected(0,2)"));
            for (int i = 2; i < 8; i++) {
                genfunc_isElementSelectedOutput(checkboxXPath.get(i), "No");
            }
        }
        //Checks all checkbox and radio button values are correct when a preauth is converted to a claim
        if (getDataSet("ClaimForm(0,1)").matches("NewConverted|NewOngoing|NewRelated")) {
            for (int i = 0; i < 8; i++) {
                genfunc_isElementSelectedOutput(checkboxXPath.get(i), checkboxValues.get(i));
            }
        }

        //checks values in referral vet section
        genfunc_isElementSelectedOutput("//*[@id=\"claimFormPetReferredYes\"]", "No");
        genfunc_isElementSelectedOutput("//*[@id=\"claimFormPetReferredNo\"]", "Yes");
        automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferringVetName\"]", "");
        automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferralDate\"]", "");
        //Checks Weight textbox is blank
        automationObject.genfunc_GetAttribute("//*[@id=\"claimFormPetWeight\"]", "");
        //checks values in Pet Dead section
        genfunc_isElementSelectedOutput("//*[@id=\"claimFormPetLostYes\"]", "No");
        genfunc_isElementSelectedOutput("//*[@id=\"claimFormPetLostNo\"]", "Yes");
        automationObject.genfunc_GetAttribute("//*[@id=\"claimFormDeathDate\"]", "");
        return true;
    }

    public Boolean vox_CheckClaimFormSavedValues() {   //Works out the expected value for each textbox/checkbox/radiobutton and checks if it matches the actual value for claims in Claims (Saved) queue
        ArrayList<String> claimTextboxValues = new ArrayList();
        ArrayList<String> viewPreAuthTextboxValues = new ArrayList();
        ArrayList<String> PreAuthTextboxValues = new ArrayList();
        ArrayList<String> textboxXPath = new ArrayList();
        for (int i = 2; i < 9; i++) {
            claimTextboxValues.add(getDataSet("$ClaimForm(0," + i + ")"));
            viewPreAuthTextboxValues.add(getDataSet("$PreAuthResub(0," + i + ")"));
            PreAuthTextboxValues.add(getDataSet("$PreAuth(0," + i + ")"));
        }
        vox_OutputClaimsTextboxArray("claim", textboxXPath);

        //All textboxes in the Condition Details Section
        for (int i = 0; i < 7; i++) {
            vox_ClaimTextboxCheck(textboxXPath, PreAuthTextboxValues, viewPreAuthTextboxValues, claimTextboxValues, i);
        }

        //Creates Arrays
        ArrayList<String> checkboxValues = new ArrayList();
        ArrayList<String> checkboxXPath = new ArrayList();
        for (int i = 1; i < 9; i++) {
            checkboxValues.add(getDataSet("$ClaimIsElementSelected(0," + i + ")"));
        }
        checkboxValues.add(getDataSet("$ReferringVet(0,1)"));
        checkboxValues.add(getDataSet("$ReferringVet(0,2)"));
        checkboxValues.add(getDataSet("$PetDeath(0,1)"));
        checkboxValues.add(getDataSet("$PetDeath(0,2)"));

        vox_OutputClaimsCheckboxArray("claimSaved", checkboxXPath);

        //radio buttons/tickboxes
        for (int i = 0; i < 12; i++) {
            genfunc_isElementSelectedOutput(checkboxXPath.get(i), checkboxValues.get(i)); //Compares text if value was entered at the Claim Form (Create) stage
        }

        //referral vet
        if (getDataSet("$ReferringVet(0,1)").equalsIgnoreCase("Yes")) {
            automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferringVetName\"]", getDataSet("$ReferringVet(0,3)"));
            automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferralDate\"]", getDataSet("$ReferringVet(0,4)"));
        } else {
            automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferringVetName\"]", "");
            automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferralDate\"]", "");
        }
        //Weight
        if (!getDataSet("$Weight(0,1)").equalsIgnoreCase("#N/A")) {
            automationObject.genfunc_GetAttribute("//*[@id=\"claimFormPetWeight\"]", getDataSet("$Weight(0,1)"));
        } else {
            automationObject.genfunc_GetAttribute("//*[@id=\"claimFormPetWeight\"]", "");
        }
        //Pet Death
        if (getDataSet("$PetDeath(0,1)").equalsIgnoreCase("Yes")) {
            automationObject.genfunc_GetAttribute("//*[@id=\"claimFormDeathDate\"]", getDataSet("$PetDeath(0,3)"));
        } else {
            automationObject.genfunc_GetAttribute("//*[@id=\"claimFormDeathDate\"]", "");
        }
        return true;
    }

    public Boolean vox_CheckClaimFormHistoricalValues() {   //Works out the expected value for each textbox/checkbox/radiobutton and checks if it matches the actual value for claims in Claims (Historical) queue

        //Creates Arrays
        ArrayList<String> claimSavedTextboxValues = new ArrayList();
        ArrayList<String> claimTextboxValues = new ArrayList();
        ArrayList<String> viewPreAuthTextboxValues = new ArrayList();
        ArrayList<String> PreAuthTextboxValues = new ArrayList();
        ArrayList<String> textboxXPath = new ArrayList();
        for (int i = 2; i < 9; i++) {
            claimSavedTextboxValues.add(getDataSet("$ClaimFormSaved(0," + i + ")"));
            claimTextboxValues.add(getDataSet("$ClaimForm(0," + i + ")"));
            viewPreAuthTextboxValues.add(getDataSet("$PreAuthResub(0," + i + ")"));
            PreAuthTextboxValues.add(getDataSet("$PreAuth(0," + i + ")"));
        }
        vox_OutputClaimsTextboxArray("claim", textboxXPath);

        //All textboxes in the Condition Details Section
        for (int i = 0; i < 7; i++) {
            vox_ClaimTextboxCheck(textboxXPath, PreAuthTextboxValues, viewPreAuthTextboxValues, claimTextboxValues, i);
        }

        //All textboxes in the Condition Details Section
        for (int i = 0; i < 7; i++) {
            if (!getDataSet("$ClaimFormSaved(0,10)").equalsIgnoreCase("ExitWithoutSavingThenSubmit") && !getDataSet("$ClaimFormSaved(0,1)").equalsIgnoreCase("")) {
                if (!claimSavedTextboxValues.get(i).equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_GetAttribute(textboxXPath.get(i), claimSavedTextboxValues.get(i)); //Compares text if value was entered at the Claim Form (Saved) stage
                } else {
                    vox_ClaimTextboxCheck(textboxXPath, PreAuthTextboxValues, viewPreAuthTextboxValues, claimTextboxValues, i);
                }
            } else {
                vox_ClaimTextboxCheck(textboxXPath, PreAuthTextboxValues, viewPreAuthTextboxValues, claimTextboxValues, i);
            }
        }

        //Create Arrays
        ArrayList<String> checkboxClaimSavedValues = new ArrayList();
        ArrayList<String> checkboxClaimsValues = new ArrayList();
        ArrayList<String> checkboxXPath = new ArrayList();
        for (int i = 1; i < 9; i++) {
            checkboxClaimSavedValues.add(getDataSet("$ClaimIsElementSelectedSaved(0," + i + ")"));
            checkboxClaimsValues.add(getDataSet("$ClaimIsElementSelected(0," + i + ")"));
        }
        for (int i = 1; i < 3; i++) {
            checkboxClaimSavedValues.add(getDataSet("$ReferringVetSaved(0," + i + ")"));
            checkboxClaimsValues.add(getDataSet("$ReferringVet(0," + i + ")"));
        }
        for (int i = 1; i < 3; i++) {
            checkboxClaimSavedValues.add(getDataSet("$PetDeathSaved(0," + i + ")"));
            checkboxClaimsValues.add(getDataSet("$PetDeatht(0," + i + ")"));
        }
        vox_OutputClaimsCheckboxArray("claimSaved", checkboxXPath);

        //radio buttons/tickboxes
        for (int i = 0; i < 12; i++) {
            if (!getDataSet("$ClaimFormSaved(0,10)").equalsIgnoreCase("ExitWithoutSavingThenSubmit") && !getDataSet("$ClaimFormSaved(0,1)").equalsIgnoreCase("")) {
                genfunc_isElementSelectedOutput(checkboxXPath.get(i), checkboxClaimSavedValues.get(i));//Compares text if value was entered at the Claim Form (Saved) stage
            } else {
                genfunc_isElementSelectedOutput(checkboxXPath.get(i), checkboxClaimsValues.get(i)); //Compares text if value was entered at the Claim Form (Create) stage
            }
        }

        //Can't see a way to reduce code
        if (!getDataSet("$ClaimFormSaved(0,10)").equalsIgnoreCase("ExitWithoutSavingThenSubmit") && !getDataSet("$ClaimFormSaved(0,1)").equalsIgnoreCase("")) {
            //Compares text if value was entered at the Claim Form (Saved) stage
            if (getDataSet("$ReferringVetSaved(0,1)").equalsIgnoreCase("Yes")) {
                //referral vet
                if (!getDataSet("$ReferringVetSaved(0,3)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferringVetName\"]", getDataSet("$ReferringVetSaved(0,3)"));
                } else {
                    automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferringVetName\"]", getDataSet("$ReferringVet(0,3)"));
                }
                if (!getDataSet("$ReferringVetSaved(0,4)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferralDate\"]", getDataSet("$ReferringVetSaved(0,4)"));
                } else {
                    automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferralDate\"]", getDataSet("$ReferringVet(0,4)"));
                }
            } else {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferringVetName\"]", "");
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferralDate\"]", "");
            }
            //Weight
            if (!getDataSet("$WeightSaved(0,1)").equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormPetWeight\"]", getDataSet("$WeightSaved(0,1)"));
            } else if (!getDataSet("$Weight(0,1)").equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormPetWeight\"]", getDataSet("$Weight(0,1)"));
            } else {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormPetWeight\"]", "");
            }
            //Pet Death
            if (getDataSet("$PetDeathSaved(0,1)").equalsIgnoreCase("Yes")) {
                if (!getDataSet("$PetDeathSaved(0,3)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_GetAttribute("//*[@id=\"claimFormDeathDate\"]", getDataSet("$PetDeathSaved(0,3)"));
                } else {
                    automationObject.genfunc_GetAttribute("//*[@id=\"claimFormDeathDate\"]", getDataSet("$PetDeath(0,3)"));
                }
            } else {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormDeathDate\"]", "");
            }
        } else {
            //Compares text if value was entered at the Claim Form (Create) stage
            //referral vet
            if (getDataSet("$ReferringVet(0,1)").equalsIgnoreCase("Yes")) {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferringVetName\"]", getDataSet("$ReferringVet(0,3)"));
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferralDate\"]", getDataSet("$ReferringVet(0,4)"));
            } else {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferringVetName\"]", "");
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormReferralDate\"]", "");
            }
            //Weight
            if (!getDataSet("$Weight(0,1)").equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormPetWeight\"]", getDataSet("$Weight(0,1)"));
            } else {
                automationObject.genfunc_GetAttribute("//*[@id=\"claimFormPetWeight\"]", "");
            }
            //Pet Death
            if (getDataSet("$PetDeath(0,1)").equalsIgnoreCase("Yes")) {
                automationObject.genfunc_GetText("//*[@id=\"claimFormDeathDate\"]", getDataSet("$PetDeath(0,3)"));
            } else {
                automationObject.genfunc_GetText("//*[@id=\"claimFormDeathDate\"]", "");
            }
        }
        return true;
    }

    //CheckURTableFirstRow
    public Boolean vox_CheckPreAuthURTable_FirstRow() {
        int i = 4;
        if (automationObject.genfunc_isElementPresent("//*[@id=\"headerorganisationName\"]") == true) {
            //organisation
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$VetUser(0,6)"));
            i++;
        }
        //PreAuth Ref
        automationObject.genfunc_CheckFormat("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", "PA[0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9][0-9][0-9]");
        i++;
        //Policy
        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PolicySearch(2,3)"));
        i++;
        //Policyholder
        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PolicySearch(0,5)") + " " + getDataSet("$PolicySearch(0,2)"));
        i++;
        //Pet
        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PolicySearch(2,1)"));
        i++;
        if (getDataSet("$PreAuth(0,1)").equalsIgnoreCase("Related")) {
            //related condition Name
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,3)"));
            i++;
        } else {
            //condition name
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,2)"));
            i++;
        }
        //estimated costs
        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", "£" + getDataSet("$PreAuth(0,6)"));
        i++;
        //first signs
        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,4)"));
        i++;
        //treatment from
        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,7)"));
        i++;
        //treatment To
        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,8)"));
        i++;
//        if (strValue15.equalsIgnoreCase("Yes"))
//        {
//            //Owner
//            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", strValue13 + " " + strValue14);
//        }
        return true;
    }

    //CheckURTableFirstRow
    public Boolean vox_CheckPreAuthHistTable_FirstRow(String strValue) {
        if (getDataSet("$PreAuthResub(0,10)").equalsIgnoreCase("Resubmit")) {
            int i = 4;
            if (automationObject.genfunc_isElementPresent("//*[@id=\"headerorganisationName\"]") == true) {
                //organisation
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$VetUser(0,6)"));
                i++;
            }
            //PreAuth Ref
            automationObject.genfunc_CheckFormat("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", "PA[0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9][0-9][0-9]");
            i++;
            //Policy
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PolicySearch(2,3)"));
            i++;
            //Policyholder
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PolicySearch(0,5)") + " " + getDataSet("$PolicySearch(0,2)"));
            i++;
            //Pet
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PolicySearch(2,1)"));
            i++;
            if (getDataSet("$PreAuthResub(0,1)").equalsIgnoreCase("Related")) {
                //related condition Name
                if (!getDataSet("$PreAuthResub(0,3)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuthResub(0,3)"));
                    i++;
                } else {
                    automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,3)"));
                    i++;
                }
            } else {
                //condition name
                if (!getDataSet("$PreAuthResub(0,2)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuthResub(0,2)"));
                    i++;
                } else {
                    automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,2)"));
                    i++;
                }
            }
            //estimated costs
            if (!getDataSet("$PreAuthResub(0,6)").equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", "£" + getDataSet("$PreAuthResub(0,6)"));
                i++;
            } else {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", "£" + getDataSet("$PreAuth(0,6)"));
                i++;
            }
            //first signs
            if (!getDataSet("$PreAuthResub(0,4)").equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuthResub(0,4)"));
                i++;
            } else {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,4)"));
                i++;
            }
            //treatment from
            if (!getDataSet("$PreAuthResub(0,7)").equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuthResub(0,7)"));
                i++;
            } else {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,7)"));
                i++;
            }
            //treatment To
            if (!getDataSet("$PreAuthResub(0,8)").equalsIgnoreCase("#N/A")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuthResub(0,8)"));
                i++;
            } else {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$PreAuth(0,8)"));
                i++;
            }
            //Status
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", strValue);
            i++;
            //Approver
            if (!automationObject.genfunc_GetTextNoOutput("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", "")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$RSAUser(0,3)") + " " + getDataSet("$RSAUser(0,4)"));
            }
        } else {
            vox_CheckPreAuthURTable_FirstRow();
            int i = 13;
            if (automationObject.genfunc_isElementPresent("//*[@id=\"headerorganisationName\"]") == true) {
                i++;
            }
            //Status
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", strValue);
            i++;
            //Approver
            if (!automationObject.genfunc_GetTextNoOutput("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", "")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[1]/td[" + i + "]", getDataSet("$RSAUser(0,3)") + " " + getDataSet("$RSAUser(0,4)"));
            }
        }
        return true;
    }

    public Boolean vox_PreAuthValidationMessages(String XPathConditionName, String XPathRelatedConditionName, String XPathIllness, String XPathInjury, String XPathFirstSignsDate,
            String XPathFirstSignsTime, String XPathEstimatedCost, String XPathTreatDateFrom, String XPathTreatDateTo, String XPathSubmit, String XPathErrorMessage) {
        int i = 0;
        while (i < Integer.parseInt(getDataSet("$PreAuthValidationDetails(0,1)"))) //loops through each row of PreAuthValidationMessages Data Set
        {
            //Clears Data
            automationObject.genfunc_ClearText(XPathEstimatedCost);
            automationObject.genfunc_ClearText(XPathTreatDateFrom);
            automationObject.genfunc_ClearText(XPathTreatDateTo);
            //Clears and inputs data if PreAuth is "New"
            if (getDataSet("$PreAuthValidationDetails(0,2)").equalsIgnoreCase("New")) {
                automationObject.genfunc_ClearText(XPathFirstSignsDate);
                automationObject.genfunc_ClearText(XPathFirstSignsTime);
                if (!getDataSet("$PreAuthValidationMessages(" + i + ",1)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_Click(XPathConditionName + "/option[contains(.,\"" + getDataSet("$PreAuthValidationMessages(" + i + ",1)") + "\")]");
                }
                automationObject.genfunc_SetText(XPathFirstSignsDate, getDataSet("$PreAuthValidationMessages(" + i + ",4)"));
                automationObject.genfunc_SetText(XPathFirstSignsTime, getDataSet("$PreAuthValidationMessages(" + i + ",5)"));
            }
            //Chooses illness/injury if PreAuth is "New" or "Related"
            if (getDataSet("$PreAuthValidationDetails(0,2)").equalsIgnoreCase("New") || getDataSet("$PreAuthValidationDetails(0,2)").equalsIgnoreCase("Related")) {
                if (getDataSet("$PreAuthValidationMessages(" + i + ",3)").equalsIgnoreCase("Illness")) {
                    if (automationObject.genfunc_isElementSelected(XPathIllness) == false) {
                        automationObject.genfunc_Click(XPathIllness);
                    }
                }
                if (getDataSet("$PreAuthValidationMessages(" + i + ",3)").equalsIgnoreCase("Injury")) {
                    if (automationObject.genfunc_isElementSelected(XPathInjury) == false) {
                        automationObject.genfunc_Click(XPathInjury);
                    }
                }
            }
            //Inputs Related Conditon Name if PreAuth is "Related", unless given input is "Empty"
            if (getDataSet("$PreAuthValidationDetails(0,2)").equalsIgnoreCase("Related")) {
                if (!getDataSet("$PreAuthValidationMessages(" + i + ",2)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_Click(XPathRelatedConditionName + "/option[contains(.,\"" + getDataSet("$PreAuthValidationMessages(" + i + ",2)") + "\")]");
                }
            }
            //Inputs Estimated Cost, Treatment Date From, and Treatment Date To data
            if (!getDataSet("$PreAuthValidationMessages(" + i + ",6)").contains("-")) {
                automationObject.genfunc_SetText(XPathEstimatedCost, getDataSet("$PreAuthValidationMessages(" + i + ",6)"));
            } else {
                genfunc_SetTextCopyAndPaste("//*[@id=\"quickSearchKeyword\"]", getDataSet("$PreAuthValidationMessages(" + i + ",6)"), XPathEstimatedCost);
            }
            automationObject.genfunc_SetText(XPathTreatDateFrom, getDataSet("$PreAuthValidationMessages(" + i + ",7)"));
            automationObject.genfunc_SetText(XPathTreatDateTo, getDataSet("$PreAuthValidationMessages(" + i + ",8)"));

            //Clicks the Submit button
            automationObject.genfunc_Click(XPathSubmit);
            System.out.println("         Row " + i);
            //Checks up to 6 validation Messages at a time
            int V = 9; //Column for start of validation messages
            while (V < 16) {
                if (automationObject.genfunc_isAlertBoxPresent()) {
                    System.out.println("FAILED - No Validation Message found for PreAuthValidationMessages data set row " + i);
                    automationObject.genfunc_ClickCancelOnAlertBox();
                    break;
                }
                if (!getDataSet("$PreAuthValidationMessages(" + i + "," + V + ")").equalsIgnoreCase("")) {
                    //tries to find the a validation message with the exact same messsage
                    try {
                        automationObject.genfunc_GetText(XPathErrorMessage + "/ul/li[contains(.,\"" + getDataSet("$PreAuthValidationMessages(" + i + "," + V + ")") + "\")]", getDataSet("$PreAuthValidationMessages(" + i + "," + V + ")"));
                    } catch (org.openqa.selenium.InvalidElementStateException e) {
                        //tries to compare its text to the first validation message
                        try {
                            automationObject.genfunc_GetText(XPathErrorMessage + "/ul/li[1]", getDataSet("$PreAuthValidationMessages(" + i + "," + V + ")"));
                        } catch (org.openqa.selenium.InvalidElementStateException f) {
                            System.out.println("FAILED - No Validation Message found for PreAuthValidationMessages data set row " + i);
                        }
                    }
                }
                V++;
            }
            i++;
        }
        return true;
    }

    public Boolean vox_ClaimValidationMessages() {
        int i = 0; //Current Row Number
        int n = 0; //Cost item count
        while (automationObject.genfunc_isElementPresent("//*[@id=\"cost_row_" + n + "_remove\"]/span") == true) {
            n++;
        }
        int TotalRows = Integer.parseInt(getDataSet("$ClaimValidationDetails(0,1)"));

        while (i < TotalRows) //loops through each row of the ClaimValidationMessages Data Set
        {
            //Clears Data
            automationObject.genfunc_ClearText("//*[@id=\"claimFormEstimatedCost\"]");
            automationObject.genfunc_ClearText("//*[@id=\"claimFormTreatmentFromDate\"]");
            automationObject.genfunc_ClearText("//*[@id=\"claimFormTreatmentToDate\"]");
            //Clears and inputs data if Claim Form is "New"
            if (getDataSet("$ClaimValidationDetails(0,2)").equalsIgnoreCase("New")) {
                automationObject.genfunc_ClearText("//*[@id=\"claimFormFirstSignsDate\"]");
                automationObject.genfunc_ClearText("//*[@id=\"claimFormFirstSignsTime\"]");
                //Inputs Condition Name unless given input "#N/A"
                if (!getDataSet("$ClaimValidationMessages(" + i + ",1)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormCondition\"]" + "/option[contains(.,\"" + getDataSet("$ClaimValidationMessages(" + i + ",1)") + "\")]");
                }
                automationObject.genfunc_SetText("//*[@id=\"claimFormFirstSignsDate\"]", getDataSet("$ClaimValidationMessages(" + i + ",4)"));
                automationObject.genfunc_SetText("//*[@id=\"claimFormFirstSignsTime\"]", getDataSet("$ClaimValidationMessages(" + i + ",5)"));
            }
            //Chooses illness/injury if claim form is "New" or "Related"
            if (getDataSet("$ClaimValidationDetails(0,2)").equalsIgnoreCase("New") || getDataSet("$ClaimValidationDetails(0,2)").equalsIgnoreCase("Related")) {
                if (getDataSet("$ClaimValidationMessages(" + i + ",3)").equalsIgnoreCase("Illness")) {
                    //If illness is already selected ingnore else click illness
                    if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormIllness\"]") == false) {
                        automationObject.genfunc_Click("//*[@id=\"claimFormIllness\"]");
                    }
                }
                if (getDataSet("$ClaimValidationMessages(" + i + ",3)").equalsIgnoreCase("Injury")) {
                    //If injury is already selected ingnore else click injury
                    if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormInjury\"]") == false) {
                        automationObject.genfunc_Click("//*[@id=\"claimFormInjury\"]");
                    }
                }
            }
            //Inputs Related Conditon Name if Claim is "Related", unless given input is "#N/A"
            if (getDataSet("$ClaimValidationDetails(0,2)").equalsIgnoreCase("Related")) {
                if (!getDataSet("$ClaimValidationMessages(" + i + ",2)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormRelatedCondition\"]" + "/option[contains(.,\"" + getDataSet("$ClaimValidationMessages(" + i + ",2)") + "\")]");
                }
            }
            //Inputs Actual Cost, Treatment Date From, and Treatment Date To data
            if (!getDataSet("$ClaimValidationMessages(" + i + ",6)").contains("-")) {
                automationObject.genfunc_SetText("//*[@id=\"claimFormEstimatedCost\"]", getDataSet("$ClaimValidationMessages(" + i + ",6)"));
            } else {
                //To handle negative numbers
                genfunc_SetTextCopyAndPaste("//*[@id=\"quickSearchKeyword\"]", getDataSet("$ClaimValidationMessages(" + i + ",6)"), "//*[@id=\"claimFormEstimatedCost\"]");
            }
            automationObject.genfunc_SetText("//*[@id=\"claimFormTreatmentFromDate\"]", getDataSet("$ClaimValidationMessages(" + i + ",7)"));
            automationObject.genfunc_SetText("//*[@id=\"claimFormTreatmentToDate\"]", getDataSet("$ClaimValidationMessages(" + i + ",8)"));

            ///////////
            //Referral Vet
            //////
            //if the input is "Yes" it clicks the yes button unless it is already selected. Then inputs relevant data
            if (getDataSet("$ClaimValidationMessages(" + i + ",9)").equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormPetReferredYes\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormPetReferredYes\"]");
                } else {
                    //clears all previous data
                    automationObject.genfunc_Click("//*[@id=\"claimFormPetReferredNo\"]");
                    automationObject.genfunc_Click("//*[@id=\"claimFormPetReferredYes\"]");
                }
                automationObject.genfunc_ClearText("//*[@id=\"claimFormReferralDate\"]");
                if (!getDataSet("$ClaimValidationMessages(" + i + ",10)").equalsIgnoreCase("#N/A")) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormReferringVetName\"]" + "/option[contains(.,\"" + getDataSet("$ClaimValidationMessages(" + i + ",10)") + "\")]");
                }
                automationObject.genfunc_SetText("//*[@id=\"claimFormReferralDate\"]", getDataSet("$ClaimValidationMessages(" + i + ",11)"));
            }
            //if the input is "No" it clicks the no button unless it is already selected
            if (getDataSet("$ClaimValidationMessages(" + i + ",9)").equalsIgnoreCase("No")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormPetReferredNo\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormPetReferredNo\"]");
                }
            }
            //////////////////
            //Weight
            //////
            //Clears and sets data
            automationObject.genfunc_ClearText("//*[@id=\"claimFormPetWeight\"]");
            if (!getDataSet("$ClaimValidationMessages(" + i + ",12)").contains("-")) {
                automationObject.genfunc_SetText("//*[@id=\"claimFormPetWeight\"]", getDataSet("$ClaimValidationMessages(" + i + ",12)"));
            } else {
                //To handle negative numbers
                genfunc_SetTextCopyAndPaste("//*[@id=\"quickSearchKeyword\"]", getDataSet("$ClaimValidationMessages(" + i + ",12)"), "//*[@id=\"claimFormPetWeight\"]");
            }

            ////////////
            //Date of Death
            /////
            //if the input is "Yes" it clicks the yes button unless it is already selected. Then inputs relevant data
            if (getDataSet("$ClaimValidationMessages(" + i + ",13)").equalsIgnoreCase("Yes")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormPetLostYes\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormPetLostYes\"]");
                }
                automationObject.genfunc_ClearText("//*[@id=\"claimFormDeathDate\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimFormDeathDate\"]", getDataSet("$ClaimValidationMessages(" + i + ",14)"));
            }
            //if the input is "No" it clicks the no button unless it is already selected
            if (getDataSet("$ClaimValidationMessages(" + i + ",13)").equalsIgnoreCase("No")) {
                if (automationObject.genfunc_isElementSelected("//*[@id=\"claimFormPetLostNo\"]") == false) {
                    automationObject.genfunc_Click("//*[@id=\"claimFormPetLostNo\"]");
                }
            }
            //////////
            //Cost Item
            ////

            //deletes all existing cost items
            int k = 0;
            while (k < n) {
                //remove cost items when creating claim forms
                if (automationObject.genfunc_isElementPresent("//*[@id=\"cost_row_" + k + "_undefined\"]/span") == true) {
                    automationObject.genfunc_Click("//*[@id=\"cost_row_" + k + "_undefined\"]/span");
                    automationObject.genfunc_ClickOKOnAlertBox();
                    automationObject.toolSleep(1);
                }
                //remove cost items for saved claim forms
                if (automationObject.genfunc_isElementPresent("//*[@id=\"cost_row_" + k + "_remove\"]/span") == true) {
                    automationObject.genfunc_Click("//*[@id=\"cost_row_" + k + "_remove\"]/span");
                    automationObject.genfunc_ClickOKOnAlertBox();
                    automationObject.toolSleep(1);
                }
                k++;
            }
            int m = Integer.parseInt(getDataSet("$ClaimValidationMessages(" + i + ",15)")); //Number of Cost Items to add
            int l = m + n;
            int j = 16;
            //Adds "m" number of Cost Items
            while (n < l) {
                automationObject.genfunc_Click("//*[@id=\"addCostItemButton\"]");
                //cost category
                if (getDataSet("$ClaimValidationMessages(" + i + "," + j + ")").equalsIgnoreCase("#N/A")) {
                    n++;
                    break;
                }
                automationObject.genfunc_SetText("//*[@id=\"cost_row_" + n + "_costCategory_selector\"]", getDataSet("$ClaimValidationMessages(" + i + "," + j + ")"));
                j++;
                //cost name
                if (getDataSet("$ClaimValidationMessages(" + i + "," + j + ")").equalsIgnoreCase("#N/A")) {
                    n++;
                    break;
                }
                automationObject.genfunc_SetText("//*[@id=\"cost_row_" + n + "_costName_selector\"]", getDataSet("$ClaimValidationMessages(" + i + "," + j + ")"));
                j++;
                if (automationObject.genfunc_isElementPresent("//*[@id=\"cost_row_" + n + "_quantity_editor\"]") == true) {//cost qunatity
                    if (getDataSet("$ClaimValidationMessages(" + i + "," + j + ")").matches("[0-9]+") || getDataSet("$ClaimValidationMessages(" + i + "," + j + ")").equalsIgnoreCase("#N/A")) {
                        automationObject.genfunc_SetText("//*[@id=\"cost_row_" + n + "_quantity_editor\"]", getDataSet("$ClaimValidationMessages(" + i + "," + j + ")"));
                    } else {
                        //To handle negative numbers
                        genfunc_SetTextCopyAndPaste("//*[@id=\"quickSearchKeyword\"]", getDataSet("$ClaimValidationMessages(" + i + "," + j + ")"), "//*[@id=\"cost_row_" + n + "_quantity_editor\"]");
                    }
                }
                j++;
                //cost date from
                automationObject.genfunc_SetText("//*[@id=\"cost_row_" + n + "_dateFrom_editor\"]", getDataSet("$ClaimValidationMessages(" + i + "," + j + ")"));
                j++;
                //cost date To
                automationObject.genfunc_SetText("//*[@id=\"cost_row_" + n + "_dateTo_editor\"]", getDataSet("$ClaimValidationMessages(" + i + "," + j + ")"));
                j++;
                //Cost
                if (!getDataSet("$ClaimValidationMessages(" + i + "," + j + ")").contains("-")) {
                    automationObject.genfunc_SetText("//*[@id=\"cost_row_" + n + "_value_editor\"]", getDataSet("$ClaimValidationMessages(" + i + "," + j + ")"));
                } else {
                    //To handle negative numbers
                    genfunc_SetTextCopyAndPaste("//*[@id=\"quickSearchKeyword\"]", getDataSet("$ClaimValidationMessages(" + i + "," + j + ")"), "//*[@id=\"cost_row_" + n + "_value_editor\"]");
                }
                j++;
                n++;
                automationObject.genfunc_Click("//*[@id=\"claimFormPetWeight\"]");
            }

            ////////////////////////
            //Calculate Excess/Save/Submit
            //////////
            //Choose if you want to test the validaiton messages by clicking Calculate Excess, Save, or Submit
            if (getDataSet("$ClaimValidationMessages(" + i + ",28)").equalsIgnoreCase("Calculate Excess")) {
                automationObject.genfunc_Click("//*[@id=\"calculateExcessButton\"]");
            }
            if (getDataSet("$ClaimValidationMessages(" + i + ",28)").equalsIgnoreCase("Save")) {
                automationObject.genfunc_Click("//*[@id=\"saveClaimFormButton\"]");
            }
            if (getDataSet("$ClaimValidationMessages(" + i + ",28)").equalsIgnoreCase("Submit")) {
                automationObject.genfunc_Click("//*[@id=\"submitClaimFormButton\"]");
            }

            /////////
            //Validation Messages
            /////////
            System.out.println("         Row " + i);
            //Checks up to 6 validation Messages at a time
            int V = 29; //Column for start of validation messages
            while (V < 36) {
                if (automationObject.genfunc_isAlertBoxPresent()) {
                    System.out.println("FAILED - No Validation Message found for ClaimValidationMessages data set row " + i);
                    automationObject.genfunc_ClickCancelOnAlertBox();
                    break;
                }
                if (!getDataSet("$ClaimValidationMessages(" + i + "," + V + ")").equalsIgnoreCase("")) {
                    //tries to find the a validation message with the exact same messsage
                    try {
                        automationObject.genfunc_GetText("//*[@id=\"claimFormErrorBlock\"]/ul/li[contains(.,\"" + getDataSet("$ClaimValidationMessages(" + i + "," + V + ")") + "\")]", getDataSet("$ClaimValidationMessages(" + i + "," + V + ")"));
                    } catch (org.openqa.selenium.InvalidElementStateException e) {
                        //tries to compare its text to the first validation message
                        try {
                            automationObject.genfunc_GetText("//*[@id=\"claimFormErrorBlock\"]/ul/li[1]", getDataSet("$ClaimValidationMessages(" + i + "," + V + ")"));
                        } catch (org.openqa.selenium.InvalidElementStateException f) {
                            System.out.println("FAILED - No Validation Message found for ClaimValidationMessages data set row " + i);
                        }
                    }
                }
                V++;
            }
            i++;
        }
        return true;
    }

    public Boolean vox_CheckPreAuthResultsMessages() {
        //Inputs come from PreAuthValidationRules data set
        //Column 1 = User, i.e. RSA/Vet/Vetfone
        //Column 2 = Expected Pre-Authorisation Result: PASSED,FAILED or #N/A
        //Column 3 = PreAuthorisation Reference
        //Column 4+ = Pre-Authorisation Results Messages
        int TotalRows = Integer.parseInt(getDataSet("$PreAuthValidationResultsDetails(0,1)"));
        for (int i = 0; i < TotalRows; i++) {
            if (i > 0) {
                int j = i - 1;
                if (!getDataSet("$PreAuthResultsMessages(" + i + ",1)").equalsIgnoreCase(getDataSet("$PreAuthResultsMessages(" + j + ",1)"))) //logout if user is different to previous user
                {
                    //log out
                    automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
                    automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Logout\")]/a");
                    automationObject.toolSleep(1);
                }
            } else {
                //On first run through, if the cog icon is present logout
                if (automationObject.genfunc_isElementPresent("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span") == true) {
                    //log out
                    automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
                    automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Logout\")]/a");
                    automationObject.toolSleep(1);
                }
                //Logout
                if (getDataSet("$PreAuthResultsMessages(" + i + ",1)").equalsIgnoreCase("RSA")) {
                    vox_ForceLogin_User(getDataSet("$RSAUser(0,1)"), getDataSet("$RSAUser(0,2)"));
                }
                if (getDataSet("$PreAuthResultsMessages(" + i + ",1)").equalsIgnoreCase("Vet")) {
                    vox_ForceLogin_User(getDataSet("$VetUser(0,1)"), getDataSet("$VetUser(0,2)"));
                }
                if (getDataSet("$PreAuthResultsMessages(" + i + ",1)").equalsIgnoreCase("Vetfone")) {
                    vox_ForceLogin_User(getDataSet("$VetfoneUser(0,1)"), getDataSet("$VetfoneUser(0,2)"));
                }
            }
            if (getDataSet("$PreAuthResultsMessages(" + i + ",2)").equalsIgnoreCase("PASSED")) {
                //naviagate to PreAuths (Historical) page
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/a");
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/ul/li[contains(.,\"PreAuths (Historical)\")]/a");
                automationObject.genfunc_waitForPageToLoad("//*[@id=\"resultsTbody\"]/tr[1]/td[contains(.,\"PA\")]/a");
                //Search by Pre-Authorisation Reference
                automationObject.genfunc_Click("//*[@id=\"preFilter\"]/option[contains(.,\"PreAuth Ref\")]");
                automationObject.toolSleep(1);
                automationObject.genfunc_SetText("//*[@id=\"preauthHistoricalFormKeyword\"]", getDataSet("$PreAuthResultsMessages(" + i + ",3)"));
            } else if (getDataSet("$PreAuthResultsMessages(" + i + ",2)").equalsIgnoreCase("FAILED")) {
                //naviagate to PreAuths (Under Review) page
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/a");
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/ul/li[contains(.,\"PreAuths (Under Review)\")]/a");
                automationObject.genfunc_waitForPageToLoad("//*[@id=\"resultsTbody\"]/tr[1]/td[contains(.,\"PA\")]/a");
                //Search by Pre-Authorisation Reference
                automationObject.genfunc_Click("//*[@id=\"preFilter\"]/option[contains(.,\"PreAuth Ref\")]");
                automationObject.toolSleep(1);
                automationObject.genfunc_SetText("//*[@id=\"preauthReviewFormKeyword\"]", getDataSet("$PreAuthResultsMessages(" + i + ",3)"));
            } else if (getDataSet("$PreAuthResultsMessages(" + i + ",2)").equalsIgnoreCase("#N/A")) {
                break;
            } else {
                System.out.println("$PreAuthResultsMessages(" + i + ",2) is not one of the following (PASSED,FAILED,#N/A)");
                break;
            }

            automationObject.genfunc_Click("//*[@id=\"preauthReviewSearchButton\"]");
            automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
            if (automationObject.genfunc_GetText("//*[@id=\"searchResultsTitle\"]", "1 Record Found") == true) {
                automationObject.genfunc_Click("//*[@id=\"resultsTbody\"]/tr[1]/td[contains(.,\"PA\")]/a");
                automationObject.genfunc_waitForPageToLoad("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
            } else {
                System.out.println("PreAuthorisation Reference not found for $PreAuthResultsMessages(" + i + ",3)");
                break;
            }
            System.out.println("         Row " + i);
            //Checks up to 8 validation Messages at a time
            for (int V = 4; V < 12; V++) {
                if (!getDataSet("$PreAuthResultsMessages(" + i + "," + V + ")").equalsIgnoreCase("")) {
                    //tries to find the a validation message with the exact same messsage
                    try {
                        automationObject.genfunc_GetText("//*[@id=\"preauthResultsViewTable\"]/table/tbody/tr[contains(.,\"" + getDataSet("$PreAuthResultsMessages(" + i + "," + V + ")") + "\")]/td", getDataSet("$PreAuthResultsMessages(" + i + "," + V + ")"));
                    } catch (org.openqa.selenium.InvalidElementStateException e) {
                        //tries to compare its text to the first validation message
                        try {
                            automationObject.genfunc_GetText("//*[@id=\"preauthResultsViewTable\"]/table/tbody/tr[1]/td", getDataSet("$PreAuthResultsMessages(" + i + "," + V + ")"));
                        } catch (org.openqa.selenium.InvalidElementStateException f) {
                            System.out.println("FAILED - " + getDataSet("$PreAuthResultsMessages(" + i + "," + V + ")") + "not found.  " + "$PreAuthResultsMessages(" + i + "," + V + ")");
                        }
                    }
                }
            }
        }
        return true;
    }

    public Boolean vox_UploadDocuments() {
        int iDocuments = Integer.parseInt(getDataSet("TotalDocumentsToUpload(0,1)"));

        for (int i = 0; i < iDocuments; i++) {
            String Type = getDataSet("UploadDocument(" + i + ",1)");
            String Location = getDataSet("UploadDocument(" + i + ",2)");
            String Description = getDataSet("UploadDocument(" + i + ",3)");

            //Click document Icon
            automationObject.genfunc_Click("//*[@id=\"documentCountSpanId\"]");
            automationObject.toolSleep(2);
            //Set Document Type
            automationObject.genfunc_Click("//*[@id=\"documentType\"]/option[contains(.,\"" + Type + "\")]");
            //Set Document description
            automationObject.genfunc_ClearText("//*[@id=\"description\"]");
            automationObject.genfunc_SetText("//*[@id=\"description\"]", Description);
            //Set Document File Name (Location)
            automationObject.genfunc_Click("//*[@id=\"documentInputImg\"]/span");
            automationObject.toolSleep(3);
            automationObject.genfunc_SetPath("", Location);
            automationObject.toolSleep(2);
            //Click the Upload button
            automationObject.genfunc_Click("//*[@id=\"docSubmitButton\"]");
            System.out.println("         Upload Document: Row " + i);
            try {
                automationObject.genfunc_GetTextAlertBox(getDataMapping("$docAlertMessage"));
                automationObject.genfunc_ClickOKOnAlertBox();
            } catch (org.openqa.selenium.NoAlertPresentException g) {
                automationObject.toolSleep(1);
                automationObject.genfunc_GetText("//*[@id=\"documentPanelFormErrorBlock\"]/ul/li[1]", "");
            }
            automationObject.toolSleep(2);
            //Click close button
            automationObject.genfunc_Click("//*[@id=\"voxInfoCloseButton\"]");
        }
        return true;

    }

    public Boolean vox_CheckDocuments() {
        int iDocuments = Integer.parseInt(getDataSet("TotalDocumentsToUpload(0,1)"));
        //Click document Icon
        automationObject.genfunc_Click("//*[@id=\"documentCountSpanId\"]");
        automationObject.toolSleep(2);
        for (int i = 0; i < iDocuments; i++) {
            String Type = getDataSet("UploadDocument(" + i + ",1)");
            String Description = getDataSet("UploadDocument(" + i + ",3)");
            if (getDataSet("UploadDocument(" + i + ",4)").equalsIgnoreCase("")) {
                int n = 1;
                try {
                    //searches for the row that matches the document description
                    while (automationObject.genfunc_GetTextNoOutput("//*[@id=\"documentTbody\"]/tr[" + n + "]/td[3]", Description) == false) {
                        n++;
                    }
                    //Checks the document "Type" and "Description"
                    automationObject.genfunc_GetText("//*[@id=\"documentTbody\"]/tr[" + n + "]/td[2]", Type);
                    automationObject.genfunc_GetText("//*[@id=\"documentTbody\"]/tr[" + n + "]/td[3]", Description);
                } catch (org.openqa.selenium.InvalidElementStateException ex) {
                    System.out.println("ERROR - No Document found matching the description \"" + Description + "\"");
                }
            }
        }
        //Click close button
        automationObject.genfunc_Click("//*[@id=\"voxInfoCloseButton\"]");
        return true;
    }

    public Boolean vox_DeleteDocuments() {

        int iDocuments = Integer.parseInt(getDataSet("TotalDocumentsToDelete(0,1)"));

        automationObject.genfunc_Click("//*[@id=\"documentCountSpanId\"]");
        automationObject.toolSleep(2);
        for (int i = 0; i < iDocuments; i++) {
            String Description = getDataSet("DeleteDocument(" + i + ",3)");
            int n = 1;
            try {
                while (automationObject.genfunc_GetTextNoOutput("//*[@id=\"documentTbody\"]/tr[" + n + "]/td[3]", Description) == false) {
                    n++;
                }
                automationObject.genfunc_Click("//*[@id=\"documentTbody\"]/tr[" + n + "]/td[1]/center/span");
                automationObject.genfunc_GetTextAlertBox(getDataMapping("$docAlertMessageDelete"));
                automationObject.genfunc_ClickOKOnAlertBox();
            } catch (org.openqa.selenium.InvalidElementStateException ex) {
                System.out.println("ERROR - No Document found matching the description \"" + Description + "\"");
            }
            int j = 0;
            while (automationObject.genfunc_isElementPresent("//*[@id=\"documentTbody\"]/tr[contains(., \"" + Description + "\")]/td[3]")) {
                automationObject.toolSleep(2);
                j++;
                if (j > 30) {
                    break;
                }
            }
        }
        automationObject.genfunc_Click("//*[@id=\"voxInfoCloseButton\"]");
        return true;
    }

    public Boolean vox_Login(String strPassword) {
        if (strPassword.matches("[0-9]+")) //"(\\+|-)?[a-z]+"
        {
            automationObject.genfunc_SetText("/html/body/div[3]/form/input[2]", strPassword);
        } else {
            //To handle negative numbers
            automationObject.genfunc_SetText("//*[@id=\"loginFormUsername\"]", strPassword);
            automationObject.genfunc_SendControlA("//*[@id=\"loginFormUsername\"]");
            automationObject.genfunc_SendControlC("//*[@id=\"loginFormUsername\"]");
            automationObject.genfunc_SendControlV("/html/body/div[3]/form/input[2]");
            //automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");
        }
        return true;
    }

    public Boolean vox_ForceLogin_User(String strUsername, String strPassword) {
        //initiallogin
        automationObject.genfunc_SetText("//*[@id=\"loginFormUsername\"]", strUsername);
        automationObject.genfunc_SetText("/html/body/div[3]/form/input[2]", strPassword);
        automationObject.genfunc_Click("/html/body/div[3]/form/button");
        //alertbox 
        if (automationObject.genfunc_isAlertBoxPresent() == true) {
            automationObject.genfunc_ClickOKOnAlertBox();
            automationObject.genfunc_SetText("//*[@id=\"loginFormUsername\"]", strUsername);
            automationObject.genfunc_SetText("/html/body/div[3]/form/input[2]", strPassword);
            automationObject.genfunc_Click("/html/body/div[3]/form/button");
            return true;
        } else {
            return false;
        }
    }

    //doesn't work properly
    public Boolean vox_CheckNotesInPaws(String PolicyNumber, ArrayList<String> al) {
        //clicks the "In Process" button
        automationObject.genfunc_Click("//*[@id=\"mainMenu\"]/div/div[1]/div/div/div[2]/div[2]/div/div[2]/div/div/div[1]/div/div/div[1]/div[4]/div[2]/div[1]/div[1]");
        automationObject.toolSleep(2);
        //choose Policy No option from combobox
        automationObject.genfunc_Click("//*[@id=\"pawsforvox-813161917\"]/div/div[2]/div/div[2]/div/div/div/div[1]/div/div[2]/div/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[1]/div/div/select/option[contains(., \"Policy No\")]");
        automationObject.toolSleep(2);
        //Set text in textbox
        automationObject.genfunc_ClearText("//*[@id=\"pawsforvox-813161917\"]/div/div[2]/div/div[2]/div/div/div/div[1]/div/div[2]/div/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[2]/div/div/div/div[1]/div/input");
        automationObject.genfunc_SetText("//*[@id=\"pawsforvox-813161917\"]/div/div[2]/div/div[2]/div/div/div/div[1]/div/div[2]/div/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[2]/div/div/div/div[1]/div/input", PolicyNumber);
        //filter button
        automationObject.genfunc_Click("//*[@id=\"pawsforvox-813161917\"]/div/div[2]/div/div[2]/div/div/div/div[1]/div/div[2]/div/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[3]/div/button/span");
        automationObject.toolSleep(2);
        //click first note icon
        automationObject.genfunc_Click("//*[@id=\"pawsforvox-813161917\"]/div/div[2]/div/div[2]/div/div/div/div[1]/div/div[2]/div/div/div/div/div[1]/div/div/div/div[2]/div[2]/div/div[2]/div[1]/table/tbody/tr[1]/td[1]/div/div/span/img");
        automationObject.toolSleep(2);
        ArrayList<String> a = new ArrayList();
        int t = 0;
        int size = al.size();

        for (int n = 1; n <= size; n++) {
            a.add(al.get(t));
            t = t + 1;
        }
        //check notes
        for (int n = 0; n < size; n++) {
            try {
                int N = n + 1;
                automationObject.genfunc_GetAttribute("/html/body/div[4]/div/div/div/div[3]/div/div/div/div[1]/div/div/div/div[1]/div/div/div/div[" + N + "]/div[2]/div/div/div[1]/div/div/fieldset/div[1]/div/div/div[1]/div/div/div/div/div/div/div/div/div/div[4]/div/textarea", a.get(n));
            } catch (org.openqa.selenium.InvalidElementStateException e) {
                System.out.println("FAILED - Note \"" + a.get(n) + "\" not found.");
            }
        }
        //click the close button on the notes window
        automationObject.genfunc_Click("//*[@class=\"v-window-closebox\"]");//might work

        return true;
    }

    public Boolean vox_CycleThroughComboboxes(String iItems, String strXPath) {
        for (int i = 0; i < Integer.parseInt(iItems); i++) {
            if (automationObject.genfunc_isElementPresent(strXPath + "/option[contains(.,\"" + getDataSet("$Combobox(" + i + ",1)") + "\")]") == true) {
                automationObject.genfunc_Click(strXPath + "/option[contains(.,\"" + getDataSet("$Combobox(" + i + ",1)") + "\")]");
                automationObject.genfunc_GetAttribute(strXPath, getDataSet("$Combobox(" + i + ",1)"));
            } else {
                System.out.println("FAILED - \"" + getDataSet("$Combobox(" + i + ",1)") + "\" was not found.");
            }
        }
        return true;
    }

    public Boolean vox_PolicySearchCombinations() {
        for (int i = 0; i < Integer.parseInt(getDataSet("PolicySearchCombinationsDetails(0,1)")); i++) {
            System.out.println("    Row " + i);
            automationObject.genfunc_ClearText("//*[@id=\"policySearchFormPolicyNumber\"]");
            automationObject.genfunc_ClearText("//*[@id=\"policySearchFormPolicyholderSurname\"]");
            automationObject.genfunc_ClearText("//*[@id=\"policySearchFormPolicyholderDob\"]");
            automationObject.genfunc_ClearText("//*[@id=\"policySearchFormPostcode\"]");
            // automationObject.genfunc_ClearText("\"//*[@id=\\\"policySearchFormBrand\\\"]\"");

            if (getDataSet("PolicySearchCombinations(" + i + ",1)").equalsIgnoreCase("X")) {
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyNumber\"]", getDataSet("PolicySearch(0,1)"));
            }
            if (getDataSet("PolicySearchCombinations(" + i + ",2)").equalsIgnoreCase("X")) {
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyholderSurname\"]", getDataSet("PolicySearch(0,2)"));
            }
            if (getDataSet("PolicySearchCombinations(" + i + ",3)").equalsIgnoreCase("X")) {
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyholderSurname\"]", getDataSet("PolicySearch(1,2)"));
            }
            if (getDataSet("PolicySearchCombinations(" + i + ",4)").equalsIgnoreCase("X")) {
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyholderDob\"]", getDataSet("PolicySearch(0,3)"));
            }
            if (getDataSet("PolicySearchCombinations(" + i + ",5)").equalsIgnoreCase("X")) {
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyholderDob\"]", getDataSet("PolicySearch(1,3)"));
            }
            if (getDataSet("PolicySearchCombinations(" + i + ",6)").equalsIgnoreCase("X")) {
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPostcode\"]", getDataSet("PolicySearch(0,4)"));
            }
            if (getDataSet("PolicySearchCombinations(" + i + ",7)").equalsIgnoreCase("X")) {
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPostcode\"]", getDataSet("PolicySearch(1,4)"));
            }
            if (getDataSet("PolicySearchCombinations(" + i + ",8)").equalsIgnoreCase("X")) {
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormBrand\"]", getDataSet("PolicySearch(0,6)"));
            }

            automationObject.genfunc_Click("//*[@id=\"policySearchForm\"]/div[4]/div[2]/button");
            vox_waitForPageToLoad();
            if (getDataSet("PolicySearchCombinations(" + i + ",9)").equalsIgnoreCase("")) {

                int j = 1;
                int n = 2;
                while (!getDataSet("PolicySearch(" + n + ",1)").equalsIgnoreCase("")) {
                    try {
                        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + j + "]/td[1]/a", getDataSet("PolicySearch(" + n + ",1)"));
                        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + j + "]/td[2]", getDataSet("PolicySearch(" + n + ",2)"));
                        automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + j + "]/td[3]", getDataSet("PolicySearch(" + n + ",3)"));
                    } catch (org.openqa.selenium.InvalidElementStateException e) {
                        System.out.println("FAILED - No Result returned for row " + i);
                        if (automationObject.isElementPresent("//*[@id=\"policySearchFormErrorBlock\"]/ul/li")) {
                            automationObject.genfunc_GetText("//*[@id=\"policySearchFormErrorBlock\"]/ul/li", "");
                        }
                    }
                    j++;
                    n++;
                }
            } else {
                try {
                    automationObject.genfunc_GetText("//*[@id=\"policySearchFormErrorBlock\"]/ul/li", getDataSet("PolicySearchCombinations(" + i + ",9)"));
                } catch (org.openqa.selenium.InvalidElementStateException e) {
                    System.out.println("FAILED - No Validation Message Found for row " + i);
                }
            }
        }
        return true;
    }

    public Boolean vox_CheckPreAuthURTable(String DataSetName) {
        String User = getDataSet("$" + DataSetName + "(0,1)"); //What kind of user e.g. RSA, Vet, or Vetfone
        int iRecords = Integer.parseInt(getDataSet("$" + DataSetName + "(0,2)")); //How many rows are returned
        int FinialRowToCheck = Integer.parseInt(getDataSet("$" + DataSetName + "(0,3)")); //How many rows you want to check

        automationObject.genfunc_GetText("//*[@id=\"searchResultsTitle\"]", iRecords + " Records Found"); //Checks the number of records found message 

        int iTableRow = 1;
        for (int iDataSetRow = 1; iDataSetRow <= FinialRowToCheck; iDataSetRow++) //loops through each row of the table
        {

            System.out.println("         Data Set Row " + iDataSetRow);
            if (iDataSetRow == 16)//Checks the table page labels and then moves to the next page if you are checking more than 15 rows
            {
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_1\"]", "1");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_2\"]", "2");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_next\"]", "Next");

                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_waitForPageToLoad("//*[@id=\"pagingLinkDivpageLink_previous\"]");

                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Previous");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_1\"]", "1");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_2\"]", "2");
                if (iRecords > 30) {
                    automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_3\"]", "3");
                    automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_next\"]", "Next");
                }
            }
            //Gets the expected data for the 12 columns
            String Created = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",1)");
            String Updated = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",2)");
            String Organisation = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",3)");
            String PreAuthRef = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",4)");
            String Policy = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",5)");
            String Policyholder = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",6)");
            String Pet = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",7)");
            String Condition = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",8)");
            String Costs = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",9)");
            String FirstSigns = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",10)");
            String TreatmentFrom = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",11)");
            String TreatmentTo = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",12)");

            if (iTableRow == 16) {
                iTableRow = 1;
            }
            int n = 2; //Coloumn number
            //Compares the expected data with the actual data for each column
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Created);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Updated);
            n++;
            if (!User.equalsIgnoreCase("Vet")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Organisation);
                n++;
            }
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", PreAuthRef);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Policy);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Policyholder);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Pet);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Condition);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Costs);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", FirstSigns);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", TreatmentFrom);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", TreatmentTo);
            iTableRow++;
        }
        return true;
    }

    public Boolean vox_CheckPreAuthHistTable(String DataSetName) {
        String User = getDataSet("$" + DataSetName + "(0,1)"); //What kind of user e.g. RSA, Vet, or Vetfone
        int iRecords = Integer.parseInt(getDataSet("$" + DataSetName + "(0,2)")); //How many rows are returned
        int FinialRowToCheck = Integer.parseInt(getDataSet("$" + DataSetName + "(0,3)")); //How many rows you want to check

        automationObject.genfunc_GetText("//*[@id=\"searchResultsTitle\"]", iRecords + " Records Found"); //Checks the number of records found message 

        int iTableRow = 1;
        for (int iDataSetRow = 1; iDataSetRow <= FinialRowToCheck; iDataSetRow++) //loops through each row of the table
        {

            System.out.println("         Data Set Row " + iDataSetRow);
            if (iDataSetRow == 16) //Checks the table page labels and then moves to the next page if you are checking more than 15 rows
            {
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_1\"]", "1");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_2\"]", "2");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_next\"]", "Next");

                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_waitForPageToLoad("//*[@id=\"pagingLinkDivpageLink_previous\"]");

                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Previous");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_1\"]", "1");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_2\"]", "2");
                if (iRecords > 30) {
                    automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_3\"]", "3");
                    automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_next\"]", "Next");
                }
            }
            //Gets the expected data for the 14 columns
            String Created = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",1)");
            String Updated = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",2)");
            String Organisation = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",3)");
            String PreAuthRef = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",4)");
            String Policy = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",5)");
            String Policyholder = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",6)");
            String Pet = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",7)");
            String Condition = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",8)");
            String Costs = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",9)");
            String FirstSigns = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",10)");
            String TreatmentFrom = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",11)");
            String TreatmentTo = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",12)");
            String Status = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",13)");
            String Approver = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",14)");

            if (iTableRow == 16) {
                iTableRow = 1;
            }
            int n = 2; //Coloumn number
            //Compares the expected data with the actual data for each column
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Created);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Updated);
            n++;
            if (!User.equalsIgnoreCase("Vet")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Organisation);
                n++;
            }
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", PreAuthRef);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Policy);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Policyholder);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Pet);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Condition);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Costs);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", FirstSigns);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", TreatmentFrom);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", TreatmentTo);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Status);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Approver);
            iTableRow++;
        }
        return true;
    }

    public Boolean vox_CheckClaimsSavedTable(String DataSetName) {
        String User = getDataSet("$" + DataSetName + "(0,1)"); //What kind of user e.g. RSA, Vet, or Vetfone
        int iRecords = Integer.parseInt(getDataSet("$" + DataSetName + "(0,2)")); //How many rows are returned
        int FinialRowToCheck = Integer.parseInt(getDataSet("$" + DataSetName + "(0,3)")); //How many rows you want to check

        automationObject.genfunc_GetText("//*[@id=\"searchResultsTitle\"]", iRecords + " Records Found");  //Checks the number of records found message

        int iTableRow = 1;
        for (int iDataSetRow = 1; iDataSetRow <= FinialRowToCheck; iDataSetRow++) //loops through each row of the table
        {

            System.out.println("         Data Set Row " + iDataSetRow);
            if (iDataSetRow == 16) //Checks the table page labels and then moves to the next page if you are checking more than 15 rows
            {
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_1\"]", "1");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_2\"]", "2");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_next\"]", "Next");

                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_waitForPageToLoad("//*[@id=\"pagingLinkDivpageLink_previous\"]");

                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Previous");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_1\"]", "1");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_2\"]", "2");
                if (iRecords > 30) {
                    automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_3\"]", "3");
                    automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_next\"]", "Next");
                }
            }
            //Gets the expected data for the 11 columns
            String Created = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",1)");
            String Updated = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",2)");
            String ClaimFormRef = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",3)");
            String PreAuthRef = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",4)");
            String Policy = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",5)");
            String Policyholder = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",6)");
            String Pet = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",7)");
            String Condition = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",8)");
            String FirstSigns = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",9)");
            String TreatmentFrom = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",10)");
            String TreatmentTo = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",11)");

            if (iTableRow == 16) {
                iTableRow = 1;
            }
            int n = 2; //Coloumn number
            //Compares the expected data with the actual data for each column
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Created);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Updated);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", ClaimFormRef);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", PreAuthRef);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Policy);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Policyholder);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Pet);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Condition);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", FirstSigns);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", TreatmentFrom);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", TreatmentTo);
            iTableRow++;
        }
        return true;
    }

    public Boolean vox_CheckClaimsHistTable(String DataSetName) {
        String User = getDataSet("$" + DataSetName + "(0,1)");  //What kind of user e.g. RSA, Vet, or Vetfone
        int iRecords = Integer.parseInt(getDataSet("$" + DataSetName + "(0,2)")); //How many rows are returned
        int FinialRowToCheck = Integer.parseInt(getDataSet("$" + DataSetName + "(0,3)")); //How many rows you want to check

        automationObject.genfunc_GetText("//*[@id=\"searchResultsTitle\"]", iRecords + " Records Found");  //Checks the number of records found message

        int iTableRow = 1;
        for (int iDataSetRow = 1; iDataSetRow <= FinialRowToCheck; iDataSetRow++) //loops through each row of the table
        {

            System.out.println("         Data Set Row " + iDataSetRow);
            if (iDataSetRow == 16) //Checks the table page labels and then moves to the next page if you are checking more than 15 rows
            {
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_1\"]", "1");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_2\"]", "2");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_next\"]", "Next");

                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_waitForPageToLoad("//*[@id=\"pagingLinkDivpageLink_previous\"]");

                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Previous");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_1\"]", "1");
                automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_2\"]", "2");
                if (iRecords > 30) {
                    automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_3\"]", "3");
                    automationObject.genfunc_GetText("//*[@id=\"pagingLinkDivpageLink_next\"]", "Next");
                }
            }
            //Gets the expected data for the 13 columns
            String Created = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",1)");
            String Updated = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",2)");
            String Organisation = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",3)");
            String ClaimFormRef = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",4)");
            String PaymentRef = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",5)");
            String Policy = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",6)");
            String Policyholder = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",7)");
            String Pet = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",8)");
            String Condition = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",9)");
            String FirstSigns = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",10)");
            String TreatmentFrom = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",11)");
            String TreatmentTo = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",12)");
            String Status = getDataSet("$" + DataSetName + "(" + iDataSetRow + ",13)");

            if (iTableRow == 16) {
                iTableRow = 1;
            }
            int n = 2; //Coloumn number
            //Compares the expected data with the actual data for each column
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Created);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Updated);
            n++;
            if (!User.equalsIgnoreCase("Vet")) {
                automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Organisation);
                n++;
            }
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", ClaimFormRef);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", PaymentRef);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Policy);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Policyholder);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Pet);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Condition);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", FirstSigns);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", TreatmentFrom);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", TreatmentTo);
            n++;
            automationObject.genfunc_GetText("//*[@id=\"resultsTbody\"]/tr[" + iTableRow + "]/td[" + n + "]", Status);
            iTableRow++;
        }
        return true;
    }

    public Boolean vox_ReorderingColumns(String ColumnXPath, String ascendingDataSet, String descendingDataSet) {
        System.out.println("         Data Set: " + ascendingDataSet);
        automationObject.genfunc_Click(ColumnXPath); //clicks on column header to order by ascending
        automationObject.genfunc_waitForPageToLoad(ColumnXPath);
        vox_CheckPreAuthURTable(ascendingDataSet);
        System.out.println("         Data Set: " + descendingDataSet);
        automationObject.genfunc_Click(ColumnXPath); //clicks on column header to order by descending
        automationObject.genfunc_waitForPageToLoad(ColumnXPath);
        vox_CheckPreAuthURTable(descendingDataSet);
        return true;
    }

    public Boolean vox_Scenario_PreAuthUR_Reordering() {
        String User = getDataSet("$PreAuthURTableCheck(0,1)");

        ArrayList<String> a = new ArrayList();
        ArrayList<String> b = new ArrayList();
        ArrayList<String> c = new ArrayList();

        //column xpaths
        a.add("//*[@id=\"headercreated\"]");
        a.add("//*[@id=\"headerupdated\"]");
        if (!User.equalsIgnoreCase("Vet")) {
            a.add("//*[@id=\"headerorganisationName\"]");
        }
        a.add("//*[@id=\"headerpreauthRef\"]");
        a.add("//*[@id=\"headerpolicyName\"]");
        a.add("//*[@id=\"headerpolicyHolder\"]");
        a.add("//*[@id=\"headerpetName\"]");
        a.add("//*[@id=\"headerconditionName\"]");
        a.add("//*[@id=\"headerestimatedCosts\"]");
        a.add("//*[@id=\"headerfirstSigns\"]");
        a.add("//*[@id=\"headertreatmentFrom\"]");
        a.add("//*[@id=\"headertreatmentTo\"]");
        //data set names
        for (int i = 1; i < 10; i++) {
            b.add("PreAuthURAsc00" + i);
            c.add("PreAuthURDesc00" + i);
        }
        for (int i = 10; i < 13; i++) {
            if (User.equalsIgnoreCase("Vet") && i == 12) {
                break;
            }
            b.add("PreAuthURAsc0" + i);
            c.add("PreAuthURDesc0" + i);
        }
        //Clicks each column header and checks the table rows are in the expected order
        for (int i = 0; i < 12; i++) {
            if (User.equalsIgnoreCase("Vet") && i == 11) {
                break;
            }
            String Header = a.get(i).replace("//*[@id=\"header", "");
            Header = Header.replace("\"]", "");
            System.out.println("         Type of User: " + User);
            System.out.println("         Column Header: " + Header);
            System.out.println("         Data Set: " + b.get(i));
            automationObject.genfunc_Click(a.get(i)); //clicks on column header to order by ascending
            automationObject.genfunc_waitForPageToLoad(a.get(i));
            vox_CheckPreAuthURTable(b.get(i));
            System.out.println("         Type of User: " + User);
            System.out.println("         Column Header: " + Header);
            System.out.println("         Data Set: " + c.get(i));
            automationObject.genfunc_Click(a.get(i)); //clicks on column header to order by descending
            automationObject.genfunc_waitForPageToLoad(a.get(i));
            vox_CheckPreAuthURTable(c.get(i));
            return true;
        }
        return true;
    }

    public Boolean vox_Scenario_PreAuthHist_Reordering() {
        String User = getDataSet("$PreAuthHistTableCheck(0,1)");

        ArrayList<String> a = new ArrayList();
        ArrayList<String> b = new ArrayList();
        ArrayList<String> c = new ArrayList();

        //column xpaths
        a.add("//*[@id=\"headercreated\"]");
        a.add("//*[@id=\"headerupdated\"]");
        if (!User.equalsIgnoreCase("Vet")) {
            a.add("//*[@id=\"headerorganisationName\"]");
        }
        a.add("//*[@id=\"headerpreauthRef\"]");
        a.add("//*[@id=\"headerpolicyName\"]");
        a.add("//*[@id=\"headerpolicyHolder\"]");
        a.add("//*[@id=\"headerpetName\"]");
        a.add("//*[@id=\"headerconditionName\"]");
        a.add("//*[@id=\"headerestimatedCosts\"]");
        a.add("//*[@id=\"headerfirstSigns\"]");
        a.add("//*[@id=\"headertreatmentFrom\"]");
        a.add("//*[@id=\"headertreatmentTo\"]");
        a.add("//*[@onclick=\"javascript:preauthHistoricalForm.onSortClick('status')\"]");
        a.add("//*[@onclick=\"javascript:preauthHistoricalForm.onSortClick('approver')\"]");
        //data set names
        for (int i = 1; i < 10; i++) {
            b.add("PreAuthHistAsc00" + i);
            c.add("PreAuthHistDesc00" + i);
        }
        for (int i = 10; i < 15; i++) {
            if (User.equalsIgnoreCase("Vet") && i == 14) {
                break;
            }
            b.add("PreAuthHistAsc0" + i);
            c.add("PreAuthHistDesc0" + i);
        }
        //Clicks each column header and checks the table rows are in the expected order
        for (int i = 0; i < 14; i++) {
            if (User.equalsIgnoreCase("Vet") && i == 13) {
                break;
            }
            String Header = a.get(i).replace("//*[@id=\"header", "");
            Header = Header.replace("//*[@onclick=\"javascript:preauthHistoricalForm.onSortClick('", "");
            Header = Header.replace("')", "");
            Header = Header.replace("\"]", "");
            System.out.println("         Type of User: " + User);
            System.out.println("         Column Header: " + Header);
            System.out.println("         Data Set: " + b.get(i));
            automationObject.genfunc_Click(a.get(i)); //clicks on column header to order by ascending
            automationObject.genfunc_waitForPageToLoad(a.get(i));
            vox_CheckPreAuthHistTable(b.get(i)); //checks the table rows are in the expected order
            System.out.println("         Type of User: " + User);
            System.out.println("         Column Header: " + Header);
            System.out.println("         Data Set: " + c.get(i));
            automationObject.genfunc_Click(a.get(i)); //clicks on column header to order by descending
            automationObject.genfunc_waitForPageToLoad(a.get(i));
            vox_CheckPreAuthHistTable(c.get(i)); //checks the table rows are in the expected order
        }
        return true;
    }

    public Boolean vox_Scenario_ClaimsSaved_Reordering() {

        ArrayList<String> a = new ArrayList();
        ArrayList<String> b = new ArrayList();
        ArrayList<String> c = new ArrayList();

        //column xpaths
        a.add("//*[@id=\"headercreated\"]");
        a.add("//*[@id=\"headerupdated\"]");
        a.add("//*[@id=\"headerclaimFormRef\"]");
        a.add("//*[@id=\"headerpreauthRef\"]");
        a.add("//*[@id=\"headerpolicyName\"]");
        a.add("//*[@id=\"headerpolicyHolder\"]");
        a.add("//*[@id=\"headerpetName\"]");
        a.add("//*[@id=\"headerconditionName\"]");
        a.add("//*[@id=\"headerfirstSigns\"]");
        a.add("//*[@id=\"headertreatmentFrom\"]");
        a.add("//*[@id=\"headertreatmentTo\"]");
        //data set names
        for (int i = 1; i < 10; i++) {
            b.add("ClaimsSavedAsc00" + i);
            c.add("ClaimsSavedDesc00" + i);
        }
        for (int i = 10; i < 12; i++) {
            b.add("ClaimsSavedAsc0" + i);
            c.add("ClaimsSavedDesc0" + i);
        }
        //Clicks each column header and checks the table rows are in the expected order
        for (int i = 0; i < 11; i++) {
            String Header = a.get(i).replace("//*[@id=\"header", "");
            Header = Header.replace("\"]", "");
            System.out.println("         Column Header: " + Header);
            System.out.println("         Data Set: " + b.get(i));
            automationObject.genfunc_Click(a.get(i)); //clicks on column header to order by ascending
            automationObject.genfunc_waitForPageToLoad(a.get(i));
            vox_CheckClaimsSavedTable(b.get(i));
            System.out.println("         Column Header: " + Header);
            System.out.println("         Data Set: " + c.get(i));
            automationObject.genfunc_Click(a.get(i)); //clicks on column header to order by descending
            automationObject.genfunc_waitForPageToLoad(a.get(i));
            vox_CheckClaimsSavedTable(c.get(i));
        }
        return true;
    }

    public Boolean vox_Scenario_ClaimsHist_Reordering() {
        String User = getDataSet("$ClaimsHistTableCheck(0,1)");

        ArrayList<String> a = new ArrayList();
        ArrayList<String> b = new ArrayList();
        ArrayList<String> c = new ArrayList();

        //column xpaths
        a.add("//*[@id=\"headersubmitted\"]");
        a.add("//*[@id=\"headerupdated\"]");
        if (!User.equalsIgnoreCase("Vet")) {
            a.add("//*[@id=\"headerorganisationName\"]");
        }
        a.add("//*[@id=\"headerclaimFormRef\"]");
        a.add("//*[@id=\"headerpaymentRef\"]");
        a.add("//*[@id=\"headerpolicyName\"]");
        a.add("//*[@id=\"headerpolicyHolder\"]");
        a.add("//*[@id=\"headerpetName\"]");
        a.add("//*[@id=\"headerconditionName\"]");
        a.add("//*[@id=\"headerfirstSigns\"]");
        a.add("//*[@id=\"headertreatmentFrom\"]");
        a.add("//*[@id=\"headertreatmentTo\"]");
        a.add("//*[@id=\"headerstatus\"]");
        //data set names
        for (int i = 1; i < 10; i++) {
            b.add("ClaimsHistAsc00" + i);
            c.add("ClaimsHistDesc00" + i);
        }
        for (int i = 10; i < 14; i++) {
            if (User.equalsIgnoreCase("Vet") && i == 13) {
                break;
            }
            b.add("ClaimsHistAsc0" + i);
            c.add("ClaimsHistDesc0" + i);
        }
        //Clicks each column header and checks the table rows are in the expected order
        for (int i = 0; i < 13; i++) {
            if (User.equalsIgnoreCase("Vet") && i == 12) {
                break;
            }
            String Header = a.get(i).replace("//*[@id=\"header", "");
            Header = Header.replace("\"]", "");
            System.out.println("         Type of User: " + User);
            System.out.println("         Column Header: " + Header);
            System.out.println("         Data Set: " + b.get(i));
            automationObject.genfunc_Click(a.get(i)); //clicks on column header to order by ascending
            automationObject.genfunc_waitForPageToLoad(a.get(i));
            vox_CheckClaimsHistTable(b.get(i));
            System.out.println("         Type of User: " + User);
            System.out.println("         Column Header: " + Header);
            System.out.println("         Data Set: " + c.get(i));
            automationObject.genfunc_Click(a.get(i)); //clicks on column header to order by descending
            automationObject.genfunc_waitForPageToLoad(a.get(i));
            vox_CheckClaimsHistTable(c.get(i));
        }
        return true;
    }

    /////////////////////////////////////////////////////////////////////////////////
    //VOX Generic Methods 
    /////////////////////////////////////////////////////////////////////////////////
    public Boolean vox_ForceLogin_MultiUser(String strUsername, String strPassword, String strOrganisation) {
        //initiallogin
        automationObject.genfunc_SetText("//*[@id=\"loginFormUsername\"]", strUsername);
        automationObject.genfunc_SetText("/html/body/div[3]/form/input[2]", strPassword);
        automationObject.genfunc_Click("/html/body/div[3]/form/button");
        //alertbox
        if (automationObject.genfunc_isAlertBoxPresent() == true) {
            //alertbox->login
            automationObject.genfunc_ClickOKOnAlertBox();
            automationObject.genfunc_SetText("//*[@id=\"loginFormUsername\"]", strUsername);
            automationObject.genfunc_SetText("/html/body/div[3]/form/input[2]", strPassword);
            automationObject.genfunc_Click("/html/body/div[3]/form/button");
            //selectorganisation
            if (automationObject.genfunc_IsObjectVisible("/html/body/div[3]/form/div[2]/button") == true) {
                automationObject.genfunc_SetText("//*[@id=\"orgLink\"]", strOrganisation);
                automationObject.genfunc_Click("/html/body/div[3]/form/div[2]/button");
            } else {
                return false;
            }
        } else {
            //selectorganisation
            if (automationObject.genfunc_IsObjectVisible("/html/body/div[3]/form/div[2]/button") == true) {
                automationObject.genfunc_SetText("//*[@id=\"orgLink\"]", strOrganisation);
                automationObject.genfunc_Click("/html/body/div[3]/form/div[2]/button");
            } else {
                return false;
            }
        }
        return true;
    }

    public Boolean vox_CogMenuSelect(String strMenuName) {
        automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/a/span");
        //*[@id="vox-top-nav-header"]/div[2]/ul[1]/li/a
        switch (strMenuName.toLowerCase()) {
            case "search user":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Search User\")]/a");
                break;
            case "display users":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Display Users\")]/a");
                break;
            case "new rsa user":
            case "rsa":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"New RSA User\")]/a");
                break;
            case "new vet user":
            case "vet":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"New Vet User\")]/a");
                break;
            case "new vetfone user":
            case "vetfone":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"New Vetfone User\")]/a");
                break;
            case "search organisation":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Search Organisation\")]/a");
                break;
            case "edit organisation":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Edit Organisation\")]/a");
                break;
            case "reporting":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Reporting\")]/a");
                break;
            case "vet messages":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Vet Messages\")]/a");
                break;
            case "account":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Account\")]/a");
                break;
            case "help":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Help\")]/a");
                break;
            case "logout":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[2]/li/ul/li[contains(.,\"Logout\")]/a");
                break;
        }
        automationObject.toolSleep(1);

        return true;
    }

    public Boolean vox_ClaimsMenuSelect(String strMenuName) {
        automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/a");

        switch (strMenuName.toLowerCase()) {
            case "search policy":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/ul/li[contains(.,\"Search Policy\")]/a");
                break;
            case "preauths (under review)":
            case "review preauth":
            case "preauths under review":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/ul/li[contains(.,\"PreAuths (Under Review)\")]/a");
                break;
            case "preauths (historical)":
            case "historical preauth":
            case "preauths historical":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/ul/li[contains(.,\"PreAuths (Historical)\")]/a");
                break;
            case "claims (historical)":
            case "historical claims":
            case "claims historical":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/ul/li[contains(.,\"Claims (Historical)\")]/a");
                break;
            case "claims (saved)":
            case "saved claims":
            case "claims saved":
                automationObject.genfunc_Click("//*[@id=\"vox-top-nav-header\"]/div[2]/ul[1]/li/ul/li[contains(.,\"Claims (Saved)\")]/a");
        }
        automationObject.toolSleep(1);

        return true;
    }

    public Boolean vox_RegisterUser(String strUser, String strFirstname, String strSurname, String currentPassword, String newPassword, String confirmPassword) {
        automationObject.genfunc_ClearText("//*[@id=\"changeUserFormUserName\"]");
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormUserName\"]", strUser);
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormFirstName\"]", strFirstname);
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormLastName\"]", strSurname);
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormUserName\"]", currentPassword);
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormFirstName\"]", newPassword);
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormLastName\"]", confirmPassword);

        automationObject.genfunc_Click("//*[@id=\"topButtonSave\"]");
        return true;
    }

    public Boolean vox_CreateUser(String strEmail, String strPAL, String selectRole) {
        automationObject.genfunc_SetText("//*[@id=\"userFormEmail\"]", strEmail);
        automationObject.genfunc_ClearText("//*[@id=\"userFormPaymentAuthorityLimit\"]");
        automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", strPAL);
        automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
        automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
        automationObject.genfunc_SendControlV("//*[@id=\"userFormPaymentAuthorityLimit\"]");
        //temporary
        switch (selectRole.toLowerCase()) {
            case "add":
                automationObject.genfunc_Click("//*[@id=\"arole_0\"]");
                automationObject.genfunc_Click("//*[@id=\"addRolesButton\"]");
                break;
            case "remove":
                automationObject.genfunc_Click("//*[@id=\"removeRolesButton\"]");
                break;
            default:
                automationObject.genfunc_Click("//*[@id=\"arole_0\"]");
                automationObject.genfunc_Click("//*[@id=\"addRolesButton\"]");

        }
        automationObject.genfunc_Click("//*[@id=\"saveButton\"]");

        return true;
    }

    public Boolean vox_ReportListboxSelect() {
        automationObject.genfunc_ClickAndHold("//*[@id=\"availableOrgsBody\"]/tr[contains(.,\"RSA\")]/a");

        automationObject.genfunc_ClickAndHold("//*[@id=\"availableOrgsBody\"]/tr[contains(.,\"Vetfone\")]/a");

        automationObject.genfunc_Click("//*[@id=\"addOrgsButton\"]");
        return true;
    }

    public Boolean vox_ChangeUserDetails(String strUser, String strFirstname, String strSurname, String strEmail) {
        automationObject.genfunc_Click("//*[@id=\"topButtonEdit\"]");
        automationObject.toolSleep(1);
        //username
        automationObject.genfunc_ClearText("//*[@id=\"changeUserFormUserName\"]");
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormUserName\"]", strUser);
        //firstname
        automationObject.genfunc_ClearText("//*[@id=\"changeUserFormFirstName\"]");
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormFirstName\"]", strFirstname);
        //surname
        automationObject.genfunc_ClearText("//*[@id=\"changeUserFormLastName\"]");
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormLastName\"]", strSurname);
        //email
        automationObject.genfunc_ClearText("//*[@id=\"changeUserFormEmail\"]");
        automationObject.genfunc_SetText("//*[@id=\"changeUserFormEmail\"]", strEmail);
        //save
        automationObject.genfunc_Click("//*[@id=\"topButtonSave\"]");
        automationObject.toolSleep(2);
        if (automationObject.genfunc_isAlertBoxPresent() == true) {
            automationObject.genfunc_ClickOKOnAlertBoxSkip();
        }

        return true;
    }

    public Boolean vox_ChangeUserPassword(String currentPass, String newPass, String confirmPass) {
        //set password
        automationObject.genfunc_SetText("//*[@id=\"changePwdFormCurrentPassword\"]", currentPass);
        automationObject.genfunc_SetText("//*[@id=\"changePwdFormNewPassword\"]", newPass);
        automationObject.genfunc_SetText("//*[@id=\"changePwdFormConfirmNewPass\"]", confirmPass);
        //save
        automationObject.genfunc_Click("//*[@id=\"bottomButtonSave\"]");
        automationObject.toolSleep(2);
        if (automationObject.genfunc_isAlertBoxPresent() == true) {
            automationObject.genfunc_ClickOKOnAlertBoxSkip();
        }

        return true;
    }

    public Boolean vox_QuickSearchPolicy(String strPolicy) {
        automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", strPolicy);
        automationObject.genfunc_Click("//*[@id=\"quickSearchForm\"]/button");
        if (automationObject.genfunc_Click("//*[@id=\"quickSearchForm\"]/ul/li[contains(.,\"Policy\")]/a") == true) {
            System.out.println("Element is not Visible");
        } else {
            automationObject.genfunc_Click("//*[@id=\"quickSearchForm\"]/ul/li[contains(.,\"Policy\")]/a");

        }
        return true;
    }

    public Boolean vox_QuickSearchOrganisation(String strOrganisation) {
        automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", strOrganisation);
        automationObject.genfunc_Click("//*[@id=\"quickSearchForm\"]/button");
        if (automationObject.genfunc_isElementPresentChk("//*[@id=\"quickSearchForm\"]/ul/li[contains(.,\"Organisation\")]/a") == true) {
            automationObject.genfunc_Click("//*[@id=\"quickSearchForm\"]/ul/li[contains(.,\"Organisation\")]/a");
        } else {
            System.out.println("Element is not Visible");
        }
        return true;
    }

    public Boolean vox_QuickSearchUser(String strUser) {
        automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", strUser);
        automationObject.genfunc_Click("//*[@id=\"quickSearchForm\"]/button");
        if (automationObject.genfunc_Click("//*[@id=\"quickSearchForm\"]/ul/li[contains(.,\"User\")]/a") == true) {
            System.out.println("Element is not Visible");
        } else {
            automationObject.genfunc_Click("//*[@id=\"quickSearchForm\"]/ul/li[contains(.,\"User\")]/a");
        }
        return true;
    }

    public Boolean vox_SearchOrganisation(String strOrg) {
        //SearchCriteria
        automationObject.genfunc_SetText("//*[@id=\"orgSearchFormKeyword\"]", strOrg);
        automationObject.genfunc_Click("//*[@id=\"orgSearchForm\"]/div/div[2]/button");
        return true;
    }

    public Boolean vox_SearchUser(String strUser, String strStatus, String strOrg) {
        automationObject.genfunc_SetText("//*[@id=\"userSearchFormKeyword\"]", strUser);
        //temporary use of switch/case
        switch (strStatus.toLowerCase()) {
            default:
                automationObject.genfunc_SetText("//*[@id=\"status\"]", "--All--");
                break;
            case "active":
                automationObject.genfunc_SetText("//*[@id=\"status\"]", "active");
                break;
            case "admin password reset":
            case "admin reset":
                automationObject.genfunc_SetText("//*[@id=\"status\"]", "admin_password_reset");
                break;
            case "locked for authentication":
            case "locked":
                automationObject.genfunc_SetText("//*[@id=\"status\"]", "locked_for_authentication");
                break;
            case "new":
                automationObject.genfunc_SetText("//*[@id=\"status\"]", "new");
                break;
            case "user password reset":
            case "user reset":
                automationObject.genfunc_SetText("//*[@id=\"status\"]", "user_password_reset");
                break;
        }
        automationObject.genfunc_SetText("//*[@id=\"userOrganisationUuid\"]", strOrg);
        automationObject.genfunc_Click("//*[@id=\"userSearchForm\"]/div/div[2]/button");

        return true;
    }

    public Boolean vox_PolicySearch(String strPolicyNumber, String strSurname, String strDOB, String strPostcode) {
        automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyNumber\"]", strPolicyNumber);
        automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyholderSurname\"]", strSurname);
        automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyholderDob\"]", strDOB);
        automationObject.genfunc_SetText("//*[@id=\"policySearchFormPostcode\"]", strPostcode);
        automationObject.genfunc_Click("//*[@id=\"policySearchForm\"]/div[4]/div[2]/button");

        automationObject.toolSleep(2);
        return true;
    }

    public Boolean vox_ConsentForm(String strCustomerType, String strCustomerName) {
        automationObject.genfunc_SetText("//*[@id=\"customerType\"]", strCustomerType);
        if (automationObject.genfunc_isElementPresentChk("//*[@id=\"thirdPartyName\"]") == true) {
            automationObject.genfunc_SetText("//*[@id=\"thirdPartyName\"]", strCustomerName);
        }
        automationObject.genfunc_Click("//*[@id=\"infoSubmitButton\"]");
        //if(automationObject.genfunc_isElementPresentChkElement("//*[@id=\"thirdPartyName\"]") == true);
        //   {
        //        automationObject.genfunc_SetText("//*[@id=\"thirdPartyName\"]", strCustomerName);
        //   }
        return true;
    }

    public Boolean vox_SelectResult() {
        automationObject.genfunc_ExplicitWait("//*[@id=\"resultsTbody\"]/tr/td[1]/a");
        automationObject.genfunc_Click("//*[@id=\"resultsTbody\"]/tr/td[1]/a");

        return true;
    }

    public Boolean vox_EditUser(String strPAL, String selectRole) {
        automationObject.genfunc_Click("//*[@id=\"topButtonEdit\"]");

        automationObject.genfunc_ClearText("//*[@id=\"userFormPaymentAuthorityLimit\"]");
        automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");

        automationObject.toolSleep(1);

        automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", strPAL);
        automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
        automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
        automationObject.genfunc_SendControlV("//*[@id=\"userFormPaymentAuthorityLimit\"]");

        if (strPAL.equalsIgnoreCase("#N/A") == true) {
            automationObject.genfunc_ClearText("//*[@id=\"userFormPaymentAuthorityLimit\"]");
        }
        //temporary
        switch (selectRole.toLowerCase()) {
            case "add":
                automationObject.genfunc_Click("//*[@id=\"arole_0\"]");
                automationObject.genfunc_Click("//*[@id=\"addRolesButton\"]");
                break;
            case "remove":
                automationObject.genfunc_Click("//*[@id=\"srole_0\"]");
                automationObject.genfunc_Click("//*[@id=\"removeRolesButton\"]");
                break;
            default:
                automationObject.genfunc_Click("//*[@id=\"arole_0\"]");
                automationObject.genfunc_Click("//*[@id=\"addRolesButton\"]");
        }
        automationObject.genfunc_Click("//*[@id=\"topButtonSave\"]");

        return true;
    }

    public Boolean vox_SelectRoles(String strRoles) {
        automationObject.genfunc_Click("");
        automationObject.genfunc_Click("");

        return true;
    }

    public Boolean vox_EditOrganisation(String strLimit, String strValue, String strTolerance, String strSortCode, String strBankNumber, String strPassword) {
        automationObject.genfunc_Click("//*[@id=\"editButton\"]");
        automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentAuthorisationLimit\"]");
        automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentToleranceValue\"]");
        automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentTolerancePcnt\"]");
        automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormBankSortCode\"]");
        automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormBankAccountNumber\"]");
        automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPassword\"]");

        automationObject.genfunc_Click("//*[@id=\"saveButton\"]");
        return true;
    }

    public Boolean vox_GenerateReport(String strReportName, String strDateFrom, String strDateTo, String selectOrg) {
        automationObject.genfunc_SetText("//*[@id=\"reportSelect\"]", strReportName);
        automationObject.genfunc_SetText("//*[@id=\"vetReportFormDateFromField\"]", strDateFrom);
        automationObject.genfunc_SetText("//*[@id=\"vetReportFormDateToField\"]", strDateTo);
        //Select Organisation
        //automationObject.genfunc_ClickAndHold("//*[@id=\"availableOrgsBody\"]/tr[contains(.,\"RSA\")]");
        //automationObject.genfunc_ClickAndHold("//*[@id=\"availableOrgsBody\"]/tr[contains(.,\"1 Call Advice Direct Vet (YO41 1WU)\")]");
        //automationObject.genfunc_ClickAndHold("//*[@id=\"availableOrgsBody\"]/tr[contains(.,\"Vetfone\")]");
        //Temporary
        switch (selectOrg.toLowerCase()) {
            case "add":
                automationObject.genfunc_Click("//*[@id=\"aorg_0\"]");
                automationObject.genfunc_Click("//*[@id=\"addOrgsButton\"]");
                break;
            case "remove":
                automationObject.genfunc_Click("//*[@id=\"removeAllOrgsButton\"]");
                break;
        }
        automationObject.genfunc_Click("//*[@id=\"vetReportForm\"]/div[4]/div/button");

        return true;
    }

    //TODO
    public Boolean vox_AddFixedRateCard(String strCostCat, String strCostName, String strRateType, String strPriceFrom, String strPriceTo) {
        automationObject.genfunc_Click("//*[@id=\"addRateCardButton\"]");
        automationObject.genfunc_Click("//*[@id=\"rate_card_row_0_costCategory_selector\"]");
        automationObject.genfunc_Click("");
        automationObject.genfunc_Click("");

        return true;
    }

    //TODO
    public Boolean vox_AddPercentRateCard(String strCostCat, String strCostName, String strRateType, String strDiscount) {
        automationObject.genfunc_Click("//*[@id=\"addRateCardButton\"]");
        automationObject.genfunc_Click("//*[@id=\"rate_card_row_0_costCategory_selector\"]");
        automationObject.genfunc_Click("");
        automationObject.genfunc_Click("");
        automationObject.genfunc_Click("//*[@=id=\"rte");
        return true;
    }

    //Temporarily working(Need Table Row/Data from input)
    public Boolean vox_CreateVetMessage(String strMessage) {
        automationObject.genfunc_Click("//*[@id=\"addMessageButton\"]");
        automationObject.genfunc_SetText("//*[@id=\"editMessageFormMessage\"]", strMessage);
        automationObject.genfunc_Click("//*[@id=\"editMessageSaveButton\"]");

        return true;
    }

    public Boolean vox_DeleteVetMessage() {
        automationObject.genfunc_Click("//*[@id=\"messageTBody\"]/tr/td[1]/span");
        automationObject.genfunc_ClickOKOnAlertBox();

        return true;
    }

    public Boolean vox_EditVetMessage(String tablerow, String strMessage) {
        automationObject.genfunc_Click("//*[@id=\"messageTBody\"]/tr[" + tablerow + "]/td[5]/a");
        automationObject.genfunc_ClearText("//*[@id=\"editMessageFormMessage\"]");
        automationObject.genfunc_SetText("//*[@id=\"editMessageFormMessage\"]", strMessage);
        automationObject.genfunc_Click("//*[@id=\"editMessageSaveButton\"]");

        return true;
    }

    public Boolean vox_ReorderVetMessage(String tablerow, String tablebutton) {
        automationObject.genfunc_Click("//*[@id=\"messageTBody\"]/tr[" + tablerow + "]/td[2]/span" + tablebutton + "");
        automationObject.genfunc_Click("//*[@id=\"saveOrderButton\"]");
        return true;
    }

    public boolean vox_CheckValidation(String XPath, String Validation) {
        automationObject.genfunc_ExplicitWait(XPath);
        if (automationObject.genfunc_isElementPresentChk(XPath) == true) {
            automationObject.genfunc_GetText(XPath, Validation);
        }
        return true;
    }

    public Boolean vox_CheckResults(String strResult) {
        automationObject.genfunc_ExplicitWait("//*[@id=\"searchResultsTitle\"]");
        automationObject.genfunc_GetText("//*[@id=\"searchResultsTitle\"]", "" + strResult + " Records Found");
        //should use if statements
        switch (strResult) {
            case "0":
                System.out.println("Elements not Present");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_1\"]", "No");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "No");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "No");
                break;
            case "1":
            case "15":
                //initial page
                System.out.println("Element Check on Page 1");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_1\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "No");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "No");
                break;
            case "16":
            case "30":
            case "22":
                //initial page
                System.out.println("Element Check on Page 1");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_1\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_2\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "No");
                //2nd page
                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_ExplicitWait("//*[@id=\"searchResultsTitle\"]");
                System.out.println("Element Check on Page 2");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "No");
                break;
            case "31":
            case "45":
            case "43":
                //initial page
                System.out.println("Element Check on Page 1");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_1\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_2\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_3\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "No");
                //2nd page
                System.out.println("Element Check on Page 2");
                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_ExplicitWait("//*[@id=\"searchResultsTitle\"]");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "Yes");
                //3rd page
                System.out.println("Element Check on Page 3");
                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_ExplicitWait("//*[@id=\"searchResultsTitle\"]");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "No");
                break;
            case "46":
            case "60":
                //initial page
                System.out.println("Element Check on Page 1");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_1\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_2\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_3\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_4\"]", "Yes");
                //2nd page
                System.out.println("Element Check on Page 2");
                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_ExplicitWait("//*[@id=\"searchResultsTitle\"]");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "Yes");
                //3rd page
                System.out.println("Element Check on Page 3");
                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_ExplicitWait("//*[@id=\"searchResultsTitle\"]");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "Yes");
                //4th page
                System.out.println("Element Check on Page 4");
                automationObject.genfunc_Click("//*[@id=\"pagingLinkDivpageLink_next\"]");
                automationObject.genfunc_ExplicitWait("//*[@id=\"searchResultsTitle\"]");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_previous\"]", "Yes");
                genfunc_isElementPresentOutput("//*[@id=\"pagingLinkDivpageLink_next\"]", "No");
                break;
        }
        return true;
    }

    public Boolean vox_ForgottenUsername(String strEmail) {
        automationObject.genfunc_Click("//*[@id=\"forgetusername\"]");
        automationObject.genfunc_SetText("//*[@id=\"forgotUserNameFormEmail\"]", strEmail);
        automationObject.genfunc_Click("//*[@id=\"forgotUserNameForm\"]/div[2]/button");
        return true;
    }

    public Boolean vox_ForgottenPassword(String strEmail) {
        automationObject.genfunc_Click("//*[@id=\"forgetPassword\"]");
        automationObject.genfunc_SetText("//*[@id=\"forgotPasswordFormEmail\"]", strEmail);
        automationObject.genfunc_Click("//*[@id=\"forgotPasswordForm\"]/div[2]/button");
        return true;
    }

    /////////////////////////////////////////////////////////////////////////////////
    //VOX Scenario - Validation Methods
    /////////////////////////////////////////////////////////////////////////////////
    public Boolean vox_Validate_Reporting() {
        String Username = (getDataSet("$reporting(0,1)"));
        String Password = (getDataSet("$reporting(0,2)"));

        vox_ForceLogin_User(Username, Password);
        vox_CogMenuSelect("Reporting");
        int i = 0;
        while (i < Integer.parseInt(getDataSet("$rows(0,1)"))) {
            String ReportName = (getDataSet("$reporting(" + i + ",3)"));
            String DateFrom = (getDataSet("$reporting(" + i + ",4)"));
            String DateTo = (getDataSet("$reporting(" + i + ",5)"));
            String Organisation = (getDataSet("$reporting(" + i + ",6)"));

            String Validation1 = (getDataSet("$reporting(" + i + ",7)"));
            String Validation2 = (getDataSet("$reporting(" + i + ",8)"));
            String Validation3 = (getDataSet("$reporting(" + i + ",9)"));
            String Validation4 = (getDataSet("$reporting(" + i + ",10)"));
            String Validation5 = (getDataSet("$reporting(" + i + ",11)"));

            try {

                if (ReportName.equalsIgnoreCase("#N/A")) {
                    vox_CogMenuSelect("Reporting");
                    automationObject.genfunc_ExplicitWait("//*[@id=\"reportSelect\"]");
                }

                automationObject.genfunc_ClearText("//*[@id=\"vetReportFormDateFromField\"]");
                automationObject.genfunc_ClearText("//*[@id=\"vetReportFormDateToField\"]");

                automationObject.genfunc_SetText("//*[@id=\"reportSelect\"]", ReportName);
                automationObject.genfunc_SetText("//*[@id=\"vetReportFormDateFromField\"]", DateFrom);
                automationObject.genfunc_SetText("//*[@id=\"vetReportFormDateToField\"]", DateTo);

                switch (Organisation.toLowerCase()) {
                    case "add":
                        automationObject.genfunc_Click("//*[@id=\"addAllOrgsButton\"]");
                        break;
                    case "remove":
                        automationObject.genfunc_Click("//*[@id=\"removeAllOrgsButton\"]");
                        break;
                }

                automationObject.genfunc_Click("//*[@id=\"vetReportForm\"]/div[4]/div/button");

                if (Validation1.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"vetReportFormErrorBlock\"]/ul/li[1]", Validation1);
                }
                if (Validation2.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"vetReportFormErrorBlock\"]/ul/li[2]", Validation2);
                }
                if (Validation3.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"vetReportFormErrorBlock\"]/ul/li[3]", Validation3);
                }
                if (Validation4.equalsIgnoreCase("N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"vetReportFormErrorBlock\"]/ul/li[4]", Validation4);
                }
                if (Validation4.equalsIgnoreCase("N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"vetReportFormErrorBlock\"]/ul/li[5]", Validation5);
                }

            } catch (org.openqa.selenium.InvalidElementStateException q) {
            }

            i++;
        }

        vox_CogMenuSelect("Logout");

        return true;
    }

    public Boolean vox_Validate_RegisterUser() {
        String Username = (getDataSet("$registeruser(0,1)"));
        String Password = (getDataSet("$registeruser(0,2)"));

        vox_ForceLogin_User(Username, Password);

        int i = 0;
        while (i < Integer.parseInt(getDataSet("$rows(0,1"))) {

            String User = (getDataSet("$registeruser(" + i + ",3)"));
            String Firstname = (getDataSet("$registeruser(" + i + ",4)"));
            String Surname = (getDataSet("$registeruser(" + i + ",5)"));
            String CurrentPass = (getDataSet("$registeruser(" + i + ",6)"));
            String NewPass = (getDataSet("$registeruser(" + i + ",7)"));
            String ConfirmPass = (getDataSet("$registeruser(" + i + ",8)"));

            String Validation1 = (getDataSet("$registeruser(" + i + ",8)"));
            String Validation2 = (getDataSet("$registeruser(" + i + ",9)"));
            String Validation3 = (getDataSet("$registeruser(" + i + ",10)"));
            String Validation4 = (getDataSet("$registeruser(" + i + ",11)"));
            String Validation5 = (getDataSet("$registeruser(" + i + ",12)"));
            String Validation6 = (getDataSet("$registeruser(" + i + ",13)"));

            try {
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormUserName\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormFirstName\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormLastName\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormCurrentPassword\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormNewPassword\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormConfirmNewPassword\"]");

                automationObject.genfunc_SetText("//*[@id=\"changeUserFormUserName\"]", User);
                automationObject.genfunc_SetText("//*[@id=\"changeUserFormFirstName\"]", Firstname);
                automationObject.genfunc_SetText("//*[@id=\"changeUserFormLastName\"]", Surname);
                automationObject.genfunc_SetText("//*[@id=\"changeUserFormCurrentPassword\"]", CurrentPass);
                automationObject.genfunc_SetText("//*[@id=\"changeUserFormNewPassword\"]", NewPass);
                automationObject.genfunc_SetText("//*[@id=\"changeUserFormConfirmNewPassword\"]", ConfirmPass);

                automationObject.genfunc_Click("//*[@id=\"topButtonSave\"]");

                automationObject.genfunc_ExplicitWait("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[1]");

                if (Validation1.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[1]", Validation1);
                }
                if (Validation2.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[2]", Validation2);
                }
                if (Validation3.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[3]", Validation3);
                }
                if (Validation4.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[4]", Validation4);
                }
                if (Validation5.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[5]", Validation5);
                }
                if (Validation6.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[6]", Validation6);
                }
                if (automationObject.genfunc_isAlertBoxPresent() == true) {
                    automationObject.genfunc_ClickOKOnAlertBoxSkip();
                }
            } catch (org.openqa.selenium.InvalidElementStateException q) {
            }

            i++;
        }

        return true;
    }

    public Boolean vox_Validate_CreateUser() {
        String Username = (getDataSet("$createuser(0,1)"));
        String Password = (getDataSet("$createuser(0,2)"));

        vox_ForceLogin_User(Username, Password);

        String UserType = (getDataSet("$createuser(0,3)"));

        vox_CogMenuSelect(UserType);

        int i = 0;
        while (i < Integer.parseInt(getDataSet("$rows(0,1)"))) {
            String Email = (getDataSet("$createuser(" + i + ",4)"));
            String PAL = (getDataSet("$createuser(" + i + ",5)"));
            String Role = (getDataSet("$createuser(" + i + ",6)"));

            String Validation1 = (getDataSet("$createuser(" + i + ",7)"));
            String Validation2 = (getDataSet("$createuser(" + i + ",8)"));
            String Validation3 = (getDataSet("$createuser(" + i + ",9)"));

            try {

                automationObject.genfunc_ClearText("//*[@id=\"userFormEmail\"]");
                automationObject.genfunc_ClearText("//*[@id=\"userFormPaymentAuthorityLimit\"]");
                automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");
                if (automationObject.genfunc_isElementPresentChk("//*[@id=\"srole_0\"]") == true) {
                    automationObject.genfunc_Click("//*[@id=\"srole_0\"]");
                    automationObject.genfunc_Click("//*[@id=\"removeRolesButton\"]");
                }

                automationObject.genfunc_SetText("//*[@id=\"userFormEmail\"]", Email);
                automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", PAL);
                automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlV("//*[@id=\"userFormPaymentAuthorityLimit\"]");
                if (PAL.equalsIgnoreCase("#N/A") == true) {
                    automationObject.genfunc_ClearText("//*[@id=\"userFormPaymentAuthorityLimit\"]");
                }
                switch (Role.toLowerCase()) {
                    case "add":
                        automationObject.genfunc_Click("//*[@id=\"arole_0\"]");
                        automationObject.genfunc_Click("//*[@id=\"addRolesButton\"]");
                        break;
                    case "remove":
                        automationObject.genfunc_Click("//*[@id=\"srole_0\"]");
                        automationObject.genfunc_Click("//*[@id=\"removeRolesButton\"]");
                        break;
                }

                automationObject.genfunc_Click("//*[@id=\"saveButton\"]");

                if (Validation1.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"userFormErrorBlock\"]/ul/li[1]", Validation1);
                }
                if (Validation2.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"userFormErrorBlock\"]/ul/li[2]", Validation2);
                }
                if (Validation3.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"userFormErrorBlock\"]/ul/li[3]", Validation3);
                }
            } catch (org.openqa.selenium.InvalidElementStateException q) {
            }

            i++;
        }

        vox_CogMenuSelect("Logout");

        return true;
    }

    public Boolean vox_Validate_AccountDetails() {

        String Username = (getDataSet("$accountdetails(0,1)"));
        String Password = (getDataSet("$accountdetails(0,2)"));

        vox_ForceLogin_User(Username, Password);
        vox_CogMenuSelect("Account");

        automationObject.genfunc_Click("//*[@id=\"topButtonEdit\"]");
        int i = 0;

        while (i < Integer.parseInt(getDataSet("$rows(0,1)"))) {

            String User = (getDataSet("$accountdetails(" + i + ",3)"));
            String FirstName = (getDataSet("$accountdetails(" + i + ",4)"));
            String Surname = (getDataSet("$accountdetails(" + i + ",5)"));
            String Email = (getDataSet("$accountdetails(" + i + ",6)"));

            String Validation1 = (getDataSet("$accountdetails(" + i + ",7)"));
            String Validation2 = (getDataSet("$accountdetails(" + i + ",8)"));
            String Validation3 = (getDataSet("$accountdetails(" + i + ",9)"));
            String Validation4 = (getDataSet("$accountdetails(" + i + ",10)"));

            try {

                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormUserName\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormFirstName\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormLastName\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changeUserFormEmail\"]");

                automationObject.genfunc_SetText("//*[@id=\"changeUserFormUserName\"]", User);
                automationObject.genfunc_SetText("//*[@id=\"changeUserFormFirstName\"]", FirstName);
                automationObject.genfunc_SetText("//*[@id=\"changeUserFormLastName\"]", Surname);
                automationObject.genfunc_SetText("//*[@id=\"changeUserFormEmail\"]", Email);

                automationObject.genfunc_Click("//*[@id=\"topButtonSave\"]");

                automationObject.genfunc_ExplicitWait("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[1]");

                if (Validation1.equalsIgnoreCase("#N/A") == false) {

                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[1]", Validation1);
                }
                if (Validation2.equalsIgnoreCase("#N/A") == false) {

                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[2]", Validation2);
                }
                if (Validation3.equalsIgnoreCase("#N/A") == false) {

                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[3]", Validation3);
                }
                if (Validation4.equalsIgnoreCase("#N/A") == false) {

                    automationObject.genfunc_GetText("//*[@id=\"changeUserFormErrorBlock\"]/ul/li[4]", Validation4);
                }
            } catch (org.openqa.selenium.InvalidElementStateException q) {

            }

            i++;
        }

        vox_CogMenuSelect("Logout");

        return true;
    }

    public Boolean vox_Validate_PolicySearch() {
        String Username = (getDataSet("$policysearch(0,1)"));
        String Password = (getDataSet("$policysearch(0,2)"));

        vox_ForceLogin_User(Username, Password);
        vox_ClaimsMenuSelect("Search Policy");

        int i = 0;
        while (i < Integer.parseInt(getDataSet("$rows(0,1)"))) {
            String PolicyNo = (getDataSet("$policysearch(" + i + ",3)"));
            String Surname = (getDataSet("$policysearch(" + i + ",4)"));
            String DOB = (getDataSet("$policysearch(" + i + ",5)"));
            String Postcode = (getDataSet("$policysearch(" + i + ",6)"));

            String Validation1 = (getDataSet("$policysearch(" + i + ",7)"));
            String Validation2 = (getDataSet("$policysearch(" + i + ",8)"));
            String Validation3 = (getDataSet("$policysearch(" + i + ",9)"));
            String Validation4 = (getDataSet("$policysearch(" + i + ",10)"));
            try {
                automationObject.genfunc_ClearText("//*[@id=\"policySearchFormPolicyNumber\"]");
                automationObject.genfunc_ClearText("//*[@id=\"policySearchFormPolicyholderSurname\"]");
                automationObject.genfunc_ClearText("//*[@id=\"policySearchFormPolicyholderDob\"]");
                automationObject.genfunc_ClearText("//*[@id=\"policySearchFormPostcode\"]");

                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyNumber\"]", PolicyNo);
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyholderSurname\"]", Surname);
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPolicyholderDob\"]", DOB);
                automationObject.genfunc_SetText("//*[@id=\"policySearchFormPostcode\"]", Postcode);

                automationObject.genfunc_Click("//*[@id=\"policySearchForm\"]/div[4]/div[2]/button");

                if (Validation1.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"policySearchFormErrorBlock\"]/ul/li[1]", Validation1);
                }
                if (Validation2.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"policySearchFormErrorBlock\"]/ul/li[2]", Validation2);
                }
                if (Validation3.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"policySearchFormErrorBlock\"]/ul/li[3]", Validation3);
                }
                if (Validation4.equalsIgnoreCase("N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"policySearchFormErrorBlock\"]/ul/li[4]", Validation4);
                }

            } catch (org.openqa.selenium.InvalidElementStateException q) {
            }

            i++;
        }

        vox_CogMenuSelect("Logout");

        return true;
    }

    public Boolean vox_Validate_EditOrganisation() {
        String Username = (getDataSet("$editorganisation(0,1)"));
        String Password = (getDataSet("$editorganisation(0,2)"));
        String Organisation = (getDataSet("$editorganisation(0,3)"));

        vox_ForceLogin_User(Username, Password);
        vox_QuickSearchOrganisation(Organisation);
        vox_SelectResult();

        automationObject.genfunc_Click("//*[@id=\"editButton\"]");
        automationObject.toolSleep(1);

        int i = 0;
        while (i < Integer.parseInt(getDataSet("$rows(0,1)"))) {
            String PAL = (getDataSet("$editorganisation(" + i + ",4)"));
            String PTV = (getDataSet("$editorganisation(" + i + ",5)"));
            String PTP = (getDataSet("$editorganisation(" + i + ",6)"));
            String SortCode = (getDataSet("$editorganisation(" + i + ",7)"));
            String AccountNumber = (getDataSet("$editorganisation(" + i + ",8)"));
            String ConfirmPass = (getDataSet("$editorganisation(" + i + ",9)"));

            String Validation1 = (getDataSet("$editorganisation(" + i + ",10)"));
            String Validation2 = (getDataSet("$editorganisation(" + i + ",11)"));
            String Validation3 = (getDataSet("$editorganisation(" + i + ",12)"));
            String Validation4 = (getDataSet("$editorganisation(" + i + ",13)"));
            String Validation5 = (getDataSet("$editorganisation(" + i + ",14)"));
            String Validation6 = (getDataSet("$editorganisation(" + i + ",15)"));
            try {
                automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentAuthorisationLimit\"]");
                automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentToleranceValue\"]");
                automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentTolerancePcnt\"]");
                automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormBankSortCode\"]");
                automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormBankAccountNumber\"]");
                automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPassword\"]");

                automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", PAL);
                automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlV("//*[@id=\"voxDetailsFormPaymentAuthorisationLimit\"]");

                automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", PTV);
                automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlV("//*[@id=\"voxDetailsFormPaymentToleranceValue\"]");

                automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", PTP);
                automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlV("//*[@id=\"voxDetailsFormPaymentTolerancePcnt\"]");

                automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", SortCode);
                automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlV("//*[@id=\"voxDetailsFormBankSortCode\"]");

                automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", AccountNumber);
                automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlV("//*[@id=\"voxDetailsFormBankAccountNumber\"]");

                automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", ConfirmPass);
                automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlV("//*[@id=\"voxDetailsFormPassword\"]");

                if (PAL.equalsIgnoreCase("#N/A") == true) {
                    automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentAuthorisationLimit\"]");
                }
                if (PTV.equalsIgnoreCase("#N/A") == true) {
                    automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentToleranceValue\"]");
                }
                if (PTP.equalsIgnoreCase("#N/A") == true) {
                    automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPaymentTolerancePcnt\"]");
                }
                if (SortCode.equalsIgnoreCase("#N/A") == true) {
                    automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormBankSortCode\"]");
                }
                if (AccountNumber.equalsIgnoreCase("#N/A") == true) {
                    automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormBankAccountNumber\"]");
                }
                if (ConfirmPass.equalsIgnoreCase("#N/A") == true) {
                    automationObject.genfunc_ClearText("//*[@id=\"voxDetailsFormPassword\"]");
                }

                automationObject.genfunc_Click("//*[@id=\"saveButton\"]");

                automationObject.toolSleep(2);

                if (Validation1.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"voxDetailsFormErrorBlock\"]/ul/li[1]", Validation1);
                }
                if (Validation2.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"voxDetailsFormErrorBlock\"]/ul/li[2]", Validation2);
                }
                if (Validation3.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"voxDetailsFormErrorBlock\"]/ul/li[3]", Validation3);
                }
                if (Validation4.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"voxDetailsFormErrorBlock\"]/ul/li[4]", Validation4);
                }
                if (Validation5.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"voxDetailsFormErrorBlock\"]/ul/li[5]", Validation5);
                }
                if (Validation6.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"voxDetailsFormErrorBlock\"]/ul/li[6]", Validation6);
                }
            } catch (org.openqa.selenium.InvalidElementStateException q) {
            }
            i++;

        }
        vox_CogMenuSelect("Logout");
        return true;
    }

    //TODO
    public Boolean vox_Validate_RateCard() {
        String Username = (getDataSet(""));
        return true;
    }

    public Boolean vox_Validate_PasswordDetails() {
        String Username = (getDataSet("$passworddetails(0,1)"));
        String Password = (getDataSet("$passworddetails(0,2)"));

        vox_ForceLogin_User(Username, Password);
        vox_CogMenuSelect("Account");

        int i = 0;
        while (i < Integer.parseInt(getDataSet("$rows(0,1)"))) {
            String Current = (getDataSet("$passworddetails(" + i + ",3)"));
            String New = (getDataSet("$passworddetails(" + i + ",4)"));
            String Confirm = (getDataSet("$passworddetails(" + i + ",5)"));

            String Validation1 = (getDataSet("$passworddetails(" + i + ",6)"));
            String Validation2 = (getDataSet("$passworddetails(" + i + ",7)"));
            String Validation3 = (getDataSet("$passworddetails(" + i + ",8)"));
            try {

                automationObject.genfunc_ClearText("//*[@id=\"changePwdFormCurrentPassword\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changePwdFormNewPassword\"]");
                automationObject.genfunc_ClearText("//*[@id=\"changePwdFormConfirmNewPass\"]");

                automationObject.genfunc_SetText("//*[@id=\"changePwdFormCurrentPassword\"]", Current);
                automationObject.genfunc_SetText("//*[@id=\"changePwdFormNewPassword\"]", New);
                automationObject.genfunc_SetText("//*[@id=\"changePwdFormConfirmNewPass\"]", Confirm);

                automationObject.genfunc_Click("//*[@id=\"bottomButtonSave\"]");

                automationObject.genfunc_ExplicitWait("//*[@id=\"changePwdFormErrorBlock\"]/ul/li[1]");

                if (Validation1.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changePwdFormErrorBlock\"]/ul/li[1]", Validation1);
                }
                if (Validation2.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changePwdFormErrorBlock\"]/ul/li[2]", Validation2);
                }
                if (Validation3.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"changePwdFormErrorBlock\"]/ul/li[3]", Validation3);
                }
            } catch (org.openqa.selenium.InvalidElementStateException q) {
            }

            i++;
        }

        vox_CogMenuSelect("Logout");

        return true;
    }

    public Boolean vox_Validate_EditUser() {

        String Username = (getDataSet("$edituser(0,1)"));
        String Password = (getDataSet("$edituser(0,2)"));
        String UserSearch = (getDataSet("$edituser(0,3)"));

        vox_ForceLogin_User(Username, Password);
        vox_QuickSearchUser(UserSearch);
        automationObject.toolSleep(2);
        vox_SelectResult();

        automationObject.genfunc_Click("//*[@id=\"topButtonEdit\"]");
        automationObject.toolSleep(1);

        int i = 0;
        while (i < Integer.parseInt(getDataSet("$rows(0,1)"))) {
            String PAL = (getDataSet("$edituser(" + i + ",4)"));
            String Role = (getDataSet("$edituser(" + i + ",5)"));

            String Validation1 = (getDataSet("$edituser(" + i + ",6)"));
            String Validation2 = (getDataSet("$edituser(" + i + ",7)"));

            try {
                automationObject.genfunc_ClearText("//*[@id=\"userFormPaymentAuthorityLimit\"]");
                automationObject.genfunc_ClearText("//*[@id=\"quickSearchKeyword\"]");

                automationObject.toolSleep(1);
                automationObject.genfunc_SetText("//*[@id=\"quickSearchKeyword\"]", PAL);
                automationObject.genfunc_SendControlA("//*[@id=\"quickSearchKeyword\"]");
                automationObject.genfunc_SendControlC("//*[@id=\"quickSearchKeyword\"]");

                automationObject.genfunc_SendControlV("//*[@id=\"userFormPaymentAuthorityLimit\"]");

                switch (Role.toLowerCase()) {
                    case "add":
                        automationObject.genfunc_Click("//*[@id=\"arole_0\"]");
                        automationObject.genfunc_Click("//*[@id=\"addRolesButton\"]");
                        break;
                    case "remove":
                        automationObject.genfunc_Click("//*[@id=\"srole_0\"]");
                        automationObject.genfunc_Click("//*[@id=\"removeRolesButton\"]");
                        break;
                    case "do nothing":
                        automationObject.genfunc_Click("//*[@id=\"removeRolesButton\"]");
                        break;
                }

                if (PAL.equalsIgnoreCase("#N/A") == true) {
                    automationObject.genfunc_ClearText("//*[@id=\"userFormPaymentAuthorityLimit\"]");
                }
                automationObject.genfunc_Click("//*[@id=\"topButtonSave\"]");

                if (Validation1.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"userFormErrorBlock\"]/ul/li[1]", Validation1);
                }
                if (Validation2.equalsIgnoreCase("#N/A") == false) {
                    automationObject.genfunc_GetText("//*[@id=\"userFormErrorBlock\"]/ul/li[2]", Validation2);
                }

            } catch (org.openqa.selenium.InvalidElementStateException q) {
            }
            i++;
        }
        vox_CogMenuSelect("Logout");

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //VOX Scenario - Generic Methods
    ////////////////////////////////////////////////////////////////////////////////
    public Boolean vox_Scenario_Login(String strUsername, String strPassword, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_CheckValidation(XPath, Validation);
        return true;
    }

    public Boolean vox_Scenario_ForgottenUsername(String strEmail, String Validation, String XPath) {
        vox_ForgottenUsername(strEmail);
        vox_CheckValidation(XPath, Validation);
        automationObject.genfunc_Click("//*[@id=\"login\"]");
        return true;
    }

    public Boolean vox_Scenario_ForgottenPassword(String strEmail, String Validation, String XPath) {
        vox_ForgottenPassword(strEmail);
        vox_CheckValidation(XPath, Validation);
        automationObject.genfunc_Click("//*[@id=\"login\"]");
        return true;
    }

    public Boolean vox_Scenario_VetMessages(String strUsername, String strPassword, String strMessage, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_CogMenuSelect("vet messages");
        //Validation
        vox_CreateVetMessage(strMessage);
        vox_CheckValidation(XPath, Validation);
        if (automationObject.genfunc_isElementPresentChk("//*[@id=\"editMessageBox\"]/button") == true) {
            automationObject.genfunc_Click("//*[@id=\"editMessageBox\"]/button");
        }
        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_RegisterUser(String strUsername, String strPassword, String strUser, String strFirstname, String strSurname, String currentPassword, String newPassword, String confirmPassword, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_RegisterUser(strUser, strFirstname, strSurname, currentPassword, newPassword, confirmPassword);
        vox_CheckValidation(XPath, Validation);
        return true;
    }

    public Boolean vox_Scenario_CreateUser(String strUsername, String strPassword, String strMenu, String strEmail,
            String strPal, String selectRole, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_CogMenuSelect(strMenu);
        //Validation
        vox_CreateUser(strEmail, strPal, selectRole);
        vox_CheckValidation(XPath, Validation);
        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_Reporting(String strUsername, String strPassword, String strReportName, String strDateFrom, String strDateTo, String selectOrg, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_CogMenuSelect("reporting");
        //Validation
        vox_GenerateReport(strReportName, strDateFrom, strDateTo, selectOrg);
        vox_CheckValidation(XPath, Validation);
        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_Search(String strUsername, String strPassword, String selectSearch, String strUser, String strStatus, String strOrg, String strResult) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_CogMenuSelect(selectSearch);
        //Validation User Search
        switch (selectSearch.toLowerCase()) {
            case "search user":
            case "user":
                vox_SearchUser(strUser, strStatus, strOrg);
                vox_CheckResults(strResult);
                break;
            case "search organisation":
            case "organisation":
                vox_SearchOrganisation(strOrg);
                vox_CheckResults(strResult);
                break;
        }
        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_PolicySearch(String strUsername, String strPassword, String strPolicyNumber, String strSurname, String strDOB, String strPostcode, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_ClaimsMenuSelect("search policy");
        //Validation
        vox_PolicySearch(strPolicyNumber, strSurname, strDOB, strPostcode);
        vox_CheckValidation(XPath, Validation);
        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_PolicyConsent(String strUsername, String strPassword, String strPolicyNumber, String strCustomerType, String strCustomerName, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_ClaimsMenuSelect("search policy");
        vox_PolicySearch(strPolicyNumber, "", "", "");
        vox_SelectResult();
        vox_ConsentForm(strCustomerType, strCustomerName);
        vox_CheckValidation(XPath, Validation);
        if (automationObject.genfunc_isElementPresentChk(XPath) == true) {
            automationObject.genfunc_Click("//*[@id=\"voxInfoCloseButton\"]");
        }
        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_AccountDetails(String strUsername, String strPassword, String strUser,
            String strFirstname, String strSurname, String strEmail, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_CogMenuSelect("account");
        //Validation
        vox_ChangeUserDetails(strUser, strFirstname, strSurname, strEmail);
        vox_CheckValidation(XPath, Validation);
        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_PasswordDetails(String strUsername, String strPassword, String currentPass, String newPass, String confirmPass, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_CogMenuSelect("account");
        //Validation
        vox_ChangeUserPassword(currentPass, newPass, confirmPass);
        vox_CheckValidation(XPath, Validation);
        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_ViewPreAuth(String strUsername, String strPassword, String State, String filterType, String dateFrom, String dateTo,
            String searchText, String condDropDown, String orgDropDown, String statusDropDown, String Validation) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_ClaimsMenuSelect("" + State + " " + "preauth");
        automationObject.genfunc_ExplicitWait("//*[@id=\"searchResultsTitle\"]");
        automationObject.genfunc_SetText("//*[@id=\"preFilter\"]", filterType);

        switch (filterType.toLowerCase()) {

            case "organisation":
                automationObject.genfunc_SetText("//*[@id=\"orgDropDown\"]", orgDropDown);
                break;
            case "condition name":
                automationObject.genfunc_SetText("//*[@id=\"condDropDown\"]", condDropDown);
                break;
            case "status":
                automationObject.genfunc_SetText("//*[@id=\"statusDropDown\"]", statusDropDown);
                break;
            case "created":
            case "updated":
            case "first signs":
            case "treatment from":
            case "treatment to":
                automationObject.genfunc_SetText("//*[@id=\"preauth" + State + "FormDateFromField\"]", dateFrom);
                automationObject.genfunc_SetText("//*[@id=\"preauth" + State + "FormDateToField\"]", dateTo);
                break;
            case "preauth ref":
            case "policy":
            case "policyholder":
            case "pet":
                automationObject.genfunc_SetText("//*[@id=\"preauth" + State + "FormKeyword\"]", searchText);
                break;
            default:
                automationObject.genfunc_SetText("//*[@id=\"preauth" + State + "FormKeyword\"]", "No Filter");
                break;
        }
        automationObject.genfunc_Click("//*[@id=\"preauthReviewSearchButton\"]");
        automationObject.genfunc_GetText("//*[@id=\"preauth" + State + "FormErrorBlock\"]/ul/li", Validation);
        vox_CogMenuSelect("logout");

        return true;
    }

    public Boolean vox_Scenario_ViewClaimForm(String strUsername, String strPassword, String State, String filterType, String dateFrom, String dateTo,
            String searchText, String condDropDown, String orgDropDown, String statusDropDown, String Validation) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_ClaimsMenuSelect("" + State + " " + "claims");
        automationObject.genfunc_ExplicitWait("//*[@id=\"headerpolicyName\"]");
        automationObject.genfunc_SetText("//*[@id=\"preFilter\"]", filterType);

        switch (filterType.toLowerCase()) {
            case "organisation":
                automationObject.genfunc_SetText("//*[@id=\"orgDropDown\"]", orgDropDown);
                break;
            case "condition name":
                automationObject.genfunc_SetText("//*[@id=\"condDropDown\"]", condDropDown);
                break;
            case "status":
                automationObject.genfunc_SetText("//*[@id=\"statusDropDown\"]", statusDropDown);
                break;
            case "submitted":
            case "created":
            case "updated":
            case "first signs":
            case "treatment from":
            case "treatment to":
                automationObject.genfunc_SetText("//*[@id=\"claims" + State + "FormDateFromField\"]", dateFrom);
                automationObject.genfunc_SetText("//*[@id=\"claims" + State + "FormDateToField\"]", dateTo);
                break;
            case "claim form ref":
            case "preauth ref":
            case "payment ref":
            case "policy":
            case "policyholder":
            case "pet":
                automationObject.genfunc_SetText("//*[@id=\"claims" + State + "FormKeyword\"]", searchText);
                break;
            default:
                automationObject.genfunc_SetText("//*[@id=\"claims" + State + "FormKeyword\"]", "No Filter");
                break;
        }
        automationObject.genfunc_Click("//*[@id=\"claimFormSearchButton\"]");
        automationObject.genfunc_GetText("//*[@id=\"claims" + State + "FormErrorBlock\"]/ul/li", Validation);
        vox_CogMenuSelect("logout");

        return true;
    }

    public Boolean vox_Scenario_EditOrganisation(String strUsername, String strPassword, String strOrg) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_QuickSearchOrganisation(strOrg);
        //*[@id="resultsTbody"]/tr/td[1]/a

        vox_CogMenuSelect("logout");
        return true;
    }

    public Boolean vox_Scenario_EditUser(String strUsername, String strPassword, String strUser, String strPAL, String selectRole, String Validation, String XPath) {
        vox_ForceLogin_User(strUsername, strPassword);
        vox_QuickSearchUser(strUser);
        vox_SelectResult();
        vox_EditUser(strPAL, selectRole);
        vox_CheckValidation(XPath, Validation);
        automationObject.genfunc_ClickOKOnAlertBox();
        vox_CogMenuSelect("logout");
        return true;
    }

/////////////////////////////////////////////////////////////////////////////////
// iPMX Business Methods.
/////////////////////////////////////////////////////////////////////////////////
    public Boolean ipmx_BusinessMethod_Login(String strUsername, String strPassword) {
        automationObject.genfunc_SetText("name=j_username", strUsername);
        automationObject.genfunc_SetText("name=j_password", strPassword);
        automationObject.genfunc_Click("name=submit");
        automationObject.toolSleep(30);

        return true;
    }

    public Boolean ipmx_BusinessMethod_Logout() {
        automationObject.toolSleep(30);
        if (automationObject.genfunc_IsObjectVisible("link=Sign Out") == false) {
            return false;
        }

        automationObject.genfunc_ClickLink("Sign Out");
        automationObject.genfunc_IsObjectVisible("id=loginPanel");

        return true;
    }

    public Boolean ipmx_BusinessMethod_AddUser(String strFirstName, String strLastName,
            String strEmail, int iProfile, int iStatus, String strPassword) {
        automationObject.genfunc_SetText("id=ext-comp-1036", strFirstName);
        automationObject.genfunc_SetText("id=ext-comp-1037", strLastName);
        automationObject.genfunc_SetText("id=ext-comp-1038", strEmail);
        automationObject.genfunc_SetComboboxValueByText("name=profile", "Accounts User");//"id=ext-gen173");
        automationObject.genfunc_SetComboboxValueByText("name=status", "Active");//"id=ext-gen178");
        automationObject.genfunc_SetText("id=ext-comp-1041", strPassword);
        automationObject.genfunc_SetText("id=ext-comp-1042", strPassword);

        // selenium.chooseOkOnNextConfirmation();
        automationObject.genfunc_Click("id=ext-gen191");

        automationObject.toolSleep(15);

        automationObject.genfunc_Click("//button[contains(@class, 'x-btn-text')][text()='OK'");

        return true;
    }

    public Boolean ipmx_BusinessMethod_SearchRepairerInvoice(String strInvoiceNo,
            String strAssessmentNo, String strClaimNo, String strUniqueInvoiceID) {
        automationObject.genfunc_ClickLink("Home");
        automationObject.toolSleep(10);

        automationObject.genfunc_ClickLink("UKAARC Repairer Invoices");
        automationObject.toolSleep(10);

        automationObject.genfunc_Click("id=ext-gen174");
        automationObject.toolSleep(10);

        automationObject.genfunc_SetText("name=invoiceNumber", strInvoiceNo);
        automationObject.genfunc_SetText("name=assessmentNumber", strAssessmentNo);
        automationObject.genfunc_SetText("name=claimReference", strClaimNo);
        automationObject.genfunc_SetText("name=uniqueInvoiceId", strUniqueInvoiceID);

        //    genfunc_SetComboboxValueByText("name=party", "Third");
        //    genfunc_SetComboboxValueByText("name=useremail", "l@ipmx.co.uk");
        automationObject.genfunc_Click("id=ext-gen118");
        automationObject.toolSleep(30);

        //genfunc_SelectTableRow("id=idGridRepairerInvoices", "0");
        //genfunc_SelectTableRow("id=ext-gen129", "0");
        //automationObject_Sleep(10);
        return true;
    }

    public Boolean ipmx_BusinessMethod_LoadRepairerInvoice(String strInvoiceNo,
            String strAssessmentNo, String strClaimNo, String strUniqueInvoiceID, String strRowNumber) {
        ipmx_BusinessMethod_SearchRepairerInvoice(strInvoiceNo, strAssessmentNo, strClaimNo, strUniqueInvoiceID);
        automationObject.genfunc_DoubleClickTableRow("id=idGridRepairerInvoices", strRowNumber);
        return true;
    }

    public Boolean ipmx_BusinessMethod_ApproveRepairerInvoice(String strInvoiceNo,
            String strAssessmentNo, String strClaimNo, String strUniqueInvoiceID, String strRowNumber) {
        ipmx_BusinessMethod_SearchRepairerInvoice(strInvoiceNo, strAssessmentNo, strClaimNo, strUniqueInvoiceID);
        automationObject.genfunc_DoubleClickTableRow("id=idGridRepairerInvoices", strRowNumber);
        automationObject.toolSleep(30);
//        genfunc_Click("id=ext-gen232");

        automationObject.genfunc_Click("//button[contains(@class, 'x-btn-text authorise-button')]");
        //genfunc_Click("//button[contains(@class, 'x-btn-text')][text()='OK'");     
        return true;
    }

    public Boolean ipmx_BusinessMethod_RejectRepairerInvoice(String strInvoiceNo,
            String strAssessmentNo, String strClaimNo, String strUniqueInvoiceID, String strRowNumber) {
        ipmx_BusinessMethod_LoadRepairerInvoice(strInvoiceNo, strAssessmentNo, strClaimNo, strUniqueInvoiceID, strRowNumber);
        automationObject.genfunc_DoubleClickTableRow("id=idGridRepairerInvoices", strRowNumber);
        automationObject.toolSleep(30);
        automationObject.genfunc_Click("id=ext-gen230");

        return true;
    }

    public Boolean ipmx_BusinessMethod_RevalidateRepairerInvoice(String strInvoiceNo,
            String strAssessmentNo, String strClaimNo, String strUniqueInvoiceID, String strRowNumber) {
        ipmx_BusinessMethod_LoadRepairerInvoice(strInvoiceNo, strAssessmentNo, strClaimNo, strUniqueInvoiceID, strRowNumber);
        automationObject.genfunc_DoubleClickTableRow("id=ext-gen123", "0");
        automationObject.toolSleep(30);
        automationObject.genfunc_Click("id=ext-gen228");

        return true;
    }
/////////////////////////////////////////////////////////////////////////////////
// MROX Business Methods.
/////////////////////////////////////////////////////////////////////////////////

    public Boolean mrox_BusinessMethod_Login(String strUsername, String strPassword) {
        automationObject.genfunc_SetText("name=j_username", strUsername);
        automationObject.genfunc_SetText("name=j_password", strPassword);
        automationObject.genfunc_Click("name=commit");
        automationObject.toolSleep(30);

        try {
            Thread.sleep(1000); // do nothing for 1000 miliseconds (1 second)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    public Boolean mrox_BusinessMethod_RFPSearch(String strRFPStatus_Value, String strBREResult_Value, String strMRORef_Value) {
        automationObject.genfunc_Click("id=ext-gen26");

        automationObject.toolSleep(30);

        automationObject.genfunc_SetComboboxValueByText("name=status", strRFPStatus_Value);
        automationObject.genfunc_SetComboboxValueByText("name=breResult", strBREResult_Value);

        automationObject.genfunc_SetText("name=mroRef", strMRORef_Value);

        automationObject.genfunc_Click("id=ext-gen51");

        automationObject.toolSleep(30);

        automationObject.genfunc_Click("id=ext-gen85");

        return true;
    }

/////////////////////////////////////////////////////////////////////////////////
// CHOX Business Methods.
/////////////////////////////////////////////////////////////////////////////////
    public Boolean chox_BusinessMethod_Login(String strUsername, String strPassword) {
//        genfunc_Click("value=Go");
//        genfunc_Selenium_Wait("30000");
        automationObject.genfunc_SetText("id=loginUserNameId", strUsername);
        automationObject.genfunc_SetText("id=loginPasswordId", strPassword);
        automationObject.genfunc_Click("id=loginSubmitButtonId");
        automationObject.toolSleep(30);

        return true;
    }

    public Boolean chox_BusinessMethod_Search(String strLiabilityStatus_Value, String strStatus_Value) {
//        genfunc_ClickTab("ext-comp-1029__searchTabId");
        automationObject.genfunc_ClickTab("id=ext-comp-1029__ext-comp-1030");
        automationObject.toolSleep(5);

        automationObject.genfunc_SetComboboxValueByText("name=liabilityStatusSearchScreenComboId", strLiabilityStatus_Value);
        automationObject.genfunc_SetComboboxValueByText("name=statusSearchScreenComboId", strStatus_Value);

        automationObject.genfunc_Click("id=ext-gen203");

        automationObject.toolSleep(30);

        automationObject.genfunc_ClickLink("27885194");

        return true;
    }
/////////////////////////////////////////////////////////////////////////////////
// iPMX Test Scenarios.
/////////////////////////////////////////////////////////////////////////////////

// Test Scenario 1 - Login.     
    public Boolean ipmx_TestScenario_1(String strUsername, String strPassword) {
        return ipmx_BusinessMethod_Login(strUsername, strPassword);
    }
// Test Scenario 2 - Login, goto User Management, create a new User.    

    public Boolean ipmx_TestScenario_2(String strUsername, String strPassword, String strFirstName, String strLastName,
            String strEmail, int iProfile, int iStatus, String strNewPassword) {
        ipmx_BusinessMethod_Login(strUsername, strPassword);

        automationObject.genfunc_ClickLink("Manage Users");
        automationObject.genfunc_Click("id=idUserAddBtn");
//        genfunc_Selenium_Wait("30000");
        ipmx_BusinessMethod_AddUser(strFirstName, strLastName, strEmail, iProfile, iStatus, strNewPassword);

        ipmx_BusinessMethod_Logout();

        return true;
    }
// Test Scenario 3 - Login    

    public Boolean ipmx_TestScenario_3(String strUsername, String strPassword, String strHyperlink, String strPartyValue, String strStatusValue) {
        ipmx_BusinessMethod_Login(strUsername, strPassword);

        automationObject.genfunc_ClickLink(strHyperlink);
        automationObject.genfunc_Click("id=ext-gen176");

        automationObject.genfunc_SetComboboxValueByText("name=party", strPartyValue);
        automationObject.genfunc_SetComboboxValueByText("name=status", strStatusValue);

        automationObject.genfunc_Click("id=ext-gen120");

        return true;
    }
/////////////////////////////////////////////////////////////////////////////////
// MROX Test Scenarios.
/////////////////////////////////////////////////////////////////////////////////

// Test Scenario 1 - Login.     
    public Boolean mrox_TestScenario_1(String strUsername, String strPassword, String strRFPStatus_Value, String strBREResult_Value, String strMRORef_Value) {
        mrox_BusinessMethod_Login(strUsername, strPassword);
        mrox_BusinessMethod_RFPSearch(strRFPStatus_Value, strBREResult_Value, strMRORef_Value);

        return true;
    }

/////////////////////////////////////////////////////////////////////////////////
// CHOX Test Scenarios.
/////////////////////////////////////////////////////////////////////////////////
// Test Scenario 1 - Login.     
    public Boolean chox_TestScenario_1(String strUsername, String strPassword) {
        chox_BusinessMethod_Login(strUsername, strPassword);

        return true;
    }

    public Boolean chox_TestScenario_2(String strLiabilityStatus_Value, String strStatus_Value) {
        automationObject.toolSleep(10);
        chox_BusinessMethod_Search(strLiabilityStatus_Value, strStatus_Value);

        return true;
    }

/////////////////////////////////////////////////////////////////////////////////
// Subrogation Methods
/////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////
    ////            General Methods
    ///////////////////////////////////
    public Boolean sp_Login(String strUsername, String strPassword) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("/html/body/div/div/div/div/div/div[2]/div/button");
        automationObject.genfunc_waitForAngular();

        //AudaConnect Authentication
//        if (strAudaConnect.equalsIgnoreCase("Yes") || strAudaConnect.equalsIgnoreCase("Y")) {
//            automationObject.genfunc_Click("/html/body/div/div[2]/div/button");
//            automationObject.genfunc_waitForAngular();
//            //hardcoded details
//            automationObject.genfunc_SetText("//*[@id=\"CompanyCode\"]", "Company Code");
//            automationObject.genfunc_SetText("//*[@id=\"UserName\"]", strUsername);
//            automationObject.genfunc_SetText("//*[@id=\"Password\"]", strPassword);
//        } else {
        //Valexa Authentication
        automationObject.genfunc_SetText("//*[@id=\"username\"]", strUsername);
        automationObject.genfunc_SetText("//*[@id=\"password\"]", strPassword);
        automationObject.genfunc_Click("/html/body/div/div[3]/form/button");
//        }
        //Subrogation Login
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementPresent("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span") == false) {
            return false;
        }
        return true;
    }

    public Boolean sp_Logout() {
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementPresent("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span")) {
            //wait for logout button to be clickable - sometimes toast notification blocks it
            automationObject.genfunc_waitForPageToLoad("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span");
            if (automationObject.genfunc_isElementPresent("//*[@id=\"toast-container\"]")) {
                automationObject.genfunc_waitForPageToLoad("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span");
            }
            automationObject.genfunc_Click("/html/body/div/div/nav/div/div[2]/ul/li[5]/a/span");
            automationObject.genfunc_waitForAngular();
            return true;
        }
        System.out.println("ERROR - Logout button is not present.");
        return false;
    }

    public Boolean sp_SelectMenuQueue(String strQueueTab, String strQueue) {

        automationObject.genfunc_waitForAngular();
        if (strQueueTab.equalsIgnoreCase("Claimant")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Defendant")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Tasks")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Tasks\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Search")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Search\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Netting")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Netting\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Upload")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Upload\")]");
        }
        if (strQueueTab.equalsIgnoreCase("User Admin")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"User Admin\")]");
        }
        if (strQueueTab.equalsIgnoreCase("Organisation Admin")) {
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"OrganisationAdmin\")]");
        }
        automationObject.genfunc_waitForAngular();
        if (strQueueTab.equalsIgnoreCase("Claimant") || strQueueTab.equalsIgnoreCase("Defendant")) {
            automationObject.genfunc_Click("//*[@id=\"queuesId\"]/div[contains(.,\"" + strQueue + "\")]/div[1]/div/div/div[2]/div[1]/div");
            automationObject.genfunc_waitForAngular();
        }
        return true;
    }

    private Boolean sp_SelectTablePage(Integer iPage) {
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementSelected("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]") == false) {
            automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]");
        }
        return true;
    }

    public Boolean sp_SelectClaim(String strClaimantClaimNumber) {

        int iPage = 1;
        //select the dropdown to display 100 results per page
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/label/select/option[contains(.,\"100\")]");
        automationObject.genfunc_waitForAngular();
        //find the claimant claim number column
        int iCol;
        for (iCol = 1; iCol < 10; iCol++) {
            if (automationObject.genfunc_GetTextNoOutput("//*[starts-with(@id,\"DataTables_Table\")]/div[2]/div[1]/div/table/thead/tr/th[" + iCol + "]", "Claimant Claim Number") == true) {
                //Claimant claim number column has been found. Exit loop
                break;
            }
        }
        //Cycle through table pages
        while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
            sp_SelectTablePage(iPage);
            automationObject.genfunc_waitForAngular();
            //Search the column for the specific claim number
            if (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strClaimantClaimNumber + "\")]/td[" + iCol + "]") == true) {
                //Open the claim
                automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strClaimantClaimNumber + "\")]/td[1]/i");
                return true;
            }
            iPage = iPage + 1;
        }
        System.out.println("ERROR - Could not select claim '" + strClaimantClaimNumber + "'.");
        return false;
    }

    public Boolean sp_IsClaimPresent(String strClaimantClaimNumber, String strExpectedResult) {
        int iPage = 1;
        //select the dropdown to display 100 results per page
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/label/select/option[contains(.,\"100\")]");
        automationObject.genfunc_waitForAngular();
        //find the claimant claim number column
        int iCol;
        for (iCol = 1; iCol < 10; iCol++) {
            if (automationObject.genfunc_GetTextNoOutput("//*[starts-with(@id,\"DataTables_Table\")]/div[2]/div[1]/div/table/thead/tr/th[" + iCol + "]", "Claimant Claim Number") == true) {
                //Claimant claim number column has been found. Exit loop
                break;
            }
        }
        //Cycle through table pages
        while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
            sp_SelectTablePage(iPage);
            automationObject.genfunc_waitForAngular();
            if (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strClaimantClaimNumber + "\")]/td[" + iCol + "]") == true) {
                //Output a statement when a claim has been found matching the specified claim number
                if (strExpectedResult.equalsIgnoreCase("No")) {
                    System.out.println("Failed - Claim '" + strClaimantClaimNumber + "' is present.");
                    return false;
                }
                return true;
            }
            iPage = iPage + 1;
        }
        //Output a statement when no claim has been found matching the specified claim number
        if (strExpectedResult.equalsIgnoreCase("No")) {
            return true;
        }
        System.out.println("Failed - Claim '" + strClaimantClaimNumber + "' is NOT present.");
        return false;
    }

    public Boolean sp_IsClaimNotPresent(String strClaimantClaimNumber, String strActingAs) {
        //Checks claim is not present in all queues

        ArrayList<String> alQueues = new ArrayList<>();
        if (strActingAs.equalsIgnoreCase("Claimant")) {
            //Claimant Queues
            alQueues.add("Draft Claims");
            alQueues.add("Assign Workgroup");
            alQueues.add("Assign Owner");
            alQueues.add("Rejected Claims");
            alQueues.add("Awaiting Invoice");
            alQueues.add("Draft Invoices");
            alQueues.add("Queried Invoices");
            alQueues.add("Paid Invoices");
            //Navigate to Claimant Queues
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]");
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
        } else if (strActingAs.equalsIgnoreCase("Defendant")) {
            //Defendant Queues
            alQueues.add("Assign Workgroup");
            alQueues.add("Assign Owner");
            alQueues.add("Claims to be Registered");
            alQueues.add("Awaiting Acknowledgement");
            alQueues.add("Pending Quantum");
            alQueues.add("Pending Liability");
            alQueues.add("Pending Payment");
            //Navigate to Defendant Queues
            automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]");
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
        } else {
            return false;
        }
        automationObject.genfunc_waitForAngular();
        //Cycle through queues to check the specified claim is not present
        for (String strQueue : alQueues) {
            if (automationObject.genfunc_isElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"" + strQueue + "\")]/div[1]/div/div/div[2]/div[1]/div") == true) {
                automationObject.genfunc_Click("//*[@id=\"queuesId\"]/div[contains(.,\"" + strQueue + "\")]/div[1]/div/div/div[2]/div[1]/div");
                automationObject.genfunc_waitForAngular();
                //If claim is present return false
                if (sp_IsClaimPresent(strClaimantClaimNumber, "No") == false) {
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean sp_Data_CreateClaim(String strQueue, String ClaimNo, String DefendantInsurer) {

        try {
            sp_SelectMenuQueue("Claimant", "Draft Claims");
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_SetText("//*[@id=\"claimantClaimNumber\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"claimantPolicyNumber\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"claimantFirstName\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"claimantLastName\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"defendantInsurer\"]", DefendantInsurer);
            automationObject.genfunc_SetText("//*[@id=\"defendantClaimNumber\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"defendantPolicyNumber\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"defendantFirstName\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"defendantLastName\"]", ClaimNo);
            automationObject.genfunc_SetText("//*[@id=\"incidentLocation\"]", "Location");
            automationObject.genfunc_SetText("//*[@id=\"incidentDescription\"]", "Damage");
            automationObject.genfunc_SetText("//*[@id=\"damageDescription\"]", "Description");
            automationObject.genfunc_SetText("//*[@id=\"dateOfIncident\"]/div/input", "01/06/2010");
            automationObject.genfunc_SetText("//*[@id=\"totalLoss\"]", "No");
            automationObject.genfunc_SetText("//*[@id=\"carUsable\"]", "Yes");

            //Saves or Submits the claim
            if (strQueue.equalsIgnoreCase("Draft Claims") == true) {
                automationObject.genfunc_Click("//button[contains(.,'Save Claim')]");
            } else {
                automationObject.genfunc_Click("//button[contains(.,'Submit Claim')]");
            }
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
        } catch (org.openqa.selenium.InvalidElementStateException p) {
            System.out.println("ERROR - " + ClaimNo + " failed to be created.");
            automationObject.genfunc_RefreshPage();
            automationObject.genfunc_waitForAngular();
            return false;
        } catch (org.openqa.selenium.WebDriverException q) {
            System.out.println("ERROR - " + ClaimNo + " failed to be created.");
            automationObject.genfunc_RefreshPage();
            automationObject.genfunc_waitForAngular();
            return false;
        }
        System.out.println("    " + ClaimNo + " Claim has been created.");
        return true;
    }

    public Boolean sp_IsElementPresent(String strXPath, String strValue, String strArea) {
        if (strValue.equalsIgnoreCase("No")) {
            if (automationObject.genfunc_isElementPresent(strXPath) == true) {
                System.out.println("FAILED - " + strArea + " is present.");
                return false;
            }
            return true;
        }
        if (automationObject.genfunc_isElementPresent(strXPath) == true) {
            return true;
        }
        System.out.println("FAILED - " + strArea + " is NOT present.");
        return false;
    }

    ///////////////////////////////////
    ////            Claim Action Methods
    ///////////////////////////////////
    public Boolean sp_SubmitClaim() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Submit Claim\")]");
        return true;
    }

    public Boolean sp_SaveClaim() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Save Claim\")]");
        return true;
    }

    public Boolean sp_DeleteClaim() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Delete Claim\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_RejectClaim(String strRejectionReason, String strReasonNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Reject Claim\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"rejectionReason\"]", strRejectionReason);
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"supportingRejectionNote\"]", strReasonNote);
        automationObject.genfunc_Click("//*[@id=\"rejectClaimForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AcknowledgeClaim() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Acknowledge Claim\")]");
        return true;
    }

    public Boolean sp_CloseClaim(String strCloseReason) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Close Claim\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"reasonToClose\"]", strCloseReason);
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[@id=\"closeClaimForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_ResubmitClaim(String strNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Resubmit Claim\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"resubmissionSupportingNote\"]", strNote);
        automationObject.genfunc_Click("//*[@id=\"resubmitClaimForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_SendToFNOL() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Send to FNOL\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[@id=\"sendToFNOLForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_ReturnClaimFromFNOL() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Return Claim from FNOL\")]");
        automationObject.genfunc_waitForAngular();
        //Defendant Claim Number must be provided from data setup
        automationObject.genfunc_Click("//*[@id=\"returnToHandlerFromFNOLForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AssignWorkgroup(String strWorkgroup) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Assign Workgroup\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"workgroup\"]", strWorkgroup);
        automationObject.genfunc_Click("//*[@id=\"assignWorkgroupForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AssignOwner(String strOwner) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Assign Owner\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"owner\"]", strOwner);
        automationObject.genfunc_Click("//*[@id=\"assignOwnerForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_Reassign(String strWorkgroup, String strOwner) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Re-assign\")]");
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementPresent("//*[@id=\"workgroup\"]") == true) {
            automationObject.genfunc_SetText("//*[@id=\"workgroup\"]", strWorkgroup);
            automationObject.genfunc_waitForAngular();
        }
        if (automationObject.genfunc_isElementPresent("//*[@id=\"owner\"]") == true) {
            automationObject.genfunc_SetText("//*[@id=\"owner\"]", strOwner);
            automationObject.genfunc_waitForAngular();
        }
        automationObject.genfunc_Click("//*[@id=\"assignWorkgroupForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AcceptRejection() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Accept Rejection\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AgreeQuantum() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Agree Quantum\")]");
        return true;
    }

    public Boolean sp_ConfirmPayment() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Confirm Payment\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AddInvoice(String strInvoiceRef, String strTotalNet) {
        automationObject.genfunc_waitForAngular();
        if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Add Invoice\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Add Invoice\")]");
        }
        if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Add Supplementary Invoice\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Add Supplementary Invoice\")]");
        }
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"invoiceReferenceNumber\"]", strInvoiceRef);
        automationObject.genfunc_Click("//*[@id=\"draftInvoiceForm\"]/div[2]/div[1]/div[2]/div/div/div/div[2]/div[2]/div[1]/div/span[2]/button");
        automationObject.genfunc_ClearText("//*[@id=\"totalNet\"]");
        automationObject.genfunc_SetText("//*[@id=\"totalNet\"]", strTotalNet);
        automationObject.genfunc_Click("//*[@id=\"draftInvoiceForm\"]/div[2]/div[1]/div[2]/div/div/div/div[2]/div[2]/div[1]/div/span[2]/button/i");
        automationObject.genfunc_Click("//*[@id=\"draftInvoiceForm\"]/div[1]/div/button/span");
        return true;

    }

    public Boolean sp_ReopenInvoice(String strSupportingNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Reopen Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Add in the provided supporting note. (Not mandatory)
        automationObject.genfunc_SetText("/*[@id=\"supportingNote\"]", strSupportingNote);
        automationObject.genfunc_waitForAngular();
        //Click on the reopen invoice button in the popup window.
        automationObject.genfunc_Click("//*[@id=\"reopenInvoiceForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_CloseInvoice(String strSupportingNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Close Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Add in the provided supporting note. (Not mandatory)
        automationObject.genfunc_SetText("/*[@id=\"supportingNote\"]", strSupportingNote);
        automationObject.genfunc_waitForAngular();
        //Click on the close invoice button in the popup window.
        automationObject.genfunc_Click("//*[@id=\"closeInvoiceForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_RejectInvoice(String strRejectionReason, String strReasonNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Reject Invoice\")]");
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"rejectionReason\"]", strRejectionReason);
        automationObject.genfunc_SetText("//*[@id=\"resubmissionSupportingNote\"]", strReasonNote);
        automationObject.genfunc_Click("//*[@id=\"rejectInvoiceForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_DeleteInvoice() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Delete Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Click yes on popup window
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_SaveInvoice() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Save Invoice\")]");
        return true;
    }

    public Boolean sp_ResubmitInvoice(String strSupportingNote) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Resubmit Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Add a default supporting note if one has not been provided.
        if (strSupportingNote.equals("")) {
            automationObject.genfunc_SetText("//*[@id=\"supportingResubmissionNote\"]", "Default automation supporting rebsubmission note.");
        }
        //Add in the provided supporting note.
        automationObject.genfunc_SetText("//*[@id=\"supportingResubmissionNote\"]", strSupportingNote);
        automationObject.genfunc_waitForAngular();
        //Click on the resubmit invoice button in the popup window.
        automationObject.genfunc_Click("//*[@id=\"resubmitInvoiceForm\"]/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_SubmitInvoice() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Submit Invoice\")]");
        return true;
    }

    public Boolean sp_ReconcileInvoice() {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//button[contains(.,\"Reconcile Invoice\")]");
        automationObject.genfunc_waitForAngular();
        //Click the reconcile button on the popup window
        automationObject.genfunc_waitForPageToLoad("/html/body/div[3]/div/div/div/div[3]/a[1]");
        automationObject.genfunc_Click("/html/body/div[3]/div/div/div/div[3]/a[1]");
        return true;
    }

    public Boolean sp_AssignLiability(String strStatus, String strClaimantLiabilityPercentage, String strDefendantLiabilityPercentage, String strSupportingNote) {
        automationObject.genfunc_waitForAngular();
        //Clicks 'Assign Initial Liability' or 'Update Liability' button (depending which is present)
        if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Assign Initial Liability\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Assign Initial Liability\")]");
        } else if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Update Liability\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Update Liability\")]");
        } else if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Confirm Liability\")]")) {
            automationObject.genfunc_Click("//button[contains(.,\"Confirm Liability\")]");
        } else {
            return false;
        }
        automationObject.genfunc_waitForAngular();
        //Choose Liability Status from the dropdown
        automationObject.genfunc_Click("//*[@id=\"liabilityStatus\"]/option[contains(.,\"" + strStatus + "\")]");
        //Claimant/Defendant Liability textboxes (Optional): provided values are used.
        //Claimant/Defendant Liability textboxes (Mandatory): default values are used if none have been provided.
        //Supporting Note (Mandatory): default support note is added if one has not been provided.
        switch (strStatus) {
            case "Full Liability Accepted":
                break;
            case "Proceed Without Prejudice":
                break;
            case "Liability Unknown":
                if (!strClaimantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_ClearText("//*[@id=\"claimantLiabilityPercentage\"]");
                    automationObject.genfunc_SetText("//*[@id=\"claimantLiabilityPercentage\"]", strClaimantLiabilityPercentage);
                }
                if (!strDefendantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_ClearText("//*[@id=\"defendantLiabilityPercentage\"]");
                    automationObject.genfunc_SetText("//*[@id=\"defendantLiabilityPercentage\"]", strDefendantLiabilityPercentage);
                }
                break;
            case "Liability in Negotiation":
                if (!strClaimantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_ClearText("//*[@id=\"claimantLiabilityPercentage\"]");
                    automationObject.genfunc_SetText("//*[@id=\"claimantLiabilityPercentage\"]", strClaimantLiabilityPercentage);
                }
                if (!strDefendantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_ClearText("//*[@id=\"defendantLiabilityPercentage\"]");
                    automationObject.genfunc_SetText("//*[@id=\"defendantLiabilityPercentage\"]", strDefendantLiabilityPercentage);
                }
                if (strSupportingNote.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"supportingLiabilityNotes\"]", "Default automation suppporting liablility note.");
                }
                break;
            case "Liability Split":
                automationObject.genfunc_ClearText("//*[@id=\"claimantLiabilityPercentage\"]");
                automationObject.genfunc_SetText("//*[@id=\"claimantLiabilityPercentage\"]", strClaimantLiabilityPercentage);
                automationObject.genfunc_ClearText("//*[@id=\"defendantLiabilityPercentage\"]");
                automationObject.genfunc_SetText("//*[@id=\"defendantLiabilityPercentage\"]", strDefendantLiabilityPercentage);
                if (strClaimantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"claimantLiabilityPercentage\"]", "50");
                }
                if (strDefendantLiabilityPercentage.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"defendantLiabilityPercentage\"]", "50");
                }
                if (strSupportingNote.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"supportingLiabilityNotes\"]", "Suppporting Liablility Note text.");
                }
                break;
            case "Liability Repudiated":
                if (strSupportingNote.equals("")) {
                    automationObject.genfunc_SetText("//*[@id=\"supportingLiabilityNotes\"]", "Suppporting Liablility Note text.");
                }
                break;
            default:
                automationObject.genfunc_Click("//*[@id=\"liabilityStatus\"]/option[contains(.,\"Full Liability Accepted\")]");
                break;
        }
        //Add supporting liability note
        automationObject.genfunc_SetText("//*[@id=\"supportingLiabilityNotes\"]", strSupportingNote);
        //Click the Update Liability button to save the liability choices
        automationObject.genfunc_Click("//*[@id=\"setLiabilityForm\"]/div/div[3]/a[1]");
        automationObject.genfunc_waitForAngular();
        return true;
    }

    public Boolean sp_CompensatingAction(String strCompensateAction) {
        automationObject.genfunc_waitForAngular();
        //Performs action on claim before compensating action
        if (strCompensateAction.equalsIgnoreCase("Cancel Claim Acknowledgement") == true) {
            sp_AcknowledgeClaim();
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
        }
        if (strCompensateAction.equalsIgnoreCase("Cancel Quantum Agreement") == true) {
            if (automationObject.genfunc_isElementPresent("//button[contains(.,\"Confirm Liability\")]")) {
                automationObject.genfunc_waitForAngular();
                automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
            } else {
                sp_AgreeQuantum();
                automationObject.genfunc_waitForAngular();
                automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
            }
        }
        if (strCompensateAction.equalsIgnoreCase("Cancel Payment") == true) {
            sp_ConfirmPayment();
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
        }
        if (strCompensateAction.equalsIgnoreCase("Cancel Reconciliation") == true) {
            sp_ReconcileInvoice();
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//button[contains(.,\"" + strCompensateAction + "\")]");
        }
        automationObject.genfunc_waitForAngular();
        //Click the reconcile button on the popup window
        automationObject.genfunc_Click("//*[@id=\"cancelActionForm\"]/div/div[3]/a[1]");

        return true;
    }

    public Boolean sp_SubmitNewDraft(String ClaimNo, String DefendantInsurer) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
        automationObject.genfunc_waitForAngular();
        sp_PopulateNewDraft(ClaimNo, DefendantInsurer, "10/08/2016", "Yes", "Yes");
        sp_SubmitClaim();

        return true;
    }

    public Boolean sp_PopulateNewDraft(String ClaimNo, String DefendantInsurer, String DateOfIncident, String TotalLoss, String CarUsable) {
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_SetText("//*[@id=\"claimantClaimNumber\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"claimantPolicyNumber\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"claimantFirstName\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"claimantLastName\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"defendantInsurer\"]", DefendantInsurer);
        automationObject.genfunc_SetText("//*[@id=\"defendantClaimNumber\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"defendantPolicyNumber\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"defendantFirstName\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"defendantLastName\"]", ClaimNo);
        automationObject.genfunc_SetText("//*[@id=\"incidentLocation\"]", "Location");
        automationObject.genfunc_SetText("//*[@id=\"incidentDescription\"]", "Damage");
        automationObject.genfunc_SetText("//*[@id=\"damageDescription\"]", "Description");
        automationObject.genfunc_SetText("//*[@id=\"dateOfIncident\"]/div/input", DateOfIncident);
        automationObject.genfunc_SetText("//*[@id=\"totalLoss\"]", TotalLoss);
        automationObject.genfunc_SetText("//*[@id=\"carUsable\"]", CarUsable);
        return true;
    }

    public Boolean sp_Action(String strAction, String strInput1, String strInput2, String strInput3, String strInput4) {
        if (strAction.equalsIgnoreCase("Submit Claim")) {
            return sp_SubmitClaim();
        }
        if (strAction.equalsIgnoreCase("Save Claim")) {
            return sp_SaveClaim();
        }
        if (strAction.equalsIgnoreCase("Delete Claim")) {
            return sp_DeleteClaim();
        }
        if (strAction.equalsIgnoreCase("Reject Claim")) {
            return sp_RejectClaim(strInput1, strInput2);
        }
        if (strAction.equalsIgnoreCase("Acknowledge Claim")) {
            return sp_AcknowledgeClaim();
        }
        if (strAction.equalsIgnoreCase("Close Claim")) {
            return sp_CloseClaim(strInput1);
        }
        if (strAction.equalsIgnoreCase("Resubmit Claim")) {
            return sp_ResubmitClaim(strInput1);
        }
        if (strAction.equalsIgnoreCase("Send To FNOL")) {
            return sp_SendToFNOL();
        }
        if (strAction.equalsIgnoreCase("Return Claim From FNOL")) {
            return sp_ReturnClaimFromFNOL();
        }
        if (strAction.equalsIgnoreCase("Assign Workgroup")) {
            return sp_AssignWorkgroup(strInput1);
        }
        if (strAction.equalsIgnoreCase("Assign Owner")) {
            return sp_AssignOwner(strInput1);
        }
        if (strAction.equalsIgnoreCase("Re-assign")) {
            return sp_Reassign(strInput1, strInput2);
        }
        if (strAction.equalsIgnoreCase("Accept Rejection")) {
            return sp_AcceptRejection();
        }
        if (strAction.equalsIgnoreCase("Agree Quantum")) {
            return sp_AgreeQuantum();
        }
        if (strAction.equalsIgnoreCase("Confirm Payment")) {
            return sp_ConfirmPayment();
        }
        if (strAction.equalsIgnoreCase("Add Invoice") || strAction.equalsIgnoreCase("Add Supplementary Invoice")) {
            return sp_AddInvoice(strInput1, strInput2);
        }
        if (strAction.equalsIgnoreCase("Reopen Invoice")) {
            return sp_ReopenInvoice(strInput1);
        }
        if (strAction.equalsIgnoreCase("Close Invoice")) {
            return sp_CloseInvoice(strInput1);
        }
        if (strAction.equalsIgnoreCase("Reject Invoice")) {
            return sp_RejectInvoice(strInput1, strInput2);
        }
        if (strAction.equalsIgnoreCase("Delete Invoice")) {
            return sp_DeleteInvoice();
        }
        if (strAction.equalsIgnoreCase("Save Invoice")) {
            return sp_SaveInvoice();
        }
        if (strAction.equalsIgnoreCase("Resubmit Invoice")) {
            return sp_ResubmitInvoice(strInput1);
        }
        if (strAction.equalsIgnoreCase("Submit Invoice")) {
            return sp_SubmitInvoice();
        }
        if (strAction.equalsIgnoreCase("Reconcile Invoice")) {
            return sp_ReconcileInvoice();
        }
        if (strAction.equalsIgnoreCase("Assign Initial Liability") || strAction.equalsIgnoreCase("Update Liability") || strAction.equalsIgnoreCase("Confirm Liability")) {
            return sp_AssignLiability(strInput1, strInput2, strInput3, strInput4);
        }
        if (strAction.equalsIgnoreCase("Cancel Claim Acknowledgement") || strAction.equalsIgnoreCase("Cancel Quantum Agreement") || strAction.equalsIgnoreCase("Cancel Payment") || strAction.equalsIgnoreCase("Cancel Reconciliation")) {
            return sp_CompensatingAction(strAction);
        }

        return true;
    }

    public Boolean sp_PerformAction(String strClaimantClaimNumber, String strAction, String strActionInput1, String strActionInput2,
            String strActionInput3, String strActionInput4, String strActionPanelText, String strEvent, String strScreenshotLocation) {
        System.out.println("    Performing '" + strAction + "' action for claim '" + strClaimantClaimNumber + "'.");

        try {

            //Do Action
            sp_Action(strAction, strActionInput1, strActionInput2, strActionInput3, strActionInput4);
            automationObject.genfunc_waitForAngular();

//Commented out for Sprint 15 Screenshot Functionality
            /*Check Action Panel Text
            if (strActionPanelText.equalsIgnoreCase("None")) {
                //No expected action panel text
            } else {
                if (automationObject.genfunc_GetTextNoOutput("//*[@id=\"tabs-page-content-area\"]//data-action-panel/div/div/p[1]", strActionPanelText) == false) {
                    automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - " + strAction + " - ActionPanelText.png");
                    System.out.println("Failed - Action panel text is not as expected. See screenshot for more details.");
                }
            }

            //Check Event
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div/data-event-log/div/div[1]/h3"); //Expand Event Log panel
            automationObject.genfunc_waitForAngular();
            /Check the Event of the first row
            if (automationObject.genfunc_GetTextNoOutput(".//*[@id='eventLogTableId']//table/tbody/tr[1]/td[3]", strEvent) == false) {
                automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - " + strAction + " - Event.png");
                System.out.println("Failed - Event text is not as expected. See screenshot for more details.");
            }

            //Tab back to original page
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
            automationObject.genfunc_waitForAngular();
             */
        } //Catches the exception if the action fails
        catch (Exception e) {
            System.out.println("ERROR - '" + strAction + "' action on claim '" + strClaimantClaimNumber + "' failed to be correctly performed.");
            automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - " + strAction + ".png");
            automationObject.genfunc_RefreshPage();
            automationObject.genfunc_waitForAngular();
            sp_Logout();
            return false;
        }
        return true;
    }

    /////////////////////////////////////////////
    ////            Subrogation Workflow Methods
    /////////////////////////////////////////////
    public Boolean sp_Workflow_DataSetRunner(String DataSetName) {

        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        //To find the last row in the data set
        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {

            //Get values from data set
            String strActingAs = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");
            String strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strCurrentQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strClaimantClaimNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strAction = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strActionInput1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
            String strActionInput2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
            String strActionInput3 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
            String strActionInput4 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strActionPanelText = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strEvent = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");
            String strExpectedQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strScreenshotLocation = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");
            String strScreenshotOnly = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)");

            //Login
            if (sp_Login(strUsername, strPassword) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Login)");
                return false;
            }

            //Navigate to queue or create claim
            if (sp_Workflow_Navigate(strActingAs, strCurrentQueue, strClaimantClaimNumber, strActionInput1, strActionInput2, strActionInput3, strActionInput4) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Workflow_Navigate)");
                return false;
            }

            //Check to take first second screenshot state
            if (strScreenshotOnly.equalsIgnoreCase("Y") || strScreenshotOnly.equalsIgnoreCase("Yes")) {
                automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + strActingAs + strCurrentQueue + strAction + "_before.png");
            }

            //Perform action and check it was successful - checking is commented out
            if (sp_PerformAction(strClaimantClaimNumber, strAction, strActionInput1, strActionInput2,
                    strActionInput3, strActionInput4, strActionPanelText, strEvent, strScreenshotLocation) == false) {
                //if action is not completed successfully skip checking queues for this claim.
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_PerformAction)");
                return false;
            }
            //Check to take second screenshot state
            if (strScreenshotOnly.equalsIgnoreCase("Y") || strScreenshotOnly.equalsIgnoreCase("Yes")) {
                automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + strActingAs + strCurrentQueue + strAction + "_after.png");
            }

            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
            automationObject.genfunc_waitForAngular();

            //Check claim is in the correct queue
            if (sp_Workflow_QueueCheck(strActingAs, strExpectedQueue, strClaimantClaimNumber) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Workflow_QueueCheck)");
            }

            //log out
            sp_Logout();
        }

        return true;
    }

    public Boolean sp_Workflow_Navigate(String strActingAs, String strQueue, String strClaimantClaimNumber,
            String strActionInput1, String strActionInput2, String strActionInput3, String strActionInput4) {

        //Open Claim or create claim if the current queue is set to "None"
        if (!strQueue.equalsIgnoreCase("None")) {
            //Select Queue
            sp_SelectMenuQueue(strActingAs, strQueue);
            //Select Claim
            if (sp_SelectClaim(strClaimantClaimNumber) == false) {
                sp_Logout();
                return false;
            }
        } else {
            sp_SelectMenuQueue("Claimant", "Draft Claims");
            //create draft button
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
            automationObject.genfunc_waitForAngular();
            //fill inputs
            sp_PopulateNewDraft(strClaimantClaimNumber, strActionInput1, strActionInput2, strActionInput3, strActionInput4);
        }
        automationObject.genfunc_waitForAngular();
        return true;
    }

    public Boolean sp_Workflow_QueueCheck(String strActingAs, String strQueue, String strClaimantClaimNumber) {
        System.out.println("    Checking claim '" + strClaimantClaimNumber + "' is in '" + strQueue + "' " + strActingAs + " queue.");
        //Check if claim is present in specified queue.
        if (!strQueue.equalsIgnoreCase("None")) {
            //Select Queue
            sp_SelectMenuQueue(strActingAs, strQueue);
            //Check Queue
            if (sp_IsClaimPresent(strClaimantClaimNumber, "Yes") == false) {
                return false;
            }
        } //Or check if claim is NOT present any queue if expected queue is "None".
        else {
            if (sp_IsClaimNotPresent(strClaimantClaimNumber, strActingAs) == false) {
                return false;
            }
        }
        return true;
    }

    //////////  Old functions  //////////
    public Boolean sp_Workflow_MoveClaimState(String strActingAs, String strClaimantOrg, String strUsername, String strPassword, String strQueue, String strDefendantOrg, String strAltUsername, String strAltPassword, String strAltQueue,
            String strClaimantClaimNumber, String strAction, String strExpectedQueue, String strComment, String strOrganisationType, String strAltOrganisationType) {
        switch (strActingAs) {
            case "Claimant":
                switch (strQueue) {
                    case "Assign Workgroup":
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltQueue.equalsIgnoreCase("Assign Workgroup") == true) {
                        }
                        if (strAltQueue.equalsIgnoreCase("Assign Owner") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                        }
                        if (strAltQueue.equalsIgnoreCase("Claims to be Registered") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                            sp_Action("Send to FNOL", "", "", "", "");
                        }
                        if (strAltQueue.equalsIgnoreCase("Awaiting Acknowledgement") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                        }
                        if (strAltQueue.equalsIgnoreCase("None") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                            if (strComment.contains("rejected") == true) {
                                sp_Action("Reject Claim", "Other", "Note", "", "");
                            } else {
                                sp_Action("Assign Initial Liability", "Full Liability Accepted", "", "", "");
                                sp_Action("Acknowledge Claim", "", "", "", "");
                            }
                        }
                        sp_Logout();
                        break;
                    case "Assign Owner":
                        //Claimant Actions
                        sp_Logout();
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                        } else {
                            sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                        }
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltQueue.equalsIgnoreCase("Assign Workgroup") == true) {
                        }
                        if (strAltQueue.equalsIgnoreCase("Assign Owner") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                        }
                        if (strAltQueue.equalsIgnoreCase("Claims to be Registered") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                            sp_Action("Send to FNOL", "", "", "", "");
                        }
                        if (strAltQueue.equalsIgnoreCase("Awaiting Acknowledgement") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                        }
                        if (strAltQueue.equalsIgnoreCase("None") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                            sp_Action("Assign Initial Liability", "Full Liability Accepted", "", "", "");
                            sp_Action("Acknowledge Claim", "", "", "", "");
                        }
                        sp_Logout();
                        break;
                    case "Rejected Claims":
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltQueue.equalsIgnoreCase("Assign Workgroup") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Reject Claim", "Other", "Note", "", "");
                        }
                        if (strAltQueue.equalsIgnoreCase("Assign Owner") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                            } else {
                                sp_SelectMenuQueue("Defendant", " Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                            sp_Action("Reject Claim", "Other", "Note", "", "");
                        }
                        if (strAltQueue.equalsIgnoreCase("None") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                            sp_Action("Reject Claim", "Other", "Note", "", "");
                        }
                        sp_Logout();
                        //Claimant Actions
                        sp_Login(strUsername, strPassword);
                        if (strExpectedQueue.equalsIgnoreCase("None")) {
                            if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Claimant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                        }
                        if (strExpectedQueue.equalsIgnoreCase("Assign Owner")) {
                            if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                        }
                        if (strExpectedQueue.equalsIgnoreCase("Assign Workgroup")) {
                            //Placeholder
                        }
                        break;
                    case "Awaiting Invoice":
                        //Defendant Action
                        sp_Logout();
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Assign Initial Liability", "Full Liability Accepted", "", "", "");
                        sp_Action("Acknowledge Claim", "", "", "", "");
                        sp_Logout();
                        //Claimant Action
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Claimant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Logout();
                        break;
                    case "Draft Invoices":
                        //Defendant Action
                        sp_Logout();
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Assign Initial Liability", "Full Liability Accepted", "", "", "");
                        sp_Action("Acknowledge Claim", "", "", "", "");
                        sp_Logout();
                        //Claimant Action
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Claimant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Add Invoice", strClaimantClaimNumber, "100", "", "");
                        sp_Logout();
                        break;
                    case "Queried Invoices":
                        //Defendant Action
                        sp_Logout();
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Assign Initial Liability", "Full Liability Accepted", "", "", "");
                        sp_Action("Acknowledge Claim", "", "", "", "");
                        sp_Logout();
                        //Claimant Action
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Claimant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Add Invoice", strClaimantClaimNumber, "100", "", "");
                        sp_Action("Submit Invoice", "", "", "", "");
                        sp_Logout();
                        //Claimant Action
                        sp_Login(strAltUsername, strAltPassword);
                        sp_SelectMenuQueue("Defendant", "Pending Quantum");
                        sp_SelectClaim(strClaimantClaimNumber);
                        sp_Action("Reject Invoice", "Other", "Automated Supporting Note", "", "");
                        sp_Logout();
                        break;
                    case "Paid Invoices":
                        //Defendant Action
                        sp_Logout();
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Assign Initial Liability", "Full Liability Accepted", "", "", "");
                        sp_Action("Acknowledge Claim", "", "", "", "");
                        sp_Logout();
                        //Claimant Action
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Claimant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Add Invoice", strClaimantClaimNumber, "100", "", "");
                        sp_Action("Submit Invoice", "", "", "", "");
                        sp_Logout();
                        //Defendant Action
                        sp_Login(strAltUsername, strAltPassword);
                        sp_SelectMenuQueue("Defendant", "Pending Quantum");
                        sp_SelectClaim(strClaimantClaimNumber);
                        sp_Action("Agree Quantum", "", "", "", "");
                        sp_Action("Confirm Payment", "", "", "", "");
                        sp_Logout();
                        if (strComment.contains("compensated")) {
                            sp_Login(strUsername, strPassword);
                            sp_SelectMenuQueue("Claimant", "Paid Invoices");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Reconcile Invoice", "", "", "", "");
                            sp_Action("Cancel Reconcilation", "", "", "", "");
                            sp_Logout();
                        }
                        break;
                }
                break;
            case "Defendant":
                switch (strQueue) {
                    case "Assign Workgroup":
                        //Default to Assign Workgroup
                        sp_Logout();
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltQueue.equalsIgnoreCase("Assign Workgroup") == true) {
                        }
                        if (strAltQueue.equalsIgnoreCase("Assign Owner") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                        }
                        if (strAltQueue.equalsIgnoreCase("None") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Claimant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                                }
                            }
                        }
                        sp_Logout();
                        break;
                    case "Assign Owner":
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                        }
                        sp_Logout();
                        //Claimant Actions
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltQueue.equalsIgnoreCase("Assign Workgroup") == true) {
                        }
                        if (strAltQueue.equalsIgnoreCase("Assign Owner") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                        }
                        if (strAltQueue.equalsIgnoreCase("None") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Claimant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                                }
                            }
                        }
                        sp_Logout();
                        break;
                    case "Claims to be Registered":
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strUsername, strPassword);
                        if (strComment.contains("Workgroup") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                        }
                        if (strComment.contains("Owner") == true) {
                            if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                        } else {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Defendant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                                }
                            }
                        }
                        sp_Action("Send to FNOL", "", "", "", "");
                        sp_Logout();
                        //Claimant Actions
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltQueue.equalsIgnoreCase("Assign Workgroup") == true) {
                        }
                        if (strAltQueue.equalsIgnoreCase("Assign Owner") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                        }
                        if (strAltQueue.equalsIgnoreCase("None") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Claimant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                                }
                            }
                        }
                        sp_Logout();
                        break;
                    case "Awaiting Acknowledgement":
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        if (strAction.equalsIgnoreCase("Acknowledge Claim") || strAction.equalsIgnoreCase("Cancel Claim Acknowledgement") == true) {
                            sp_Action("Assign Initial Liability", "Full Liability Accepted", "", "", "");
                        }
                        if (strComment.contains("compensated") == true) {
                            sp_Action("Acknowledge Claim", "", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            sp_Action("Cancel Claim Acknowledgement", "", "", "", "");
                        } else {
                        }
                        sp_Logout();
                        //Claimant Actions
                        sp_Login(strAltUsername, strAltPassword);
                        if (strAltQueue.equalsIgnoreCase("Assign Workgroup") == true) {
                        }
                        if (strAltQueue.equalsIgnoreCase("Assign Owner") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            }
                        }
                        if (strAltQueue.equalsIgnoreCase("None") == true) {
                            if (strAltOrganisationType.equalsIgnoreCase("Ownership") == true) {
                                sp_SelectMenuQueue("Claimant", "Assign Owner");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                            } else {
                                sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                                sp_SelectClaim(strClaimantClaimNumber);
                                sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                                automationObject.genfunc_waitForAngular();
                                if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                    sp_Action("Assign Owner", strClaimantOrg, "", "", "");
                                }
                            }
                        }
                        sp_Logout();
                        break;
                    case "Pending Quantum":
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        if (strExpectedQueue.equalsIgnoreCase("Pending Liability") || (strAction.equalsIgnoreCase("Update Liability")) == true) {
                            sp_Action("Assign Initial Liability", "Liability Unknown", "", "", "");
                        } else {
                            sp_Action("Assign Initial Liability", "Full Liablity Accepted", "", "", "");
                        }
                        sp_Action("Acknowledge Claim", "", "", "", "");
                        sp_Logout();
                        //Claimant Actions
                        sp_Login(strAltUsername, strAltPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Claimant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Add Invoice", strClaimantClaimNumber, "100", "", "");
                        sp_Action("Submit Invoice", "", "", "", "");
                        sp_Logout();
                        break;
                    case "Pending Liability":
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Assign Initial Liability", "Liability Unknown", "", "", "");
                        sp_Action("Acknowledge Claim", "", "", "", "");
                        sp_Logout();
                        //Claimant Actions
                        sp_Login(strAltUsername, strAltPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Claimant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Add Invoice", strClaimantClaimNumber, "100", "", "");
                        sp_Action("Submit Invoice", "", "", "", "");
                        sp_Logout();
                        //Defendant Action
                        sp_Login(strUsername, strPassword);
                        sp_SelectMenuQueue("Defendant", "Pending Quantum");
                        sp_SelectClaim(strClaimantClaimNumber);
                        sp_Action("Agree Quantum", "", "", "", "");
                        if (strComment.contains("compensated") == true) {
                            sp_Action("Cancel Quantum Agreement", "", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            sp_Action("Agree Quantum", "", "", "", "");
                        } else {
                        }
                        sp_Logout();
                        break;
                    case "Pending Payment":
                        //Defendant Actions
                        sp_Logout();
                        sp_Login(strUsername, strPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Defendant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Defendant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Assign Initial Liability", "Full Liability Accepted", "", "", "");
                        sp_Action("Acknowledge Claim", "", "", "", "");
                        sp_Logout();
                        //Claimant Actions
                        sp_Login(strAltUsername, strAltPassword);
                        if (strOrganisationType.equalsIgnoreCase("Ownership") == true) {
                            sp_SelectMenuQueue("Claimant", "Assign Owner");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                        } else {
                            sp_SelectMenuQueue("Claimant", "Assign Workgroup");
                            sp_SelectClaim(strClaimantClaimNumber);
                            sp_Action("Assign Workgroup", "Workgroup A", "", "", "");
                            automationObject.genfunc_waitForAngular();
                            if (automationObject.isElementPresent("//button[contains(.,\"Assign Owner\")]") == true) {
                                sp_Action("Assign Owner", strDefendantOrg, "", "", "");
                            }
                        }
                        sp_Action("Add Invoice", strClaimantClaimNumber, "100", "", "");
                        sp_Action("Submit Invoice", "", "", "", "");
                        sp_Logout();
                        //Defendant Actions
                        sp_Login(strUsername, strPassword);
                        sp_SelectMenuQueue("Defendant", "Pending Quantum");
                        sp_SelectClaim(strClaimantClaimNumber);
                        sp_Action("Agree Quantum", "", "", "", "");
                        sp_Logout();
                        break;
                }
                break;
        }
        return true;
    }

    public Boolean sp_Workflow_DataSetRunner_old(String DataSetName) {
        //To find the last row in the data set
        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        String strQueue;
        String strExpectedQueue;
        String strUsername;
        String strPassword;
        String strAltUsername;
        String strAltPassword;
        String strAltExpectedQueue;
        String strAltActingAs;
        String strAltQueue;
        String strOrganisationType;
        String strAltOrganisationType;

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {

            String strActingAs = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");

            if (strActingAs.equalsIgnoreCase("Claimant")) {
                strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
                strPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
                strAltUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
                strAltPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
                strQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
                strAltQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
                strExpectedQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",17)");
                strAltExpectedQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",18)");
                strAltActingAs = "Defendant";
                strOrganisationType = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",21)");
                strAltOrganisationType = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",22)");
            } else if (strActingAs.equalsIgnoreCase("Defendant")) {
                strAltUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
                strAltPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
                strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
                strPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
                strQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
                strAltQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
                strExpectedQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",18)");
                strAltExpectedQueue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",17)");
                strAltActingAs = "Claimant";
                strOrganisationType = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",22)");
                strAltOrganisationType = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",21)");
            } else {
                return false;
            }
            String strClaimantOrg = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strDefendantOrg = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strClaimantClaimNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strAction = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strActionInput1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");
            String strActionInput2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strActionInput3 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");
            String strActionInput4 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)");
            String strActionPanelText = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",16)");
            String strScreenshotLocation = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",19)");
            String strClaimantUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strClaimantPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strComment = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",20)");

            //Create Claim
            if (sp_Login(strClaimantUsername, strClaimantPassword) == false) {
                continue;
            }
            automationObject.genfunc_waitForAngular();
            if (strAction.equalsIgnoreCase("Submit Claim") || strAction.equalsIgnoreCase("Save Claim")) {
                if (!strQueue.equalsIgnoreCase("None")) {
                    if (sp_Data_CreateClaim(strQueue, strClaimantClaimNumber, strDefendantOrg) == false) {
                        continue;
                    }
                    automationObject.genfunc_waitForAngular();
                }
            } else {
                if (sp_Data_CreateClaim(strQueue, strClaimantClaimNumber, strDefendantOrg) == false) {
                    continue;

                }
            }
            sp_Logout();

            automationObject.genfunc_waitForAngular();

            if (sp_Workflow_MoveClaimState(strActingAs, strClaimantOrg, strUsername, strPassword, strQueue, strDefendantOrg, strAltUsername, strAltPassword, strAltQueue,
                    strClaimantClaimNumber, strAction, strExpectedQueue, strComment, strOrganisationType, strAltOrganisationType) == false) {
                continue;
            }

            if (sp_Login(strUsername, strPassword) == false) {
                continue;
            }
            if (sp_Workflow_Action_old(strActingAs, strQueue, strClaimantClaimNumber, strAction, strDefendantOrg, strActionInput1, strActionInput2,
                    strActionInput3, strActionInput4, strActionPanelText, strExpectedQueue, strScreenshotLocation) == false) {
                //if action is not completed successfully skip checking queues for this claim.
                continue;
            }
            sp_Workflow_QueueCheck(strActingAs, strExpectedQueue, strClaimantClaimNumber);
            if (sp_Login(strAltUsername, strAltPassword) == false) {
                continue;
            }
            sp_Workflow_QueueCheck(strAltActingAs, strAltExpectedQueue, strClaimantClaimNumber);
        }

        return true;
    }

    public Boolean sp_Workflow_Action_old(String strActingAs, String strQueue, String strClaimantClaimNumber, String strAction, String strDefendantOrg,
            String strActionInput1, String strActionInput2, String strActionInput3, String strActionInput4, String strActionPanelText,
            String strExpectedQueue, String strScreenshotLocation) {
        System.out.println("    Performing '" + strAction + "' action for claim '" + strClaimantClaimNumber + "'.");

        //Open Claim or create claim if the current queue is set to "None"
        if (!strQueue.equalsIgnoreCase("None")) {
            //Select Queue
            sp_SelectMenuQueue(strActingAs, strQueue);
            //Select Claim
            if (sp_SelectClaim(strClaimantClaimNumber) == false) {
                sp_Logout();
                return false;
            }
        } else {
            sp_SelectMenuQueue("Claimant", "Draft Claims");
            //create draft button
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
            automationObject.genfunc_waitForAngular();
            //fill inputs
            sp_PopulateNewDraft(strClaimantClaimNumber, strDefendantOrg, "01/01/2010", "No", "Yes");
        }
        automationObject.genfunc_waitForAngular();

        try {
            //Do Action
            sp_Action(strAction, strActionInput1, strActionInput2, strActionInput3, strActionInput4);
            automationObject.genfunc_waitForAngular();
            //Check Action Panel Text
            if (strActionPanelText.equalsIgnoreCase("None")) {
                //No expected action panel text
            } else if (!strExpectedQueue.equalsIgnoreCase("Draft Claims")) {
                if (automationObject.genfunc_GetTextNoOutput("//*[@id=\"tabs-page-content-area\"]/div/div/data-action-panel/div/div/p[1]", strActionPanelText) == false) {
                    automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - ActionPanelText.png");
                    System.out.println("Failed - Action panel text is not as expected.");
                }
            } else {
                if (automationObject.genfunc_GetTextNoOutput("//*[@id=\"tabs-page-content-area\"]/div/data-action-panel/div/div/p[1]", strActionPanelText) == false) {
                    automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - ActionPanelText.png");
                    System.out.println("Failed - Action panel text is not as expected.");
                }
            }
            //Tab back to queue page
            automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading");
            automationObject.genfunc_waitForAngular();
        } //catches the exception if the action fails
        catch (Exception e) {
            System.out.println("ERROR - '" + strAction + "' action on claim '" + strClaimantClaimNumber + "' failed to be correctly performed.");
            automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimantClaimNumber + " - " + strAction + ".png");
            automationObject.genfunc_RefreshPage();
            automationObject.genfunc_waitForAngular();
            sp_Logout();
            return false;
        }
        return true;
    }

    /////////////////////////////////////////////
    ////            Subrogation Bulk Upload Methods
    /////////////////////////////////////////////
    public Boolean sp_BulkUpload_DataSetRunner(String DataSetName) {

        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        int iLastRow = 0;
        while (getDataSet("$UploadDataSet(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {

            String strClaimantInsurer = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strClaimantClaimNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strClaimantWorkgroup = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strClaimantHandler = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strClaimantPolicyNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strClaimantTitle = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
            String strClaimantFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
            String strClaimantLastName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
            String strClaimantAddress1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strClaimantAddress2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strClaimantTown = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");
            String strClaimantCounty = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strClaimantPostcode = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");

            String strDefendantInsurer = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)");
            String strDefendantClaimNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",16)");
            String strDefendantWorkgroup = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",17)");
            String strDefendantHandler = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",18)");
            String strDefendantPolicyNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",19)");
            String strDefendantTitle = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",20)");
            String strDefendantFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",21)");
            String strDefendantLastName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",22)");
            String strDefendantAddress1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",23)");
            String strDefendantAddress2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",24)");
            String strDefendantTown = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",25)");
            String strDefendantCounty = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",26)");
            String strDefendantPostcode = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",27)");

            String strIncidentLocation = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",28)");
            String strIncidentDescription = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",29)");
            String strDamageDescription = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",30)");
            String strDateOfIncident = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",31)");
            String strTotalLoss = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",32)");
            String strCarUsable = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",33)");
            String strManagingRepair = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",34)");
            String strHireType = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",35)");

            String strClaimantVehicleMake = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",36)");
            String strClaimantVehicleModel = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",37)");
            String strClaimantVehicleVRN = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",38)");
            String strClaimantDriverTitle = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",39)");
            String strClaimantDriverFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",40)");
            String strClaimantDriverLastName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",41)");

            String strDefendantVehicleMake = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",42)");
            String strDefendantVehicleModel = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",43)");
            String strDefendantVehicleVRN = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",44)");
            String strDefendantDriverTitle = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",45)");
            String strDefendantDriverFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",46)");
            String strDefendantDriverLastName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",47)");

            String strPoliceInvolved = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 48)");
            String strCrimeRefNo = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 49)");
            String strPoliceOfficerName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 50)");
            String strPoliceOfficerID = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 51)");
            String strStationAddress1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 52)");
            String strStationAddress2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 53)");
            String strStationTown = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 54)");
            String strStationCounty = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 55)");
            String strStationPostcode = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 56)");

            String strWitnessName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 57)");
            String strWitnessAddress1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 58)");
            String strWitnessAddress2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 59)");
            String strWitnessTown = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 60)");
            String strWitnessCounty = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 61)");
            String strWitnessPostcode = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 62)");
            String strWitnessStatement = getDataSet("$" + DataSetName + "(" + iCurrentRow + ", 63)");

            if (strTotalLoss.toLowerCase().contains("n") || strTotalLoss.toLowerCase().contains("false")) {
                strTotalLoss = "No";
            } else if (strTotalLoss.toLowerCase().contains("y") || strTotalLoss.toLowerCase().contains("true")) {
                strTotalLoss = "Yes";
            }
            if (strCarUsable.toLowerCase().contains("n") || strCarUsable.toLowerCase().contains("false")) {
                strCarUsable = "No";
            } else if (strCarUsable.toLowerCase().contains("y") || strCarUsable.toLowerCase().contains("true")) {
                strCarUsable = "Yes";
            }
            if (strManagingRepair.toLowerCase().contains("n") || strManagingRepair.toLowerCase().contains("false")) {
                strManagingRepair = "No";
            } else if (strManagingRepair.toLowerCase().contains("y") || strManagingRepair.toLowerCase().contains("true")) {
                strManagingRepair = "Yes";
            }
            if (strPoliceInvolved.toLowerCase().contains("n") || strPoliceInvolved.toLowerCase().contains("false")) {
                strPoliceInvolved = "No";
            } else if (strPoliceInvolved.toLowerCase().contains("y") || strPoliceInvolved.toLowerCase().contains("true")) {
                strPoliceInvolved = "Yes";
            }

            sp_BulkUpload_Login(strDefendantInsurer, strClaimantClaimNumber);
            System.out.println("Selecting Claim Number " + strClaimantClaimNumber);

            sp_BulkUpload_ClaimSummaryFields(strClaimantInsurer, strClaimantClaimNumber, strClaimantWorkgroup, strClaimantHandler, strClaimantPolicyNumber, strClaimantTitle,
                    strClaimantFirstName, strClaimantLastName, strDefendantInsurer, strDefendantClaimNumber, strDefendantWorkgroup, strDefendantHandler, strDefendantPolicyNumber,
                    strDefendantTitle, strDefendantFirstName, strDefendantLastName, strClaimantAddress1, strClaimantAddress2, strClaimantTown, strClaimantCounty, strClaimantPostcode,
                    strDefendantAddress1, strDefendantAddress2, strDefendantTown, strDefendantCounty, strDefendantPostcode);

            sp_BulkUpload_IncidentDetailsFields(strIncidentLocation, strIncidentDescription, strDamageDescription, strDateOfIncident, strTotalLoss, strCarUsable, strManagingRepair,
                    strHireType, strClaimantVehicleMake, strClaimantVehicleModel, strClaimantVehicleVRN, strClaimantDriverTitle, strClaimantDriverFirstName, strClaimantDriverLastName,
                    strDefendantVehicleMake, strDefendantVehicleModel, strDefendantVehicleVRN, strDefendantDriverTitle, strDefendantDriverFirstName, strDefendantDriverLastName);

            sp_BulkUpload_PoliceDetailsFields(strPoliceInvolved, strCrimeRefNo, strPoliceOfficerName, strPoliceOfficerID, strStationAddress1, strStationAddress2, strStationTown,
                    strStationCounty, strStationPostcode);

            sp_BulkUpload_WitnessDetailsFields(strWitnessName, strWitnessAddress1, strWitnessAddress2, strWitnessTown, strWitnessCounty, strWitnessPostcode, strWitnessStatement);

            sp_Logout();
        }
        return true;
    }

    public Boolean sp_BulkUpload_Login(String strDefendantInsurer, String strClaimantClaimNumber) {

        if (strDefendantInsurer.equals("") || strDefendantInsurer.equals("null")) {
            System.out.println("ERROR - Claim '" + strClaimantClaimNumber + "' skipped as no username or/and password were provided.");
            return false;
        }

        String strUserOrg = strDefendantInsurer.toLowerCase();
        sp_Login("user1@" + strUserOrg + ".com", "password");
        sp_SelectMenuQueue("Defendant", "Assign Workgroup");
        sp_SelectClaim(strClaimantClaimNumber);

        return true;
    }

    public Boolean sp_BulkUpload_ClaimSummaryFields(String strClaimantInsurer, String strClaimantClaimNumber, String strClaimantWorkgroup, String strClaimantHandler, String strClaimantPolicyNumber, String strClaimantTitle,
            String strClaimantFirstName, String strClaimantLastName, String strDefendantInsurer, String strDefendantClaimNumber, String strDefendantWorkgroup, String strDefendantHandler, String strDefendantPolicyNumber,
            String strDefendantTitle, String strDefendantFirstName, String strDefendantLastName, String strClaimantAddress1, String strClaimantAddress2, String strClaimantTown, String strClaimantCounty, String strClaimantPostcode,
            String strDefendantAddress1, String strDefendantAddress2, String strDefendantTown, String strDefendantCounty, String strDefendantPostcode) {

        automationObject.genfunc_waitForAngular();
        //Claimant Insurer Details - All fields
        automationObject.genfunc_GetText("//*[@id=\"claimantInsurer\"]", strClaimantInsurer);
        automationObject.genfunc_GetText("//*[@id=\"claimantClaimNumber\"]", strClaimantClaimNumber);
        automationObject.genfunc_GetText("//*[@id=\"claimantWorkgroup\"]", strClaimantWorkgroup);
        automationObject.genfunc_GetText("//*[@id=\"claimantClaimHandler\"]", strClaimantHandler);
        automationObject.genfunc_GetText("//*[@id=\"claimantPolicyNumber\"]", strClaimantPolicyNumber);
        automationObject.genfunc_GetText("//*[@id=\"claimantTitle\"]", strClaimantTitle);
        automationObject.genfunc_GetText("//*[@id=\"claimantFirstName\"]", strClaimantFirstName);
        automationObject.genfunc_GetText("//*[@id=\"claimantLastName\"]", strClaimantLastName);

        //Defendant Insurer Details - All fields
        automationObject.genfunc_GetText("//*[@id=\"defendantInsurer\"]", strDefendantInsurer);
        automationObject.genfunc_GetText("//*[@id=\"defendantClaimNumber\"]", strDefendantClaimNumber);
        automationObject.genfunc_GetText("//*[@id=\"defendantWorkgroup\"]", strDefendantWorkgroup);
        automationObject.genfunc_GetText("//*[@id=\"defendantClaimHandler\"]", strDefendantHandler);
        automationObject.genfunc_GetText("//*[@id=\"defendantPolicyNumber\"]", strDefendantPolicyNumber);
        automationObject.genfunc_GetText("//*[@id=\"defendantTitle\"]", strDefendantTitle);
        automationObject.genfunc_GetText("//*[@id=\"defendantFirstName\"]", strDefendantFirstName);
        automationObject.genfunc_GetText("//*[@id=\"defendantLastName\"]", strDefendantLastName);

        //Address fields Claimant/Defendant
        //Toggle Expandable fields
        if (automationObject.genfunc_isElementPresent("//*[@id=\"claimantAddress1\"]") == false) {
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div/data-claim-summary/div/div[2]/div/div/div[1]/data-claimant-insurer-details/div/div/div[2]/div/div/div[10]/div/div/div[1]/p");
            automationObject.genfunc_waitForAngular();
        }
        if (automationObject.genfunc_isElementPresent("//*[@id=\"defendantAddress1\"]") == false) {
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div/data-claim-summary/div/div[2]/div/div/div[2]/data-defendant-insurer-details/div/div/div[2]/div/div/div[10]/div/div/div[1]/p");
            automationObject.genfunc_waitForAngular();
        }
        automationObject.genfunc_GetText("//*[@id=\"claimantAddress1\"]", strClaimantAddress1);
        automationObject.genfunc_GetText("//*[@id=\"claimantAddress2\"]", strClaimantAddress2);
        automationObject.genfunc_GetText("//*[@id=\"claimantTown\"]", strClaimantTown);
        automationObject.genfunc_GetText("//*[@id=\"claimantCounty\"]", strClaimantCounty);
        automationObject.genfunc_GetText("//*[@id=\"claimantPostcode\"]", strClaimantPostcode);

        automationObject.genfunc_GetText("//*[@id=\"defendantAddress1\"]", strDefendantAddress1);
        automationObject.genfunc_GetText("//*[@id=\"defendantAddress2\"]", strDefendantAddress2);
        automationObject.genfunc_GetText("//*[@id=\"defendantTown\"]", strDefendantTown);
        automationObject.genfunc_GetText("//*[@id=\"defendantCounty\"]", strDefendantCounty);
        automationObject.genfunc_GetText("//*[@id=\"defendantPostcode\"]", strDefendantPostcode);

        return true;
    }

    public Boolean sp_BulkUpload_IncidentDetailsFields(String strIncidentLocation, String strIncidentDescription, String strDamageDescription, String strDateOfIncident, String strTotalLoss, String strCarUsable, String strManagingRepair,
            String strHireType, String strClaimantVehicleMake, String strClaimantVehicleModel, String strClaimantVehicleVRN, String strClaimantDriverTitle, String strClaimantDriverFirstName, String strClaimantDriverLastName,
            String strDefendantVehicleMake, String strDefendantVehicleModel, String strDefendantVehicleVRN, String strDefendantDriverTitle, String strDefendantDriverFirstName, String strDefendantDriverLastName) {

        automationObject.genfunc_waitForAngular();
        //IncidentDetails
        automationObject.genfunc_GetText("//*[@id=\"incidentLocation\"]", strIncidentLocation);
        automationObject.genfunc_GetText("//*[@id=\"incidentDescription\"]", strIncidentDescription);
        automationObject.genfunc_GetText("//*[@id=\"damageDescription\"]", strDamageDescription);
        automationObject.genfunc_GetText("//*[@id=\"dateOfIncident\"]/div/input", strDateOfIncident);
        automationObject.genfunc_GetText("//*[@id=\"totalLoss\"]", strTotalLoss);
        automationObject.genfunc_GetText("//*[@id=\"carUsable\"]", strCarUsable);
        automationObject.genfunc_GetText("//*[@id=\"managingRepair\"]", strManagingRepair);
        automationObject.genfunc_GetText("//*[@id=\"hireType\"]", strHireType);

        automationObject.genfunc_GetText("//*[@id=\"claimantVehicleMake\"]", strClaimantVehicleMake);
        automationObject.genfunc_GetText("//*[@id=\"claimantVehicleModel\"]", strClaimantVehicleModel);
        automationObject.genfunc_GetText("//*[@id=\"claimantVehicleVRN\"]", strClaimantVehicleVRN);
        automationObject.genfunc_GetText("//*[@id=\"claimantDriverTitle\"]", strClaimantDriverTitle);
        automationObject.genfunc_GetText("//*[@id=\"claimantDriverFirstName\"]", strClaimantDriverFirstName);
        automationObject.genfunc_GetText("//*[@id=\"claimantDriverLastName\"]", strClaimantDriverLastName);

        automationObject.genfunc_GetText("//*[@id=\"defendantVehicleMake\"]", strDefendantVehicleMake);
        automationObject.genfunc_GetText("//*[@id=\"defendantVehicleModel\"]", strDefendantVehicleModel);
        automationObject.genfunc_GetText("//*[@id=\"defendantVehicleVRN\"]", strDefendantVehicleVRN);
        automationObject.genfunc_GetText("//*[@id=\"defendantDriverTitle\"]", strDefendantDriverTitle);
        automationObject.genfunc_GetText("//*[@id=\"defendantDriverFirstName\"]", strDefendantDriverFirstName);
        automationObject.genfunc_GetText("//*[@id=\"defendantDriverLastName\"]", strDefendantDriverLastName);

        return true;
    }

    public Boolean sp_BulkUpload_PoliceDetailsFields(String strPoliceInvolved, String strCrimeRefNo, String strPoliceOfficerName, String strPoliceOfficerID, String strStationAddress1, String strStationAddress2, String strStationTown,
            String strStationCounty, String strStationPostcode) {

        automationObject.genfunc_waitForAngular();

        automationObject.genfunc_GetText("//*[@id=\"policeInvolved\"]", strPoliceInvolved);
        automationObject.genfunc_GetText("//*[@id=\"crimRefNo\"]", strCrimeRefNo);
        automationObject.genfunc_GetText("//*[@id=\"policeOfficerName\"]", strPoliceOfficerName);
        automationObject.genfunc_GetText("//*[@id=\"policeOfficerID\"]", strPoliceOfficerID);
        automationObject.genfunc_GetText("//*[@id=\"stationAddressOne\"]", strStationAddress1);
        automationObject.genfunc_GetText("//*[@id=\"stationAddressTwo\"]", strStationAddress2);
        automationObject.genfunc_GetText("//*[@id=\"stationTown\"]", strStationTown);
        automationObject.genfunc_GetText("//*[@id=\"stationCounty\"]", strStationCounty);
        automationObject.genfunc_GetText("//*[@id=\"stationPostcode\"]", strStationPostcode);

        return true;
    }

    public Boolean sp_BulkUpload_WitnessDetailsFields(String strWitnessName, String strWitnessAddress1, String strWitnessAddress2, String strWitnessTown, String strWitnessCounty, String strWitnessPostcode, String strWitnessStatement) {

        automationObject.genfunc_waitForAngular();

        automationObject.genfunc_GetText("//*[@id=\"witnessName\"]", strWitnessName);
        automationObject.genfunc_GetText("//*[@id=\"witnessAddressOne\"]", strWitnessAddress1);
        automationObject.genfunc_GetText("//*[@id=\"witnessAddressTwo\"]", strWitnessAddress2);
        automationObject.genfunc_GetText("//*[@id=\"witnessTown\"]", strWitnessTown);
        automationObject.genfunc_GetText("//*[@id=\"witnessCounty\"]", strWitnessCounty);
        automationObject.genfunc_GetText("//*[@id=\"witnessPostcode\"]", strWitnessPostcode);
        automationObject.genfunc_GetText("//*[@id=\"witnessStatement\"]", strWitnessStatement);

        return true;
    }

    /////////////////////////////////////////////
    ////            Subrogation Admin/User Methods
    /////////////////////////////////////////////
    public Boolean sp_Admin_CreateUsers(String DataSetName) {

        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        //To find the last row in the data set
        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        //Click Admin tab
        automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Admin\")]");
        automationObject.genfunc_waitForAngular();
        //Click User Admin sub tab
        automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[4]/ul/li[contains(.,\"User Admin\")]/a");
        automationObject.genfunc_waitForAngular();

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {
            String strOrganisation = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");
            String strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strEmailAddress = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strFirstName = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strSurname = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strWorkgroup = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strRole;
            Integer iCurrentRolesColumn = 7;
            try {
                //Select the organisation to add the user to
                automationObject.genfunc_Click("//*[@id=\"organisation\"]/option[contains(.,\"" + strOrganisation + "\")]");
                automationObject.genfunc_waitForAngular();
                //Click the "Add New User" button
                automationObject.genfunc_Click("//button[contains(.,\"Add New User\")]");
                automationObject.genfunc_waitForAngular();
                //Fill in new user details
                automationObject.genfunc_SetText("//*[@id=\"userName\"]", strUsername);
                automationObject.genfunc_SetText("//*[@id=\"email\"]", strEmailAddress);
                automationObject.genfunc_SetText("//*[@id=\"firstName\"]", strFirstName);
                automationObject.genfunc_SetText("//*[@id=\"lastName\"]", strSurname);
                //Select the 'Active' tickbox
                automationObject.genfunc_Click("//*[@id=\"userDetailsTabId\"]/div[2]/div/div/div[1]/div/div/div[2]/div/div/div/div[7]/div/div/div/div/label/input");
                //Click the Create User button
                automationObject.genfunc_Click("//*[@id=\"userDetailsTabId\"]/div[3]/a[contains(.,\"Create\")]");
                automationObject.genfunc_waitForAngular();
                //If there is any validation messages print error and move on to creating next user
                if (automationObject.genfunc_isElementPresent("//*[@id=\"userDetailsTabId\"]/div[2]/div/div/div[2]/div/div") == true) {
                    System.out.println("ERROR - " + strUsername + " not created.");
                    //Navigate back to User Admin tab
                    automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"User Admin\")]");
                    automationObject.genfunc_waitForAngular();
                    continue;
                }
                //Navigate to roles tab
                automationObject.genfunc_Click("//*[@id=\"userTabsId\"]/a/tab-heading[contains(.,\"Roles\")]");
                //Add roles
                iCurrentRolesColumn = 7;
                while (!getDataSet("$" + DataSetName + "(" + iCurrentRow + "," + iCurrentRolesColumn + ")").equalsIgnoreCase("")) {
                    strRole = getDataSet("$CreateUsersDataSet(" + iCurrentRow + "," + iCurrentRolesColumn + ")");
                    automationObject.genfunc_Click("//td[.=\"" + strRole + "\"]");
                    //automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[.=\"" + strRole + "\"]");
                    iCurrentRolesColumn = iCurrentRolesColumn + 1;
                }
                //Move roles over to Assigned Roles listbox
                automationObject.genfunc_Click("//*[@id=\"userRolesTabId\"]/div[2]/div/div/div[2]/div[1]/span/i");
                //Save roles
                automationObject.genfunc_Click("//*[@id=\"userRolesTabId\"]/div[3]/a[contains(.,\"Save\")]");
                automationObject.genfunc_waitForAngular();
                //Add user to workgroup (if applicable)
                if (!strWorkgroup.equalsIgnoreCase("")) {
                    //Navigate back to Workgroups tab
                    automationObject.genfunc_Click("//*[@id=\"userTabsId\"]/a/tab-heading[contains(.,\"Workgroups\")]");
                    //Clcik on workgroup
                    automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strWorkgroup + "\")]");
                    //Move workgroup over to Assigned Workgroup listbox
                    automationObject.genfunc_Click("//*[@id=\"userWorkgroupsTabId\"]/div[2]/div/div/div[2]/div[1]/span/i");
                    //Save workgroups
                    automationObject.genfunc_Click("//*[@id=\"userWorkgroupsTabId\"]/div[3]/a[contains(.,\"Save\")]");
                    automationObject.genfunc_waitForAngular();
                }
                //Close the tab
                automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"" + strUsername + "\")]/i[2]");
                automationObject.genfunc_waitForAngular();
                //Navigate back to User Admin tab
                automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"User Admin\")]");
                automationObject.genfunc_waitForAngular();
            } catch (Exception e) {
                System.out.println("ERROR - " + strUsername + " not created.");
                //Navigate back to User Admin tab
                automationObject.genfunc_RefreshPage();
                automationObject.genfunc_waitForAngular();
                if (automationObject.genfunc_isElementPresent("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"" + strUsername + "\")]/i[2]") == true) {
                    //Close the tab if present
                    automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"" + strUsername + "\")]/i[2]");
                    automationObject.genfunc_waitForAngular();
                }
                automationObject.genfunc_Click("//*[@id=\"claimantTabsId\"]/a/tab-heading[contains(.,\"User Admin\")]");
                automationObject.genfunc_waitForAngular();
            }
        }

        return true;
    }

    public Boolean sp_Admin_CheckPermissions(String DataSetName) {
        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        //To find the last row in the data set
        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {
            String strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");
            String strPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");

            String strWorkQueuesValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strClaimantQueuesValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strClaimant_DC_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strClaimant_AW_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strClaimant_AO_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
            String strClaimant_RC_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
            String strClaimant_AI_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
            String strClaimant_DI_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strClaimant_QI_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strClaimant_PI_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");

            String strDefendantQueuesValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strDefendant_AW_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");
            String strDefendant_AO_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)");
            String strDefendant_CR_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",16)");
            String strDefendant_AA_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",17)");
            String strDefendant_PQ_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",18)");
            String strDefendant_PL_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",19)");
            String strDefendant_PP_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",20)");

            String strSearchValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",21)");
            String strNettingValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",22)");
            String strAdminValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",23)");
            String strAdmin_Organisation_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",24)");
            String strAdmin_User_Value = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",25)");
            String strUploadValue = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",26)");

            System.out.println("    Checking " + strUsername + " permissions.");

            if (sp_Login(strUsername, strPassword) == false) {
                System.out.println("ERROR - Failed to login.");
                continue;
            }

            //Work Queues
            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]", strWorkQueuesValue, "Work Queues Menu Option");
            if (automationObject.genfunc_isElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]") == true) {
                automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]");
                automationObject.genfunc_waitForAngular();
                //Claimant Work Queues
                sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]", strClaimantQueuesValue, "Claimant Queues Menu Option");
                if (automationObject.genfunc_isElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]") == true) {
                    automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Claimant Queues\")]");
                    automationObject.genfunc_waitForAngular();
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Draft Claims\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_DC_Value, "Claimant Queue: Draft Claims");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Assign Workgroup\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_AW_Value, "Claimant Queue: Assign Workgroup");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Assign Owner\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_AO_Value, "Claimant Queue: Assign Owner");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Rejected Claims\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_RC_Value, "Claimant Queue: Reject Claims");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Awaiting Invoice\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_AI_Value, "Claimant Queue: Awaiting Invoice");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Draft Invoices\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_DI_Value, "Claimant Queue: Draft Invoices");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Queried Invoices\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_QI_Value, "Claimant Queue: Queried Invoices");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Paid Invoices\")]/div[1]/div/div/div[2]/div[1]/div", strClaimant_PI_Value, "Claimant Queue: Paid Invoices");
                }
                //Defendant Work Queues
                sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]", strDefendantQueuesValue, "Defendant Queues Menu Option");
                if (automationObject.genfunc_isElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]") == true) {
                    automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Work Queues\")]/ul/li[contains(.,\"Defendant Queues\")]");
                    automationObject.genfunc_waitForAngular();
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Assign Workgroup\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_AW_Value, "Defendant Queue: Assign Workgroup");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Assign Owner\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_AO_Value, "Defendant Queue: Assign Owner");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Claims to be Registered\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_CR_Value, "Defendant Queue: Claims to be Registered");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Awaiting Acknowledgement\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_AA_Value, "Defendant Queue: Awaiting Acknowledgement");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Pending Quantum\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_PQ_Value, "Defendant Queue: Pending Quantum");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Pending Liability\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_PL_Value, "Defendant Queue: Pending Liability");
                    sp_IsElementPresent("//*[@id=\"queuesId\"]/div[contains(.,\"Pending Payment\")]/div[1]/div/div/div[2]/div[1]/div", strDefendant_PP_Value, "Defendant Queue: Pending Payment");
                }
            }

            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Search\")]", strSearchValue, "Search Menu Option");
            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Netting\")]", strNettingValue, "Netting Menu Option");
            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Admin\")]", strAdminValue, "Admin Menu Option");
            if (automationObject.genfunc_isElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Admin\")]") == true) {
                automationObject.genfunc_Click("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Admin\")]");
                automationObject.genfunc_waitForAngular();
                sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Organisation Admin\")]/ul/li[contains(.,\"Organisation Admin\")]", strAdmin_Organisation_Value, "Organisation Admin Menu Option");
                sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"User Admin\")]/ul/li[contains(.,\"User Admin\")]", strAdmin_User_Value, "User Admin Menu Option");
            }
            sp_IsElementPresent("//*[@id=\"sidebar-wrapper\"]/ul/li[contains(.,\"Upload\")]", strUploadValue, "Upload Menu Option");
            sp_Logout();
        }
        return true;
    }

    /////////////////////////////////////////////
    ////            Subrogation Tasks Methods
    /////////////////////////////////////////////
    public Boolean sp_Tasks_DataSetRunner(String DataSetName) {

        if (DataSetName.equalsIgnoreCase("")) {
            return false;
        }

        //To find the last row in the data set
        int iLastRow = 0;
        while (getDataSet("$" + DataSetName + "(" + iLastRow + ",1)").equalsIgnoreCase("") == false) {
            iLastRow++;
        }

        for (int iCurrentRow = 0; iCurrentRow < iLastRow; iCurrentRow++) {

            //Get values from data set      
            String strUsername = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",1)");
            String strPassword = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",2)");
            String strTaskType = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",3)");
            String strTaskDescription = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",4)");
            String strClaimNumber = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",5)");
            String strDueDays = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",6)");
            String strAction = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",7)");
            String strActionInput1 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",8)");
            String strActionInput2 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",9)");
            String strActionInput3 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",10)");
            String strActionInput4 = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",11)");
            String strActionPanelText = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",12)");
            String strEvent = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",13)");
            String strCompletionStatus = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",14)");
            String strScreenshotLocation = getDataSet("$" + DataSetName + "(" + iCurrentRow + ",15)");

            //Convert due days to an integer
            int iDueDays = 0;
            if (!strDueDays.equalsIgnoreCase("")) {
                iDueDays = Integer.parseInt(strDueDays);
            }

            automationObject.genfunc_waitForAngular();

            //Login
            if (sp_Login(strUsername, strPassword) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Login)");
                return false;
            }

            //Navigate to tasks page or create claim
            if (sp_Tasks_Navigate(strTaskDescription, strClaimNumber, strAction, strActionInput1, strActionInput2, strActionInput3,
                    strActionInput4, strActionPanelText, strEvent, strScreenshotLocation) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Tasks_Navigate)");
                return false;
            }

            //Find claim,check row data, and open the task
            if (sp_Tasks_SelectTask(strTaskType, strTaskDescription, strClaimNumber, iDueDays, strAction, strScreenshotLocation) == false) {
                System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_Tasks_SelectTask)");
                return false;
            }

            if (!strTaskDescription.equalsIgnoreCase("None")) { //skips performing the action if the Task Description is equal to None i.e. there is no task
                //Perform action and check it was successful
                if (sp_PerformAction(strClaimNumber, strAction, strActionInput1, strActionInput2, strActionInput3,
                        strActionInput4, strActionPanelText, strEvent, strScreenshotLocation) == false) {
                    //if action is not completed successfully skip checking queues for this claim.
                    System.out.println("Failed - Scenario '" + DataSetName + "' has been skipped. (sp_PerformAction)");
                    return false;
                }
            }

            //Check task has been completed
            sp_Tasks_CheckCompleted(strTaskDescription, strClaimNumber, strCompletionStatus, strAction, strScreenshotLocation);

            //Logout
            sp_Logout();
        }

        return true;
    }

    public Boolean sp_Tasks_Navigate(String strTaskDescription, String strClaimNumber, String strAction, String strActionInput1, String strActionInput2,
            String strActionInput3, String strActionInput4, String strActionPanelText, String strEvent, String strScreenshotLocation) {

        //Open Claim or create claim if the current queue is set to "None"
        if (!strTaskDescription.equalsIgnoreCase("None")) {
            //navigate to Tasks page
            if (sp_SelectMenuQueue("Tasks", "") == false) {
                sp_Logout();
                return false;
            }

        } else {
            sp_SelectMenuQueue("Claimant", "Draft Claims");
            //create draft button
            automationObject.genfunc_waitForAngular();
            automationObject.genfunc_Click("//*[@id=\"tabs-page-content-area\"]/div/div[2]/div/div/button");
            automationObject.genfunc_waitForAngular();
            //fill inputs
            sp_PopulateNewDraft(strClaimNumber, strActionInput1, strActionInput2, strActionInput3, strActionInput4);
            //submit claim
            if (sp_PerformAction(strClaimNumber, strAction, strActionInput1, strActionInput2,
                    strActionInput3, strActionInput4, strActionPanelText, strEvent, strScreenshotLocation) == false) {
                return false;
            }
        }
        automationObject.genfunc_waitForAngular();
        return true;
    }

    public Boolean sp_Tasks_SelectTask(String strTaskType, String strTaskDescription, String strClaimNumber, Integer iDueDays, String strAction, String strScreenshotLocation) {

        if (strTaskDescription.equalsIgnoreCase("None")) {
            return true;
        }

        int iPage = 1;
        //select the dropdown to display 100 results per page
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/label/select/option[contains(.,\"100\")]");
        automationObject.genfunc_waitForAngular();

        //Table Columns
        int iOpenButtonCol = 1;
        int iTypeCol = 2;
        int iDescriptionCol = 3;
        int iClaimNumberCol = 4;
        int iCreatedDateCol = 8;
        int iDueDateCol = 9;

        //Xpath contains task description and claim number
        String strTaskXPath = "//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strTaskDescription + "\") and contains(.,\"" + strClaimNumber + "\")]";

        //Find the created and due dates
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //Set date format
        Calendar cal = Calendar.getInstance(); //Set the calandar date to todays date
        Date CurrentDate = cal.getTime(); //Get the calander date
        cal.add(Calendar.DATE, iDueDays); //Add 'iDueDays' days to the calander date
        Date DueDate = cal.getTime(); //Get the calander date
        String strCreatedDate = sdf.format(CurrentDate); //Change the date to the correct format
        String strDueDate = sdf.format(DueDate); //Change the date to the correct format

        //Cycle through table pages
        while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
            sp_SelectTablePage(iPage);
            automationObject.genfunc_waitForAngular();
            //Search the column for the specific claim number
            if (automationObject.genfunc_isElementPresent(strTaskXPath + "/td[" + iOpenButtonCol + "]/i") == true) {

                //Check data values
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iTypeCol + "]", strTaskType) == false) {
                    System.out.println("Failed - Task Type is not as expected. See screenshot for more details.");
                }
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iDescriptionCol + "]", strTaskDescription) == false) {
                    System.out.println("Failed - Task Description is not as expected. See screenshot for more details.");
                }
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iClaimNumberCol + "]", strClaimNumber) == false) {
                    System.out.println("Failed - Task Claim Number is not as expected. See screenshot for more details.");
                }
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCreatedDateCol + "]", strCreatedDate) == false) {
                    System.out.println("Failed - Task Created Date is not as expected. See screenshot for more details.");
                }
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iDueDateCol + "]", strDueDate) == false) {
                    System.out.println("Failed - Task Due Date is not as expected. See screenshot for more details.");
                }

                //Take screenshot if any values failed
                if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iTypeCol + "]", strTaskType) == false
                        || automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iDescriptionCol + "]", strTaskDescription) == false
                        || automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iClaimNumberCol + "]", strClaimNumber) == false
                        || automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCreatedDateCol + "]", strCreatedDate) == false
                        || automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iDueDateCol + "]", strDueDate) == false) {
                    automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimNumber + " - " + strAction + " - TasksTableData.png");
                }

                //Open the claim 
                automationObject.genfunc_Click(strTaskXPath + "/td[" + iOpenButtonCol + "]/i");
                return true;
            }
            iPage = iPage + 1;
        }
        System.out.println("ERROR - Could not select claim '" + strClaimNumber + "' with description '" + strTaskDescription + "'.");
        sp_Logout();
        return false;
    }

    public Boolean sp_Tasks_CheckCompleted(String strTaskDescription, String strClaimNumber, String strCompletionStatus, String strAction, String strScreenshotLocation) {

        if (strTaskDescription.equalsIgnoreCase("None")) {
            return true;
        }

        System.out.println("    Checking task with claim number '" + strClaimNumber + "' and task description '" + strTaskDescription + "' has been completed.");
        int iPage = 1;
        //select the dropdown to display 100 results per page
        automationObject.genfunc_waitForAngular();
        automationObject.genfunc_Click("//*[starts-with(@id,\"DataTables_Table\")]/label/select/option[contains(.,\"100\")]");
        automationObject.genfunc_waitForAngular();

        //Table Columns
        int iOpenButtonCol = 1;
        int iCompletedDateCol = 11;
        int iCompletionStatusCol = 13;

        //Xpath contains task description and claim number
        String strTaskXPath = "//*[starts-with(@id,\"DataTables_Table\")]/tbody/tr[contains(.,\"" + strTaskDescription + "\") and contains(.,\"" + strClaimNumber + "\")]";

        //Display completed tasks as well
        automationObject.genfunc_Click("//button[contains(.,\"Show Completed Tasks\")]");

        //Find the created and due dates
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //Set date format
        Calendar cal = Calendar.getInstance(); //Set the calandar date to todays date
        Date CurrentDate = cal.getTime(); //Get the calander date
        //   cal.add(Calendar.DATE, iDueDays); //Add 'iDueDays' days to the calander date
        //   Date DueDate = cal.getTime(); //Get the calander date
        String strCreatedDate = sdf.format(CurrentDate); //Change the date to the correct format
        //   String strDueDate = sdf.format(DueDate); //Change the date to the correct format

        if (strCompletionStatus.equalsIgnoreCase("None")) {
            //Cycle through table pages
            while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
                sp_SelectTablePage(iPage);
                automationObject.genfunc_waitForAngular();
                //Search the column for the specific claim number and task description
                if (automationObject.genfunc_isElementPresent(strTaskXPath + "/td[" + iOpenButtonCol + "]/i") == true) {

                    if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCompletedDateCol + "]", "") == false
                            || automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCompletionStatusCol + "]", "") == false) {
                        System.out.println("Failed - Task has been moved to completed. See screenshot for more details.");
                        automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimNumber + " - " + strAction + " - TaskCompletion.png");
                    }
                    break;
                }
                iPage = iPage + 1;
            }
        } else {
            //Cycle through table pages
            while (automationObject.genfunc_isElementPresent("//*[starts-with(@id,\"DataTables_Table\")]/span/a[" + iPage + "]")) {
                sp_SelectTablePage(iPage);
                automationObject.genfunc_waitForAngular();
                //Search the column for the specific claim number and task description
                if (automationObject.genfunc_isElementPresent(strTaskXPath + "/td[" + iOpenButtonCol + "]/i") == true) {

                    if (automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCompletedDateCol + "]", strCreatedDate) == false
                            || automationObject.genfunc_GetTextNoOutput(strTaskXPath + "/td[" + iCompletionStatusCol + "]", strCompletionStatus) == false) {
                        System.out.println("Failed - Task has NOT been moved to completed. See screenshot for more details.");
                        automationObject.genfunc_TakeScreenshot(strScreenshotLocation + strClaimNumber + " - " + strAction + " - TaskCompletion.png");
                    }
                    break;
                }
                iPage = iPage + 1;
            }

        }
        return true;
    }

}
