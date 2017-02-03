package com.testframework.genericfunctions;

public class XPathName_Object {

    private String strPageName;
    private String strObjectName;
    private String strXPathName;

    public XPathName_Object(String PageName, String ObjectName, String XPathName) {
        strPageName = PageName;
        strObjectName = ObjectName;
        strXPathName = XPathName;
    }

    public String returnMatchingXPathName(String PageName, String ObjectName) {
        if (strPageName.equalsIgnoreCase(PageName) && strObjectName.equalsIgnoreCase(ObjectName)) {
            return strXPathName;
        }

        return "";
    }

    @Override
    public String toString() {
        return "Page Name - '" + strPageName + "'. Object Name - '" + strObjectName + "'. XPath Name - '" + strXPathName + "'.";
    }
}
