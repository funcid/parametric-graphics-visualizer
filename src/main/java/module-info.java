module me.func.parametricfunction {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.codehaus.groovy;

    opens me.func.parametricfunction to javafx.fxml;
    opens me.func.parametricfunction.controller.main to javafx.fxml;

    exports me.func.parametricfunction;
    exports me.func.parametricfunction.controller.main;
}