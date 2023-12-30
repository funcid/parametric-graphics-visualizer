package me.func.parametricfunction;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.func.parametricfunction.controller.main.MainController;
import me.func.parametricfunction.factory.GraphBuilder;
import me.func.parametricfunction.factory.MaterialFactory;
import me.func.parametricfunction.factory.TextAreaFactory;
import me.func.parametricfunction.handler.CameraHandler;

public class App extends Application {

    private static App app;

    private GraphBuilder graphBuilder;

    @Override
    public void start(Stage primaryStage) throws Exception {
        app = this;

        CameraHandler cameraHandler = new CameraHandler();
        MaterialFactory materialFactory = new MaterialFactory();
        TextAreaFactory textAreaFactory = new TextAreaFactory();
        graphBuilder = new GraphBuilder(cameraHandler, materialFactory, textAreaFactory);

        Parent parent = initMainController(primaryStage);
        configurePrimaryStage(primaryStage, parent);
    }

    private Parent initMainController(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main/main.fxml"));
        Parent root = fxmlLoader.load();

        MainController mainController = fxmlLoader.getController();
        mainController.setStage(primaryStage);

        return root;
    }

    private void configurePrimaryStage(Stage primaryStage, Parent parent) {
        primaryStage.setTitle("Проект Артема Царюка 10 класс");
        primaryStage.setScene(new Scene(parent));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static App getApp() {
        return app;
    }

    public GraphBuilder getGraphBuilder() {
        return graphBuilder;
    }
}