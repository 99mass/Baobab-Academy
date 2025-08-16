package com.example.baobab_academy.models.enums;

public enum CourseLevel {
    DEBUTANT("DEBUTANT"),
    INTERMEDIAIRE("INTERMEDIAIRE"),
    AVANCE("AVANCE");

    private final String displayName;

    CourseLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
