module com.dict {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.dict to javafx.fxml;
    opens com.dict.controller to javafx.fxml;
    exports com.dict;
    exports com.dict.controller;
}
