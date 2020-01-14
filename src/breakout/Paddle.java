package breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {
    int lives;
    Paddle(int startX, int startY, int width, int height, Color color, int lives) {
        super( width, height, color );

        this.lives = lives;
        setX(startX);
        setY(startY);
    }

    void decrementLives() {
        lives -= 1;
    }

    public void moveTo(double x) {
        setX(x-getWidth()/2);
    }

}