package com.testframework.runner;

import com.testframework.genericfunctions.*;
import com.testframework.fileio.*;

import java.util.regex.Pattern;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.ProcessBuilder.Redirect;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class Runner {

    private static HSSFSheet shAutomationControl;
    private static HSSFSheet shCurrentAutomationScript;
    private static HSSFRow row;
    private static ReadXLFile rXLFile;

    private static final String AUTOMATIONCONTROL_PATH = "C:\\Automation\\Subrogation\\AutomationControl.xls";

    private static final int AUTOMATIONCONTROL_COMMAND = 0;
    private static final int AUTOMATIONCONTROL_SPREADSHEET = 1;
    private static final int AUTOMATIONCONTROL_RUN = 2;
    private static final int AUTOMATIONCONTROL_ONFAIL = 3;

    private static final String AUTOMATIONCONTROL_COMMAND_LOADAUTOMATIONSCRIPT = "LoadAutomationScript";
    private static final String AUTOMATIONCONTROL_COMMAND_LOADMAPPINGFILE = "LoadElementMappingFile";
    private static final String AUTOMATIONCONTROL_COMMAND_APPENDMAPPINGFILE = "AppendElementMappingFile";
    private static final String AUTOMATIONCONTROL_COMMAND_LOADDATAMAPPINGFILE = "LoadDataMappingFile";
    private static final String AUTOMATIONCONTROL_COMMAND_APPENDDATAMAPPINGFILE = "AppendDataMappingFile";
    private static final String AUTOMATIONCONTROL_COMMAND_LOADDATASETFILE = "LoadDataSetFile";
    private static final String AUTOMATIONCONTROL_COMMAND_APPENDDATASETFILE = "AppendDataSetFile";
    private static final String AUTOMATIONCONTROL_COMMAND_SETAUTOMATONTOOL = "SetAutomationTool";
    private static final String AUTOMATIONCONTROL_COMMAND_EXECUTECMD = "ExecuteCMD";

    private static final int AUTOMATIONSCRIPT_PAGENAME = 0;
    private static final int AUTOMATIONSCRIPT_OBJECTNAME = 1;
    private static final int AUTOMATIONSCRIPT_XPATHNAME = 2;
    private static final int AUTOMATIONSCRIPT_COMMAND = 3;
    private static final int AUTOMATIONSCRIPT_OUTPUTFIELD = 4;
    private static final int AUTOMATIONSCRIPT_ONFAIL = 5;
    private static final int AUTOMATIONSCRIPT_INPUTPARAMETERSTART = 6;

    private static final String AUTOMATIONTOOL_SELENIUMCORE = "SELENIUM_CORE";
    private static final String AUTOMATIONTOOL_SELENIUMRC = "SELENIUM_RC";
    private static final String AUTOMATIONTOOL_SELENIUMWD = "SELENIUM_WD";
    private static final String AUTOMATIONTOOL_VAADINTESTBENCH = "VAADIN_TESTBENCH";
    private static final String AUTOMATIONTOOL_ENGINETESTER = "STS_ENGINETESTER";
    private static final String AUTOMATIONTOOL_IOSDRIVER = "IOSDRIVER";
    private static final String AUTOMATIONTOOL_ANDROIDDRIVER = "ANDROIDDRIVER";

    private static final String AUTOMATIONENVIRONMENT_VOX_RESTORE = "VOX_RESTORE";
    private static final String AUTOMATIONENVIRONMENT_VOX_BACKUP = "VOX_BACKUP";
    private static final String AUTOMATIONENVIRONMENT_START_APPIUM = "START_APPIUM";
    private static final String AUTOMATIONENVIRONMENT_STOP_APPIUM = "STOP_APPIUM";

    private static LogOutput logOutput = new LogOutput();
    private static GenericFunctions gf = new GenericFunctions();

    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        logOutput.loadMessages();
        gf.Set_AutomationTool(0);
        LoadAutomationControlSpreadsheet();
        RunAutomationControlSpreadsheet();

        return;
    }

    private static void LoadAutomationControlSpreadsheet() {
        ReadXLFile rXLFile = new ReadXLFile();

        shAutomationControl = rXLFile.LoadSpreadsheet(AUTOMATIONCONTROL_PATH);

        rXLFile = null;
        System.gc();
    }

    private static void RunAutomationControlSpreadsheet() throws IOException, InterruptedException, Exception {
        if (shAutomationControl == null) {
            logOutput.LogStep(1001, "", "", null);
            return;
        }

        rXLFile = new ReadXLFile();

        for (int i = 1; i <= shAutomationControl.getLastRowNum(); i++) {
            //HSSFRow  
            row = (HSSFRow) shAutomationControl.getRow(i);

            if (String.valueOf(row.getCell(AUTOMATIONCONTROL_RUN)).equals("Yes")) {
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_LOADAUTOMATIONSCRIPT)) {
                    AutomationControlCommand_RunAutomationScript(String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_LOADMAPPINGFILE)) {
                    AutomationControlCommand_LoadElementMapping(false, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_APPENDMAPPINGFILE)) {
                    AutomationControlCommand_LoadElementMapping(true, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_LOADDATAMAPPINGFILE)) {
                    AutomationControlCommand_LoadDataMapping(false, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_APPENDDATAMAPPINGFILE)) {
                    AutomationControlCommand_LoadDataMapping(true, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_LOADDATASETFILE)) {
                    AutomationControlCommand_LoadDataSetFile(false, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_APPENDDATASETFILE)) {
                    AutomationControlCommand_LoadDataSetFile(true, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_SETAUTOMATONTOOL)) {
                    AutomationControlCommand_SetAutomationTool(String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
                if (String.valueOf(row.getCell(AUTOMATIONCONTROL_COMMAND)).equals(AUTOMATIONCONTROL_COMMAND_EXECUTECMD)) {
                    AutomationControlCommand_ExecuteCMD(String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)));
                }
            }
        }
    }

    /**
     * ***********************************************************************************
     */
    /* Automation Control Command Functions  
/**************************************************************************************/
    private static void AutomationControlCommand_RunAutomationScript(String strData) throws Exception {
        shCurrentAutomationScript = rXLFile.LoadSpreadsheet(strData);
        if (shCurrentAutomationScript == null) {
            logOutput.LogStep(1002, "", "", null);
            return;
        }

        logOutput.LogStep(1004, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)), "", null);

        for (int i = 1; i <= shCurrentAutomationScript.getLastRowNum(); i++) {
            row = (HSSFRow) shCurrentAutomationScript.getRow(i);

            String[] InputCommands = AutomationScriptSpreadsheet_GetParameters(i, 0, AUTOMATIONSCRIPT_INPUTPARAMETERSTART);
            String[] InputParameters = AutomationScriptSpreadsheet_GetParameters(i, AUTOMATIONSCRIPT_INPUTPARAMETERSTART, row.getLastCellNum());

            String strXPathName = gf.Command_Get_XPathName(InputCommands[AUTOMATIONSCRIPT_PAGENAME], InputCommands[AUTOMATIONSCRIPT_OBJECTNAME], InputCommands[AUTOMATIONSCRIPT_XPATHNAME]);

            logOutput.LogStep(1009, InputCommands[AUTOMATIONSCRIPT_COMMAND], strXPathName, InputParameters);

            if (gf.Command_Call_Function(InputCommands[AUTOMATIONSCRIPT_COMMAND], InputParameters, strXPathName) == false) {
                logOutput.LogStep(1003, InputCommands[AUTOMATIONSCRIPT_COMMAND], strXPathName, InputParameters);
            }
        }
    }

    public static void AutomationControlCommand_LoadElementMapping(Boolean blnAppend, String strData) {
        shCurrentAutomationScript = rXLFile.LoadSpreadsheet(strData);
        if (shCurrentAutomationScript == null) {
            logOutput.LogStep(1002, "", "", null);
            return;
        }

        if (blnAppend == false) {
            gf.Command_XPathNameArray_Clear();
            logOutput.LogStep(1005, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)), "", null);
        } else {
            logOutput.LogStep(1006, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)), "", null);
        }

        for (int i = 1; i <= shCurrentAutomationScript.getLastRowNum(); i++) {
            row = (HSSFRow) shCurrentAutomationScript.getRow(i);

            String[] ObjectNames = AutomationScriptSpreadsheet_GetParameters(i, 0, row.getLastCellNum());
            gf.Command_XPathNameArray_AddRow(ObjectNames[AUTOMATIONSCRIPT_PAGENAME], ObjectNames[AUTOMATIONSCRIPT_OBJECTNAME], ObjectNames[AUTOMATIONSCRIPT_XPATHNAME]);

            //logOutput.LogStep(1010, "", "", ObjectNames);
        }
        System.out.println("Elements up to Row " + shCurrentAutomationScript.getLastRowNum() + " mapped");
    }

    public static void AutomationControlCommand_LoadDataMapping(Boolean blnAppend, String strData) {
        shCurrentAutomationScript = rXLFile.LoadSpreadsheet(strData);
        if (shCurrentAutomationScript == null) {
            logOutput.LogStep(1002, "", "", null);
            return;
        }

        if (blnAppend == false) {
            gf.clearDataMappingArray();
            logOutput.LogStep(1011, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)), "", null);
        } else {
            logOutput.LogStep(1012, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)), "", null);
        }

        for (int i = 1; i <= shCurrentAutomationScript.getLastRowNum(); i++) {
            row = (HSSFRow) shCurrentAutomationScript.getRow(i);

            String[] DataNames = AutomationScriptSpreadsheet_GetParameters(i, 0, row.getLastCellNum());
            gf.addDataMappingArray(DataNames[AUTOMATIONCONTROL_COMMAND], DataNames[AUTOMATIONCONTROL_SPREADSHEET]);
            // logOutput.LogStep(1013, "", "", DataNames);
        }
        System.out.println("Elements up to Row " + shCurrentAutomationScript.getLastRowNum() + " mapped");
    }

    public static void AutomationControlCommand_LoadDataSetFile(Boolean blnAppend, String strData) {
        shCurrentAutomationScript = rXLFile.LoadSpreadsheet(strData);
        if (shCurrentAutomationScript == null) {
            logOutput.LogStep(1002, "", "", null);
            return;
        }

        if (blnAppend == false) {
            gf.clearDataSetArray();
            logOutput.LogStep(1007, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)), "", null);
        } else {
            logOutput.LogStep(1014, String.valueOf(row.getCell(AUTOMATIONCONTROL_SPREADSHEET)), "", null);
        }

        for (int i = 1; i <= shCurrentAutomationScript.getLastRowNum(); i++) {
            row = (HSSFRow) shCurrentAutomationScript.getRow(i);
            String[] DataNames = AutomationScriptSpreadsheet_GetParameters(i, 0, row.getLastCellNum());
            gf.addDataSetArray(DataNames[AUTOMATIONCONTROL_COMMAND], i, DataNames);
            //logOutput.LogStep(1013, DataNames[AUTOMATIONCONTROL_COMMAND], Integer.toString(i), DataNames);
        }
        System.out.println("Elements up to Row " + shCurrentAutomationScript.getLastRowNum() + " mapped");

        return;
    }

    public static void AutomationControlCommand_SetAutomationTool(String strData) {
        logOutput.LogStep(1008, strData, "", null);
        int iAutomationTool = 0;

        switch (strData) {
            case AUTOMATIONTOOL_SELENIUMRC:
                iAutomationTool = 0;
                break;
            case AUTOMATIONTOOL_SELENIUMWD:
                iAutomationTool = 1;
                break;
            case AUTOMATIONTOOL_SELENIUMCORE:
                iAutomationTool = 2;
                break;
            case AUTOMATIONTOOL_VAADINTESTBENCH:
                iAutomationTool = 3;
                break;
            case AUTOMATIONTOOL_ENGINETESTER:
                iAutomationTool = 4;
                break;
            case AUTOMATIONTOOL_IOSDRIVER:
                iAutomationTool = 5;
                break;
            case AUTOMATIONTOOL_ANDROIDDRIVER:
                iAutomationTool = 6;
                break;
            default:
                iAutomationTool = 0;
                break;
        }

        gf.Set_AutomationTool(iAutomationTool);
    }

    private static String[] AutomationScriptSpreadsheet_GetParameters(int iRow, int iColumnStart, int iColumnEnd) {
        String[] strParameters = new String[(iColumnEnd - iColumnStart)];

        int iParameter = 0;
        for (int j = iColumnStart; j < iColumnEnd; j++) {
            strParameters[iParameter] = String.valueOf(row.getCell(j));
            iParameter++;
        }

        return strParameters;
    }

    //windows command only
    public static void AutomationControlCommand_StartServer() throws IOException, InterruptedException {
        String appiumLoc = "re";
        String appConfig = "--address 127.0.0.1 --port 4723 --app C:\\Users\\ukrimlai\\Downloads\\com.twitter.android-5.74.apk --app-activity com.twitter.android.LoginActivity --app-pkg com.twitter.android --pre-launch --platform-name Android --platform-version 22 --automation-name Appium --device-name \"BH900AVY16\" --log-no-color";

        logOutput.LogStep(1018, "", "", null);

        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "" + "nodejs appium.js" + "");
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.waitFor();
    }

    //windows command only
    public static void AutomationControlCommand_ExecuteCMD(String strFile) throws IOException, InterruptedException {
        logOutput.LogStep(1017, strFile, "", null);

        switch (strFile) {
            case AUTOMATIONENVIRONMENT_VOX_RESTORE:

                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "restore.bat");
                pb.directory(new File("C:\\automation\\bat\\"));

                File log = new File("C://logr.txt");
                pb.redirectErrorStream(true);
                pb.redirectOutput(Redirect.appendTo(log));
                Process p = pb.start();
                p.waitFor();

                break;

            case AUTOMATIONENVIRONMENT_VOX_BACKUP:

                ProcessBuilder pbb = new ProcessBuilder("cmd.exe", "/C", "backup.bat");
                pbb.directory(new File("C:\\automation\\bat\\"));

                File logs = new File("C://logb.txt");

                pbb.redirectErrorStream(true);
                pbb.redirectOutput(Redirect.appendTo(logs));
                Process b = pbb.start();
                b.waitFor();
                System.out.println("Completed VOX Backup");

                break;

            case AUTOMATIONENVIRONMENT_START_APPIUM:

                ProcessBuilder pbbb = new ProcessBuilder("cmd.exe", "/C", "runappium.bat");
                pbbb.directory(new File("C:\\Users\\ukrimlai\\"));

                File logss = new File("C://logb.txt");

                pbbb.redirectErrorStream(true);
                pbbb.redirectOutput(Redirect.appendTo(logss));
                Process bb = pbbb.start();
                bb.waitFor();
                System.out.println("APPIUM_STARTED");
                break;

            case AUTOMATIONENVIRONMENT_STOP_APPIUM:

                break;

            default:
                System.out.println("Default: No command given.");
                break;
        }

    }
}
