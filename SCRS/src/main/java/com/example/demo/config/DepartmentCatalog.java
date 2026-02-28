package com.example.demo.config;

import java.util.List;

public final class DepartmentCatalog {

    private static final List<String> DEPARTMENTS = List.of(
            "Computer Applications",
            "Engineering",
            "Management",
            "Hospitality",
            "Commerce",
            "Arts",
            "Science"
    );

    private DepartmentCatalog() {
    }

    public static List<String> departments() {
        return DEPARTMENTS;
    }
}
