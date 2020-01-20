package breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


public class Brick extends Rectangle {
    String index;
    int brickScore;
    int strength;
    double startX;
    double startY;

    private final Color DARK_RED = Color.web("#cc0000");
    private final Color MEDIUM_RED = Color.web("#ff4d4d");
    private final Color LIGHT_RED = Color.web("#ffcccc");

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

    /**
     * Decreases the brick strength by 1 unit
     */
    public void decrementStrength() {
        strength -= 1;
    }

    /**
     * Sets the color to match the strength of brick
     */
    public void changeColor () {
        switch(strength){
            case 3:
                setFill(DARK_RED);
                break;
            case 2:
                setFill(MEDIUM_RED);
                break;
            case 1:
                setFill(LIGHT_RED);
                break;
            default:
                setFill(Color.BLACK);
                break;
        }
    }

    /**
     * Check if laser has collided with this brick
     * @param myLaser the laser object
     * @return boolean representing whether a collision has been detected between this brick and the laser
     */
    public boolean checkLaserCollide (Laser myLaser) {
        Shape intersection = Shape.intersect(this, myLaser);
        return (intersection.getBoundsInLocal().getWidth() != -1);
    }

    /**
     * @return the row and column location of this brick in the layout array
     */
    public String getIndex(){
        return index;
    }

    /**
     * @return boolean representing if the brick has been destroyed
     */
    public boolean isDead(){
        return strength==0;
    }
}