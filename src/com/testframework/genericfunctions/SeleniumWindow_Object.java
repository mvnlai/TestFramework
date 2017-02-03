package com.testframework.genericfunctions;

public class SeleniumWindow_Object {

    public String WindowID;
    public String WindowTitle;

    public SeleniumWindow_Object(String ID, String Title) {
        WindowID = ID;
        WindowTitle = Title;
    }

    @Override
    public String toString() {
        return "WindowID - '" + WindowID + "'. WindowTitle - '" + WindowTitle + "'.";
    }
}
