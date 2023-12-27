package me.func.parametricfunction.factory;

import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

public class TextAreaFactory {

    private static final int HALF_LENGTH_OFFSET = 2;
    private static final int TEXT_AREA_OFFSET = 150;
    private static final int TEXT_AREA_HALF_LENGTH_OFFSET = TEXT_AREA_OFFSET / HALF_LENGTH_OFFSET;
    private static final double TEXT_AREA_TRANSLATE_FACTOR = 200.0;

    private final Font font = Font.font(24);

    public TextArea[] createNumberTextAreas(int xProperty, int yProperty, int zProperty, boolean isNormal) {
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
}
