package com.testframework.genericfunctions;

public class ElementMapping_Object {

    private String strPageName;
    private String strObjectName;
    private String strElement;

    public ElementMapping_Object(String PageName, String ObjectName, String Element) {
        strPageName = PageName;
        strObjectName = ObjectName;
        strElement = Element;
    }

    public String getElementName(String PageName, String ObjectName) {
        if (strPageName.equalsIgnoreCase(PageName) && strObjectName.equalsIgnoreCase(ObjectName)) {
            return strElement;
        }

        return "";
    }

    @Override
    public String toString() {
        return "Page Name - '" + strPageName + "'. Object Name - '" + strObjectName + "'. Element - '" + strElement + "'.";
    }
}
