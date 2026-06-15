module pl.projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires org.bytedeco.opencv;
    exports pl.projekt;
    opens pl.projekt.controller;
    opens pl.projekt.repository;
    opens pl.projekt.util;
}
