/*
 * File: src/main/java/com/example/demo/config/DepartmentCatalog.java
 * Role: Config
 * MVC Fit: Spring configuration and bean wiring.
 * Connects To: Bootstraps app behavior
 */

package com.example.demo.config;

import java.util.List;

public final class DepartmentCatalog {

// Field: stores DEPARTMENTS for this class.
// Configuration method: defines how Spring should create/manage a bean.
    private static final List<String> DEPARTMENTS = List.of(
            "Computer Applications",
            "Engineering",
            "Management",
            "Hospitality",
            "Commerce",
            "Arts",
            "Science"
    );

// Configuration method: defines how Spring should create/manage a bean.
    private DepartmentCatalog() {
    }

// Configuration method: defines how Spring should create/manage a bean.
    public static List<String> departments() {
        return DEPARTMENTS;
    }
}
