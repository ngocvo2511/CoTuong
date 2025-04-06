module com.example.cotuong {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cotuong to javafx.fxml;
    exports com.example.cotuong;
}