package client.GameModels;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class XGroup extends Group {

    public XGroup() {
        super();
        getTransforms().add(new Affine());
    }

    public void rotate(double angle, Point3D axis) {
        Rotate r = new Rotate(angle, axis);
        getTransforms().set(0, r.createConcatenation(getTransforms().get(0)));
    }

    public void reset() {
        getTransforms().set(0, new Affine());
    }
}