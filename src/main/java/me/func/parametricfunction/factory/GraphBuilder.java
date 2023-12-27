package me.func.parametricfunction.factory;

import groovy.lang.GroovyShell;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import me.func.parametricfunction.handler.CameraHandler;

import static me.func.parametricfunction.util.MathEvaluator.evaluatePoint3D;
import static me.func.parametricfunction.util.MathEvaluator.initializeMathShell;

public class GraphBuilder {

    // Константы, используемые для определения размеров и количества графических элементов.
    private static final int GRAPH_COUNT = 2000;
    private static final int SCENE_WIDTH = 1000;
    private static final int SCENE_HEIGHT = 1000;
    private static final int RECTANGLE_DIMENSION = 1000;
    private static final int TRANSPARENT_RECTANGLE_SIZE = 2000;

    private final CameraHandler cameraHandler;
    private final MaterialFactory materialFactory;
    private final TextAreaFactory textAreaFactory;

    public GraphBuilder(CameraHandler cameraHandler, MaterialFactory materialFactory, TextAreaFactory textAreaFactory) {
        this.cameraHandler = cameraHandler;
        this.materialFactory = materialFactory;
        this.textAreaFactory = textAreaFactory;
    }

    public void loadGraph(
            Stage stage,
            String xField,
            String yField,
            String zField
    ) {
        // Инициализация группы и камеры для отображения графа
        Group group = new Group();
        Camera camera = cameraHandler.initializeCamera();

        // Создание осей координат и линий графика
        Cylinder axis = createAxis(Color.LIGHTBLUE, Rotate.X_AXIS, 90, materialFactory);
        Cylinder ordinate = createAxis(Color.RED, null, 0, materialFactory);
        Cylinder z = createAxis(Color.GREEN, Rotate.Z_AXIS, 90, materialFactory);

        Cylinder[] lines = generateGraphLines(xField, yField, zField, materialFactory.getBlack());

        // Создание прозрачного прямоугольника и добавление текстовых полей
        Rectangle rectangle = createTransparentRectangle();
        group.getChildren().add(rectangle);
        group.getChildren().addAll(textAreaFactory.createNumberTextAreas(0, 1, 0, false));
        group.getChildren().addAll(textAreaFactory.createNumberTextAreas(1, 0, 0, false));
        group.getChildren().addAll(textAreaFactory.createNumberTextAreas(0, 0, 1, true));
        group.getChildren().add(ordinate);
        group.getChildren().add(z);
        group.getChildren().add(axis);
        group.getChildren().addAll(lines);

        // Установка сцены и камеры, добавление обработчиков событий
        Scene scene = new Scene(group, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setCamera(camera);

        cameraHandler.addEventHandlers(scene, rectangle);
        initializeAndShowStage(scene, stage);
    }

    private Cylinder createAxis(Color color, Point3D rotationAxis, double rotationAngle, MaterialFactory materialFactory) {
        Cylinder axis = new Cylinder(1, 100000);
        axis.setMaterial(materialFactory.createMaterial(color));
        if (rotationAxis != null) {
            axis.setRotationAxis(rotationAxis);
            axis.setRotate(rotationAngle);
        }
        return axis;
    }

    private void initializeAndShowStage(Scene scene, Stage stage) {
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    private Rectangle createTransparentRectangle() {
        Rectangle rect = new Rectangle(
                -RECTANGLE_DIMENSION,
                -RECTANGLE_DIMENSION,
                TRANSPARENT_RECTANGLE_SIZE,
                TRANSPARENT_RECTANGLE_SIZE
        );
        rect.setOpacity(0);
        return rect;
    }

    private Cylinder[] generateGraphLines(String xField, String yField, String zField, Material material) {
        GroovyShell shell = initializeMathShell();
        Cylinder[] lines = new Cylinder[GRAPH_COUNT - 1];
        Point3D previous = null;

        // Проходимся по каждой точке графика и создаем линии между ними
        for (double p = 0; p < GRAPH_COUNT; p++) {
            shell.setVariable("x", p);
            Point3D now = evaluatePoint3D(
                    shell,
                    xField,
                    yField,
                    zField
            );
            if (p > 0) {
                lines[(int) p - 1] = createConnection(previous, now, material);
            }
            previous = now;
        }
        return lines;
    }

    private Cylinder createConnection(Point3D origin, Point3D target, Material material) {
        // Определяем ось Y для вычисления угла поворота линии
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);

        // Находим среднюю точку между начальной и конечной точками
        Point3D mid = target.midpoint(origin);
        // Создаем цилиндр, который представляет соединительную линию
        Cylinder line = new Cylinder(.9, diff.magnitude() + .2);
        line.setMaterial(material);

        // Добавляем трансформации для корректного отображения соединительной линии
        line.getTransforms().addAll(
                new Translate(mid.getX(), mid.getY(), mid.getZ()),
                new Rotate(-Math.toDegrees(Math.acos(diff.normalize().dotProduct(yAxis))), diff.crossProduct(yAxis))
        );
        return line;
    }
}
