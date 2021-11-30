package com.surveyfiesta.mroc.constants;

public class DefaultValues {

    private static final String DEFAULT_HOST = "http://localhost:8080";
    private static final String BASE_URL = DEFAULT_HOST+"/SurveyFiesta";
    private static final String BASE_WEB_SERVICES = BASE_URL+ "/webresources";

    /**
     * Public Values
     */
    public static final String BASE_WEB_SOCKET = BASE_URL+"/chat";
    public static final String BASE_IMAGE_URL = BASE_URL + "/ImageServlet/";
    public static final String BASE_CHAT_URL = BASE_WEB_SERVICES+"/chat/";
    public static final String BASE_USERS_URL = BASE_WEB_SERVICES+"/users/";
    public static final String BASE_SHARE_URL = BASE_CHAT_URL+ "share/";
}
