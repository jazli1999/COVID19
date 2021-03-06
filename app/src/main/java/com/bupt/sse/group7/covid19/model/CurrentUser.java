package com.bupt.sse.group7.covid19.model;

/**
 * 当前登录用户
 * label取值范围 {"visitor", "hospital", "patient"}
 */
public class CurrentUser {
    private static String label;
    private static int id;

    static {
        label = "visitor";
    }

    public static String getLabel() {
        return label;
    }

    public static void setLabel(String newLabel) {
        label = newLabel;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int newId) {
        id = newId;
    }
}
