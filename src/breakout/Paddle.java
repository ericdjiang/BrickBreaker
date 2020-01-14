package breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {

    Paddle(int startX, int startY, int width, int height, Color color) {
        super( width, height, color );

        setTranslateX(startX);
        setTranslateY(startY);
    }

    public void moveTo(double x) {
        setTranslateX(x-getWidth()/2);
    }

}