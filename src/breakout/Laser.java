package breakout;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.ArrayList;

public class Laser extends Rectangle {
    String index;
    int vel = -3;

    Laser(int startX, int startY, int width, int height) {
        super(width, height, Color.BLUEVIOLET);

        setX(startX);
        setY(startY);
    }
    public boolean checkTopCollide (int STAGE_MARGIN) {
        return(getY() < STAGE_MARGIN);
    }
    public void moveUp(){
        setY(getY()+vel);
    }

}