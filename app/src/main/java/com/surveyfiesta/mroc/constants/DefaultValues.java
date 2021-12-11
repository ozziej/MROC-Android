package com.surveyfiesta.mroc.constants;

public class DefaultValues {
    private static final String DEFAULT_APPLICATION_PATH = "/SurveyFiesta";
    private static final String DEFAULT_URL_SCHEME = "http://";
    private static final String DEFAULT_WS_SCHEME = "ws://";
    private static final String DEFAULT_HOST = "localhost:8080";

    private static final String BASE_URL =  DEFAULT_URL_SCHEME + DEFAULT_HOST+ DEFAULT_APPLICATION_PATH;
    private static final String BASE_WEB_SOCKET_URL = DEFAULT_WS_SCHEME + DEFAULT_HOST+ DEFAULT_APPLICATION_PATH;
    private static final String BASE_WEB_SERVICES = BASE_URL+ "/webresources";

    /**
     * Public Values
     */
    public static final String BASE_WEB_SOCKET = BASE_WEB_SOCKET_URL+"/chat/";
    public static final String BASE_IMAGE_URL = BASE_URL + "/ImageServlet/";
    public static final String BASE_CHAT_URL = BASE_WEB_SERVICES+"/chat/";
    public static final String BASE_USERS_URL = BASE_WEB_SERVICES+"/users/";
    public static final String BASE_SHARE_URL = BASE_CHAT_URL+ "share/";
}
