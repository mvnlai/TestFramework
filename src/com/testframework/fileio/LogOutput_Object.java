package com.testframework.fileio;

public class LogOutput_Object {

    private int iMessageNumber;
    private String strMessage;
    private String strType;
    private String strFormat;

    public LogOutput_Object(int MessageNumber, String Message, String Type, String Format) {
        iMessageNumber = MessageNumber;
        strMessage = Message;
        strType = Type;
        strFormat = Format;
    }

    public String GetMessage() {
        return strMessage;
    }

    public String GetType() {
        return strType;
    }

    public String GetFormat() {
        return strFormat;
    }
}
