package me.func.parametricfunction.util;

import groovy.lang.GroovyShell;
import javafx.geometry.Point3D;

public class MathEvaluator {

    private static final int RADIUS = 200;

    public static GroovyShell initializeMathShell() {
        GroovyShell shell = new GroovyShell();
        shell.evaluate(
                "cos = {double x -> Math.cos(Math.toRadians(x))}\n" +
                        "sin = {double x -> Math.sin(Math.toRadians(x))}\n" +
                        "exp = {double x -> Math.exp(Math.toRadians(x))}\n" +
                        "pi = Math.PI\n"
        );
        return shell;
    }

    public static Point3D evaluatePoint3D(GroovyShell shell, String xText, String yText, String zText) {
        return new Point3D(
                (double) shell.evaluate(xText) * RADIUS,
                (double) shell.evaluate(yText) * -RADIUS,
                (double) shell.evaluate(zText) * RADIUS
        );
    }
}
