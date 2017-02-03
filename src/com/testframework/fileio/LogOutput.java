package com.testframework.fileio;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogOutput {

    private ArrayList alMessageArray = new ArrayList();
    private int iTotalCountMsg = 0;
    private int[] iCountMsg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String formatedDate = sdf.format(date);

    private String Results_Path = "C:\\" + formatedDate + "-Results.xls";

    public void loadMessages() {
        clearMessageArray();
        populateMessageArray();
    }

    private void clearMessageArray() {
        alMessageArray.clear();
    }

    private void populateMessageArray() {
        AddMessage(1000, "Unknown Error Code", "UNKNOWN", "");
        AddMessage(1001, "shAutomationControl Spreadsheet is Null.", "ERROR", "");
        AddMessage(1002, "shCurrentAutomationScript Spreadsheet is Null.", "ERROR", "");
        AddMessage(1003, "Command Failed to be executed as it returned 'false'", "ERROR", "");
        AddMessage(1004, "Loading Automation Test Script", "COMMAND", "     ");
        AddMessage(1005, "Loading Element Mapping File", "COMMAND", "     ");
        AddMessage(1006, "Appending Element Mapping File", "COMMAND", "     ");
        AddMessage(1007, "Loading Data Set File", "COMMAND", "     ");
        AddMessage(1008, "Setting Automation Tool", "COMMAND", "     ");
        AddMessage(1009, "Executing Test Step", "STEP", "          ");
        AddMessage(1010, "Loading Element Mapping Data Object", "STEP", "          ");
        AddMessage(1011, "Loading Data Mapping File", "COMMAND", "     ");
        AddMessage(1012, "Appending Data Mapping File", "COMMAND", "     ");
        AddMessage(1013, "Loading Data Mapping Data Object", "STEP", "          ");
        AddMessage(1014, "Appending Data Set File", "COMMAND", "     ");
        AddMessage(1015, "Executing Test Step", "STEP", "          ");
        AddMessage(1016, "Command returned 'false'", "ERROR", "");
    }

    public Boolean AddMessage(int iMessageNumber, String strMessage, String strType, String strFormat) {
        LogOutput_Object loObject = new LogOutput_Object(iMessageNumber, strMessage, strType, strFormat);
        alMessageArray.add(loObject);

        loObject = null;
        System.gc();

        return true;
    }

    public void LogStep(int iMessageNumber, String strParameter1, String strParameter2, String[] strParameters) {
        if (((iMessageNumber - 1000) < 0) || ((iMessageNumber - 1000) > alMessageArray.size())) {
            iMessageNumber = 1000;
        }
        LogOutput_Object loObject = (LogOutput_Object) alMessageArray.get(iMessageNumber - 1000);
        LogStep_SystemCmdPrompt(loObject.GetType(), loObject.GetMessage(), iMessageNumber, loObject.GetFormat(), strParameter1, strParameter2, strParameters);
        loObject = null;
        System.gc();
    }

    private void LogStep_SystemCmdPrompt(String strType, String strMessage, int iCode, String strFormat, String strParameter1, String strParameter2, String[] strParameters) {
        String strParametersData = "";
        if (strParameters != null) {
            for (int i = 0; i < strParameters.length; i++) {
                strParametersData += strParameters[i] + " # ";
            }
        }
        iTotalCountMsg = iTotalCountMsg + 1;

        for (int i = 0; i < 17; i++) {
            if (iCode == 1000 + i) {
                iCountMsg[i] = iCountMsg[i] + 1; //counts the total number of messages
            }
        }
        if (iCode == 1008) {
            WriteToExcel.CreateSpreadsheet(Results_Path); //creates the initial test report
        }

        if (iCode != 1015 & iCode != 1016) {
            String Output = strFormat + strType + ": " + strMessage + "(" + Integer.toString(iCode) + ") - Parameter(s):" + strParameter1 + " : " + strParameter2 + " Data Parameter(s): " + strParametersData;
            System.out.println(Output);
            WriteToExcel.UpdateSpreadsheet(iTotalCountMsg, iCountMsg, Output, Results_Path);  //updates the test report
        } else {
            String Output = strFormat + strType + ": " + strMessage + "(" + Integer.toString(iCode) + ") - Details:" + strParameter1 + " : " + strParameter2;
            System.out.println(Output);
            WriteToExcel.UpdateSpreadsheet(iTotalCountMsg, iCountMsg, Output, Results_Path); // updates the test report
        }
    }
}
