package com.example.educheck.Utils;

public class HttpUrl {
    private static String ip = "http://10.188.83.189:3000/api/";
    public static String UrlPostOnUniversity = ip + "addUser";
    public static String UrlGetUniversity = "";
    public static String UrlGetUniversities = ip + "allUni";
    public static String UrlPostAcademicBackground = ip + "pathStudent";
    public static String UrlGetAcademicBackground = ip + "getPaths";
    public static String UrlConnexion = ip + "findUser";
    public static String UrlForgetPassword = "";
}
