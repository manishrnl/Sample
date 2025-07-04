package org.example.university_management_system.Java_StyleSheet;

import javafx.scene.Scene;

/**
 * Theme_Manager is responsible for applying and tracking the active CSS theme
 * used in the JavaFX application.
 */
public class Theme_Manager {

    // Default theme (can be dark-theme.css or light-theme.css)
    private static String currentThemePath = "/org/example/university_management_system/Stylesheet/Light-Theme.css"; // Change this default if needed

    /**
     * Sets the current theme path. This will be used on next scene load.
     *
     * @param themeCssPath Path to the theme CSS file, e.g., "/styles/dark-theme.css"
     */
    public static void setCurrentTheme(String themeCssPath) {
        if (themeCssPath != null && !themeCssPath.isEmpty()) {
            currentThemePath = themeCssPath;
        }
    }

    /**
     * Returns the currently set theme CSS path.
     *
     * @return the current theme CSS file path
     */
    public static String getCurrentTheme() {
        return currentThemePath;
    }

    /**
     * Applies the current theme to the provided scene.
     *
     * @param scene the JavaFX Scene to apply the theme to
     */
    public static void applyTheme(Scene scene) {
        applyTheme(scene, currentThemePath);
    }

    /**
     * Applies the given theme CSS to the provided scene and stores it as current.
     *
     * @param scene         the JavaFX Scene to apply the theme to
     * @param themeCssPath  path to the theme CSS file, e.g., "/styles/dark-theme.css"
     */
    public static void applyTheme(Scene scene, String themeCssPath) {
        if (scene == null || themeCssPath == null || themeCssPath.isEmpty()) {

            return;
        }

        try {
            String themeUrl = Theme_Manager.class.getResource(themeCssPath).toExternalForm();
            scene.getStylesheets().clear(); // Remove previous styles
            scene.getStylesheets().add(themeUrl);
            currentThemePath = themeCssPath;

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
