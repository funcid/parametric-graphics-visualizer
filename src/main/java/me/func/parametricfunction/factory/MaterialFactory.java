package me.func.parametricfunction.factory;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public class MaterialFactory {

    private final PhongMaterial black = createMaterial(Color.BLACK);

    public PhongMaterial createMaterial(Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        return material;
    }

    public PhongMaterial getBlack() {
        return black;
    }
}
