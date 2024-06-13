module project.workshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens project.workshop to javafx.fxml;
    exports project.workshop;
    exports project.workshop.gui;
    opens project.workshop.gui to javafx.fxml;
    exports project.workshop.gui.util;
    opens project.workshop.gui.util to javafx.fxml;
    exports project.workshop.model.entities;
    opens project.workshop.model.entities to javafx.fxml;
}