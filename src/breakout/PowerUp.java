package breakout;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PowerUp extends ImageView {
    private static final int VELOCITY_Y = 2;
    int strength;
    double startX;
    double startY;

    PowerUp(int startX, int startY, int width, int height, Image image) {
        super(image);

        setFitWidth(width);
        setFitHeight(height);
        setX(startX);
        setY(startY);
    }

    public void moveDown() {
        setY(getY()+VELOCITY_Y);
    }


}