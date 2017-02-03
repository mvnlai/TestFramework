package com.testframework.genericfunctions;

public class DataSet_Object {

    private String strName;
    private int iRow;
    private String[] strData;

    public DataSet_Object(String Name, int Row, String[] Data) {
        strName = Name;
        iRow = Row;
        strData = Data;
    }

    public String[] getDataArray(String Name, int Row) {
        if (strName.equalsIgnoreCase(Name) && Row == iRow) {
            return strData;
        }

        return null;
    }

    public String getDataItem(String Name, int Row, int iPos) {
        if (strName.equalsIgnoreCase(Name)) {
            if (iRow == Row) {
                if (iPos < strData.length) {
                    return strData[iPos];
                }
            }
        }

        return "";
    }

    public int getRow(String Name) {
        if (strName.equalsIgnoreCase(Name)) {
            return iRow;
        }

        return -1;
    }

    @Override
    public String toString() {
        String strDataItems = "";

        for (int i = 0; i < strData.length; i++) {
            strDataItems = strDataItems + " # " + strData[i];
        }

        return "Name - '" + strName + "'. Row - '" + String.valueOf(iRow) + "'. Data Items - '" + strDataItems + "'.";
    }
}
