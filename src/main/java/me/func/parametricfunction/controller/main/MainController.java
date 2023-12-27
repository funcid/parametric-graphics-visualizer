package me.func.parametricfunction.controller.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.func.parametricfunction.App;

public class MainController {

    @FXML
    private TextField inputXField;
    @FXML
    private TextField inputYField;
    @FXML
    private TextField inputZField;
    @FXML
    private Button buildButton;
    private Stage stage;

    @FXML
    private void initialize() {
        setupBuildButton();
    }

    private void setupBuildButton() {
        buildButton.setOnAction(event -> {
            if (anyInputFieldIsEmpty()) return;
            App.getApp().getGraphBuilder().loadGraph(
                    stage,
                    inputXField.getText(),
                    inputYField.getText(),
                    inputZField.getText()
            );
        });
    }

    private boolean anyInputFieldIsEmpty() {
        return inputXField.getText().isEmpty() || inputYField.getText().isEmpty() || inputZField.getText().isEmpty();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
