module pl.projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires opencv;
    exports pl.projekt;
    opens pl.projekt.controller to javafx.fxml;
}
