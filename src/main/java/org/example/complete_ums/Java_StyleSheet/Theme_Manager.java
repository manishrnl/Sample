package org.example.complete_ums.Java_StyleSheet;

import javafx.scene.Scene;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Theme_Manager {
    private static final String THEME_FILE = "Save_Themes.properties";
    private static String currentThemePath = "/org/example/complete_ums/Stylesheet/Light-Theme.css"; // Default theme


    static {
        try (FileInputStream fis = new FileInputStream(THEME_FILE)) {
            Properties props = new Properties();
            props.load(fis);
            String savedTheme = props.getProperty("current.theme");
            if (savedTheme != null && !savedTheme.isEmpty()) {
                currentThemePath = savedTheme;
                System.out.println("Loaded saved theme: " + currentThemePath);
            }
        } catch (Exception e) {
        }
    }

    public static void setCurrentTheme(String themeCssPath) {
        if (themeCssPath != null && !themeCssPath.isEmpty()) {
            if (!themeCssPath.equals(currentThemePath)) {
                currentThemePath = themeCssPath;
                saveThemeToFile(themeCssPath);
            }
        }
    }

    public static String getCurrentTheme() {
        return currentThemePath;
    }

    public static void applyTheme(Scene scene) {
        applyTheme(scene, currentThemePath);
    }

    public static void applyTheme(Scene scene, String themeCssPath) {
        if (scene == null || themeCssPath == null || themeCssPath.isEmpty()) {
            return;
        }
        try {
            String themeUrl = Theme_Manager.class.getResource(themeCssPath).toExternalForm();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(themeUrl);

            if (!themeCssPath.equals(currentThemePath)) {
                currentThemePath = themeCssPath;
                saveThemeToFile(themeCssPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveThemeToFile(String themeCssPath) {
        try (FileOutputStream fos = new FileOutputStream(THEME_FILE)) {
            Properties props = new Properties();
            props.setProperty("current.theme", themeCssPath);
            props.store(fos, "Last selected theme");
            System.out.println("Saved theme: " + themeCssPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
