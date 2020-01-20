package breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class represents a Paddle whose position is
 * controlled by the user's mouse
 *
 * @author  Eric Jiang
 * @version 1.0
 * @since   2020-01-20
 */

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