package com.example.baobab_academy.models.enums;

public enum CourseLevel {
    DEBUTANT("Débutant"),
    INTERMEDIAIRE("Intermédiaire"),
    AVANCE("Avancé");

    private final String displayName;

    CourseLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
