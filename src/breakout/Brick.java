package breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle {
    int strength;
    double startX;
    double startY;

    Brick(int startX, int startY, int width, int height, Color color, int strength) {
        super(width, height, color);

        this.strength = strength;

        this.startX = startX;
        this.startY = startY;

        setX(startX);
        setY(startY);
    }

    void decrementStrength() {
        strength -= 1;
    }

    void changeColor () {
        switch(strength){
            case 3:
                setFill(Color.web("#cc0000"));
                break;
            case 2:
                setFill(Color.web("#ff4d4d"));
                break;
            case 1:
                setFill(Color.web("#ffcccc"));
                break;
            default:
                setFill(Color.AZURE);
                break;
        }
    }

    void update() {
        setX(startX);
        setY(startY);
    }
}