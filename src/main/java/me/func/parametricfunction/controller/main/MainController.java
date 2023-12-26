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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
    private final Font font = Font.font(24);
    private final PhongMaterial black = createMaterial(Color.BLACK);

    @FXML
    private void initialize() {
        configureBuildButton();
    }

    private void configureBuildButton() {
        buildButton.setOnAction(event -> {
            if (anyInputFieldIsEmpty()) return;
            loadGraph();
        });
    }

    private boolean anyInputFieldIsEmpty() {
        return inputXField.getText().isEmpty() || inputYField.getText().isEmpty() || inputZField.getText().isEmpty();
    }

    private GroovyShell createMathShell() {
        GroovyShell shell = new GroovyShell();

        shell.evaluate(
                "cos = {double x -> Math.cos(Math.toRadians(x))}\n" +
                        "sin = {double x -> Math.sin(Math.toRadians(x))}\n" +
                        "exp = {double x -> Math.exp(Math.toRadians(x))}\n" +
                        "pi = Math.PI\n"
        );
        return shell;
    }

    private void loadGraph() {
        Group group = new Group();

        Camera camera = new PerspectiveCamera(true);
        camera.setFarClip(500000);

        Cylinder axis = new Cylinder(1, 100000);
        axis.setRotationAxis(Rotate.Z_AXIS);
        axis.setRotate(90);
        axis.setRotationAxis(Rotate.X_AXIS);
        axis.setRotate(90);
        axis.setMaterial(createMaterial(Color.LIGHTBLUE));

        Cylinder ordinate = new Cylinder(1, 100000);
        ordinate.setMaterial(createMaterial(Color.RED));

        Cylinder z = new Cylinder(1, 100000);
        z.setRotationAxis(Rotate.Z_AXIS);
        z.setRotate(90);
        z.setMaterial(createMaterial(Color.GREEN));

        int count = 2000, r = 200, length = 150;

        Point3D previous = null;
        Cylinder[] lines = new Cylinder[count - 1];

        GroovyShell shell = createMathShell();

        for (double p = 0; p < count; p++) {
            shell.setVariable("x", p);
            Point3D now = new Point3D(
                    (double) shell.evaluate(inputXField.getText()) * r,
                    (double) shell.evaluate(inputYField.getText()) * -r,
                    (double) shell.evaluate(inputZField.getText()) * r
            );
            if (p > 0)
                lines[(int) p - 1] = createConnection(previous, now);
            previous = now;
        }

        Rectangle rect = new Rectangle(-1000, -1000, 2000, 2000);
        rect.setOpacity(0);

        group.getChildren().addAll(rect);
        group.getChildren().addAll(setSize(length, r, 0, 1, 0, false));
        group.getChildren().addAll(setSize(length, r, 1, 0, 0, false));
        group.getChildren().addAll(setSize(length, r, 0, 0, 1, true));
        group.getChildren().addAll(ordinate, z, axis);
        group.getChildren().addAll(lines);

        Scene scene = new Scene(group, 1000, 1000);
        scene.setCamera(camera);

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            double y = camera.getScaleY(), p = camera.getScaleZ(), k = 100;

            double xzLength = cos(p) * k;
            double dx = xzLength * sin(y) * (camera.getRotate() % 360 >= 90 && camera.getRotate() % 360 <= 270 ? -1 : 1);
            double dz = xzLength * cos(y) * (camera.getRotate() % 360 >= 90 && camera.getRotate() % 360 <= 270 ? -1 : 1);
            double dy = k * sin(p);

            switch (event.getCode()) {
                case W:
                    camera.translateYProperty().set(camera.getTranslateY() - dy);
                    break;
                case S:
                    camera.translateYProperty().set(camera.getTranslateY() + dy);
                    break;
                case D:
                    camera.translateXProperty().set(camera.getTranslateX() + dx);
                    camera.translateZProperty().set(camera.getTranslateZ() - dz);
                    break;
                case A:
                    camera.translateXProperty().set(camera.getTranslateX() - dx);
                    camera.translateZProperty().set(camera.getTranslateZ() + dz);
                    break;
                case E:
                    camera.setRotationAxis(Rotate.Y_AXIS);
                    camera.setRotate(camera.getRotate() + 20);
                    break;
            }
        });

        stage.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            Tooltip t = new Tooltip("X: " + event.getX() / r + "; Y: " + -event.getY() / r);
            Tooltip.install(rect, t);
        });

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            camera.translateZProperty().set(camera.getTranslateZ() + delta);
            camera.translateXProperty().set(camera.getTranslateX() + (camera.getRotate() > 180 ? -event.getX() / 25 : event.getX() / 25));
            camera.translateYProperty().set(camera.getTranslateY() + (camera.getRotate() > 180 ? -event.getY() / 25 : event.getY() / 25));
        });

        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
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

    private TextArea[] setSize(int length, int radius, int XProperty, int YProperty, int ZProperty, boolean normal) {
        TextArea[] numbers = new TextArea[length];
        for (int i = -length / 2; i < length / 2; i++) {
            numbers[i + length / 2] = new TextArea(-i + "");
            numbers[i + length / 2].translateXProperty().set(-i * radius * XProperty);
            numbers[i + length / 2].translateYProperty().set(i * radius * YProperty);
            numbers[i + length / 2].translateZProperty().set(i * radius * ZProperty);
            numbers[i + length / 2].setMaxWidth(radius);
            numbers[i + length / 2].setMaxHeight(radius);
            numbers[i + length / 2].setFont(font);
            numbers[i + length / 2].setEditable(false);
            numbers[i + length / 2].setMouseTransparent(true);
            numbers[i + length / 2].setFocusTraversable(false);
            if (normal) {
                numbers[i + length / 2].translateYProperty().set(i * radius * YProperty - radius / 2);
                numbers[i + length / 2].setRotationAxis(Rotate.X_AXIS);
                numbers[i + length / 2].setRotate(270);
            }
        }
        return numbers;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
