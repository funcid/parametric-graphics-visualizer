package me.func.parametricfunction.controller.main;

import groovy.lang.GroovyShell;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import me.func.parametricfunction.handler.CameraHandler;
import me.func.parametricfunction.util.MathEvaluator;

import static me.func.parametricfunction.handler.CameraHandler.*;
import static me.func.parametricfunction.util.MathEvaluator.*;

public class MainController {

    private static final int HALF_LENGTH_OFFSET = 2;
    private static final int GRAPH_COUNT = 2000;
    private static final int SCENE_WIDTH = 1000;
    private static final int SCENE_HEIGHT = 1000;
    private static final int RECTANGLE_DIMENSION = 1000;
    private static final int TRANSPARENT_RECTANGLE_SIZE = 2000;
    private static final int TEXT_AREA_OFFSET = 150;
    private static final int TEXT_AREA_HALF_LENGTH_OFFSET = TEXT_AREA_OFFSET / HALF_LENGTH_OFFSET;
    private static final double TEXT_AREA_TRANSLATE_FACTOR = 200.0;

    @FXML
    private TextField inputXField;
    @FXML
    private TextField inputYField;
    @FXML
    private TextField inputZField;
    @FXML
    private Button buildButton;
    private Stage stage;
    private final Font font = Font.font(24);
    private final PhongMaterial black = createMaterial(Color.BLACK);

    @FXML
    private void initialize() {
        setupBuildButton();
    }

    private void setupBuildButton() {
        buildButton.setOnAction(event -> {
            if (anyInputFieldIsEmpty()) return;
            loadGraph();
        });
    }

    private boolean anyInputFieldIsEmpty() {
        return inputXField.getText().isEmpty() || inputYField.getText().isEmpty() || inputZField.getText().isEmpty();
    }

    private void loadGraph() {
        Group group = new Group();

        Camera camera = initializeCamera();

        Cylinder axis = createAxis(Color.LIGHTBLUE, Rotate.X_AXIS, 90);
        Cylinder ordinate = createAxis(Color.RED, null, 0);
        Cylinder z = createAxis(Color.GREEN, Rotate.Z_AXIS, 90);

        Cylinder[] lines = generateGraphLines();

        Rectangle rectangle = createTransparentRectangle();

        group.getChildren().add(rectangle);
        group.getChildren().addAll(createNumberTextAreas(0, 1, 0, false));
        group.getChildren().addAll(createNumberTextAreas(1, 0, 0, false));
        group.getChildren().addAll(createNumberTextAreas(0, 0, 1, true));
        group.getChildren().add(ordinate);
        group.getChildren().add(z);
        group.getChildren().add(axis);
        group.getChildren().addAll(lines);

        Scene scene = new Scene(group, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setCamera(camera);

        addEventHandlers(scene, rectangle);
        initializeAndShowStage(scene);
    }

    private Camera initializeCamera() {
        Camera camera = new PerspectiveCamera(true);
        camera.setFarClip(500000);
        return camera;
    }

    private Cylinder createAxis(Color color, Point3D rotationAxis, double rotationAngle) {
        Cylinder axis = new Cylinder(1, 100000);
        axis.setMaterial(createMaterial(color));
        if (rotationAxis != null) {
            axis.setRotationAxis(rotationAxis);
            axis.setRotate(rotationAngle);
        }
        return axis;
    }

    private Cylinder[] generateGraphLines() {
        GroovyShell shell = initializeMathShell();
        Cylinder[] lines = new Cylinder[GRAPH_COUNT - 1];
        Point3D previous = null;

        for (double p = 0; p < GRAPH_COUNT; p++) {
            shell.setVariable("x", p);
            Point3D now = evaluatePoint3D(
                    shell,
                    inputXField.getText(),
                    inputYField.getText(),
                    inputZField.getText()
            );
            if (p > 0) {
                lines[(int) p - 1] = createConnection(previous, now);
            }
            previous = now;
        }
        return lines;
    }

    private void initializeAndShowStage(Scene scene) {
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    private Rectangle createTransparentRectangle() {
        Rectangle rect = new Rectangle(-RECTANGLE_DIMENSION, -RECTANGLE_DIMENSION, TRANSPARENT_RECTANGLE_SIZE, TRANSPARENT_RECTANGLE_SIZE);
        rect.setOpacity(0);
        return rect;
    }

    private PhongMaterial createMaterial(Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        return material;
    }

    private Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);

        Point3D mid = target.midpoint(origin);
        Cylinder line = new Cylinder(.9, diff.magnitude() + .2);
        line.setMaterial(black);

        line.getTransforms().addAll(
                new Translate(mid.getX(), mid.getY(), mid.getZ()),
                new Rotate(-Math.toDegrees(Math.acos(diff.normalize().dotProduct(yAxis))), diff.crossProduct(yAxis))
        );
        return line;
    }

    private TextArea[] createNumberTextAreas(int xProperty, int yProperty, int zProperty, boolean isNormal) {
        TextArea[] numbers = new TextArea[TEXT_AREA_OFFSET];

        for (int i = -TEXT_AREA_HALF_LENGTH_OFFSET; i < TEXT_AREA_HALF_LENGTH_OFFSET; i++) {
            int index = i + TEXT_AREA_HALF_LENGTH_OFFSET;
            numbers[index] = initializeTextArea(i, xProperty, yProperty, zProperty, isNormal);
        }

        return numbers;
    }

    private TextArea initializeTextArea(int value, int xProperty, int yProperty, int zProperty, boolean isNormal) {
        TextArea textArea = new TextArea(String.valueOf(-value));

        setupTextAreaProperties(textArea, value, xProperty, yProperty, zProperty, isNormal);

        return textArea;
    }

    private void setupTextAreaProperties(TextArea textArea, int value, int xProperty, int yProperty, int zProperty, boolean isNormal) {
        textArea.translateXProperty().set(-value * TEXT_AREA_TRANSLATE_FACTOR * xProperty);
        textArea.translateYProperty().set(value * TEXT_AREA_TRANSLATE_FACTOR * yProperty);

        if (isNormal) {
            textArea.translateYProperty().set(value * TEXT_AREA_TRANSLATE_FACTOR * yProperty - (double) TEXT_AREA_TRANSLATE_FACTOR / 2);
            textArea.setRotationAxis(Rotate.X_AXIS);
            textArea.setRotate(270);
        }

        textArea.translateZProperty().set(value * TEXT_AREA_TRANSLATE_FACTOR * zProperty);
        textArea.setMaxWidth(TEXT_AREA_TRANSLATE_FACTOR);
        textArea.setMaxHeight(TEXT_AREA_TRANSLATE_FACTOR);
        textArea.setFont(font);
        textArea.setEditable(false);
        textArea.setMouseTransparent(true);
        textArea.setFocusTraversable(false);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
