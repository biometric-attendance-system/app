package pl.projekt;

/**
 * @brief Wrapper class for the main application.
 * Its purpose is to bypass JavaFX module-path requirements and
 * launch an application when packaged as a Jar file correctly.
 */
public class AppWrap {
    public static void main(String[] args) {
        App.main(args);
    }
}