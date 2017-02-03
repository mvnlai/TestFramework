package com.testframework.genericfunctions;

public class DataMapping_Object {

    private String strName;
    private String strData;

    public DataMapping_Object(String Name, String Data) {
        strName = Name;
        strData = Data;
    }

    public String getData(String Name) {
        if (strName.equalsIgnoreCase(Name)) {
            return strData;
        }

        return "";
    }

    @Override
    public String toString() {
        return "Name - '" + strName + "'. Data - '" + strData + "'.";
    }
}
