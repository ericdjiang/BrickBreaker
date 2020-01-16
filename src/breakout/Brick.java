package breakout;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Pair;

import java.util.ArrayList;

public class Brick extends Rectangle {
    String index;
    int brickScore;
    int strength;
    double startX;
    double startY;

    Brick(int startX, int startY, int width, int height, Color color, int strength, String index) {
        super(width, height, color);

        this.index = index;
        this.strength = strength;
        this.brickScore = strength;

        this.startX = startX;
        this.startY = startY;

        setX(startX);
        setY(startY);
        changeColor();
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
                setFill(Color.BLUE);
                break;
        }
    }

    void kill(Group root, ArrayList<Brick> myBricks){
        root.getChildren().remove(this);
        myBricks.remove(this);
    }

    boolean checkLaserCollide (Laser myLaser) {
        Shape intersection = Shape.intersect(this, myLaser);
        return (intersection.getBoundsInLocal().getWidth() != -1);
    }

    String getIndex(){
        return index;
    }
    boolean isDead(){
        return strength==0;
    }
    void update() {
        setX(startX);
        setY(startY);
    }
}