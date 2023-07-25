module com.example.mystudent {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.mystudent to javafx.fxml;
    exports com.example.mystudent;
}