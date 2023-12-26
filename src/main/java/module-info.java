module me.func.parametricfunction {
    requires javafx.controls;
    requires javafx.fxml;

    opens me.func.parametricfunction to javafx.fxml;
    exports me.func.parametricfunction;
}