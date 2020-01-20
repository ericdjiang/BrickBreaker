package breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {

    Paddle(int startX, int startY, int width, int height, Color color) {
        super( width, height, color );
        setX(startX);
        setY(startY);
    }

    /**
     * Moves paddle to specified x position
     * @param x position of the mouse
     */
    public void moveTo(double x) {
        setX(x-getWidth()/2);
    }
}