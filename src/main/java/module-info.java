module com.example.cotuong {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.ikonli.fontawesome5;

    requires org.kordamp.ikonli.materialdesign;
    requires Java.WebSocket;
    requires org.json;
    requires com.google.gson;

    opens com.example.cotuong.controller to javafx.fxml;  // Thêm dòng này để mở package cho javafx.fxml

    exports com.example.cotuong;
    exports com.example.cotuong.controller;  // Nếu cần thiết, mở rộng xuất khẩu package controller
}