package com.surveyfiesta.mroc.constants;

import org.apache.commons.text.WordUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public enum UserTypes {
    NEW, DISABLED, DELETED, USER, SITE_ADMIN;

    private static final Map<String, String> BY_DESCRIPTION = new LinkedHashMap<>();

    static {
        for (UserTypes t : values()) {
            BY_DESCRIPTION.put(t.name(), WordUtils.capitalizeFully(t.name().replace("_", " ")));
            BY_DESCRIPTION.remove(DELETED.toString());
        }
    }

    public static String getTypeDescription(String descriptionType) {
        return BY_DESCRIPTION.get(descriptionType);
    }

    public static Map<String, String> getTypeDescriptions() {
        return BY_DESCRIPTION;
    }
}