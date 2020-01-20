package breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class holds a Laser which always moves upwards
 *
 * @author  Eric Jiang
 * @version 1.0
 * @since   2020-01-20
 */

public class Laser extends Rectangle {
    private final int VELOCITY_Y = -3;

    Laser(int startX, int startY, int width, int height) {
        super(width, height, Color.BLUEVIOLET);

        setX(startX);
        setY(startY);
    }

    /**
     * Check if the power up has collided with the top of the playarea
     * @param STAGE_MARGIN the topmost part of the playarea
     * @return boolean representing if the power up has collided with the top of the playarea
     */
    public boolean checkTopCollide (int STAGE_MARGIN) {
        return(getY() < STAGE_MARGIN);
    }

    /**
     * Translate upwards at constant velocity
     */
    public void moveUp(){
        setY(getY()+VELOCITY_Y);
    }
}