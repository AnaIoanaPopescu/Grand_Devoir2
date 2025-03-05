module com.example.grand_devoir2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.grand_devoir2 to javafx.fxml;
    exports com.example.grand_devoir2;
}