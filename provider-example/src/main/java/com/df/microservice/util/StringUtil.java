package com.df.microservice.util;

/**
 * @author Liyang
 * @time 2020-5-18
 */
public class StringUtil {

    public static boolean isNullOrEmpty(String str) {
        return null == str || "".equals(str) || "null".equals(str);
    }

    public static boolean isNullOrEmpty(Object obj) {
        return null == obj || "".equals(obj);
    }
}