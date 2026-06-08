module gamezone {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens gamezone.ui to javafx.fxml;
    exports gamezone.ui;
    exports gamezone.model;
    exports gamezone.service;
    exports gamezone.repository;
    exports gamezone.interfaces;
}
