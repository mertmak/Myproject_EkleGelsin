module eklegelsin {
    requires javafx.controls;
    requires javafx.fxml;

    exports eklegelsin.arayuz to javafx.graphics;
    opens eklegelsin.arayuz to javafx.fxml;
}
