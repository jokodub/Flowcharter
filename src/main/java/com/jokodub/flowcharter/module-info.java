module com.jokodub.flowcharter {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.jokodub.flowcharter to javafx.fxml;
    exports com.jokodub.flowcharter;
}