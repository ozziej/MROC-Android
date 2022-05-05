package com.surveyfiesta.mroc.constants;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.text.WordUtils;

public enum NotificationTypes {
        USER, SURVEY, UPGRADE, POINTS,
        CONNECT, DISCONNECT, MESSAGE, SYSTEM_MESSAGE, PING, PONG;

        private static final Map<String, String> BY_DESCRIPTION = new LinkedHashMap<>();

        static {
            for (NotificationTypes t : values()) {
                BY_DESCRIPTION.put(t.name(), WordUtils.capitalizeFully(t.name().replace("_", " ")));
            }
        }

        public static String getTypeDescription(String descriptionType) {
            return BY_DESCRIPTION.get(descriptionType);
        }

        public static Map<String, String> getTypeDescriptions() {
            return BY_DESCRIPTION;
        }
    }
