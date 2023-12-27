package me.func.parametricfunction.handler;

import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class CameraHandler {

    private static final double MOVE_DISTANCE = 100.0;

    public void addEventHandlers(Scene scene, Rectangle rectangle) {
        addKeyboardEventHandler(scene);
        addMouseMoveEventHandler(scene, rectangle);
        addScrollEventHandler(scene);
    }

    public Camera initializeCamera() {
        Camera camera = new PerspectiveCamera(true);
        camera.setFarClip(500000);
        return camera;
    }

    private void addKeyboardEventHandler(Scene scene) {
        scene.setOnKeyPressed(event -> {
            Camera camera = scene.getCamera();

            switch (event.getCode()) {
                case W:
                    camera.translateYProperty().set(camera.getTranslateY() - MOVE_DISTANCE);
                    break;
                case S:
                    camera.translateYProperty().set(camera.getTranslateY() + MOVE_DISTANCE);
                    break;
                case A:
                    camera.translateXProperty().set(camera.getTranslateX() - MOVE_DISTANCE);
                    break;
                case D:
                    camera.translateXProperty().set(camera.getTranslateX() + MOVE_DISTANCE);
                    break;
                case E:
                    camera.setRotationAxis(Rotate.Y_AXIS);
                    camera.setRotate(camera.getRotate() + 20);
                    break;
                default:
                    break;
            }
        });
    }

    private void addMouseMoveEventHandler(Scene scene, Rectangle rectangle) {
        scene.setOnMouseMoved(event -> {
            Tooltip tooltip = new Tooltip("X: " + event.getX() + "; Y: " + event.getY());
            Tooltip.install(rectangle, tooltip);
        });
    }

    private void addScrollEventHandler(Scene scene) {
        scene.setOnScroll(event -> {
            Camera camera = scene.getCamera();
            double delta = event.getDeltaY();
            camera.translateZProperty().set(camera.getTranslateZ() + delta);
            camera.translateXProperty().set(camera.getTranslateX() + (camera.getRotate() > 180 ? -event.getX() / 25 : event.getX() / 25));
            camera.translateYProperty().set(camera.getTranslateY() + (camera.getRotate() > 180 ? -event.getY() / 25 : event.getY() / 25));
        });
    }
}
