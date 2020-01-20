package breakout;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * This class represents a Bouncer which deflects off paddles and bricks
 *
 * @author  Eric Jiang
 * @version 1.0
 * @since   2020-01-20
 */

public class Bouncer extends Rectangle {
    int directionX = 0;
    int directionY = 0;
    int vel = 5;


    Bouncer (int startX, int startY, int width, int height, Color color) {
        super( width, height, color);

        setX(startX);
        setY(startY);
    }

    /**
     * Translates bouncer to new x and y location
     */
    public void move () {
        setX(getX() + directionX * vel);
        setY(getY() + directionY * vel);
    }

    /**
     * Translates bouncer to mouse X location
     * @param x x location of the mouse
     */
    public void moveTo(double x) {
        setX(x-getWidth()/2);
    }

    /**
     * Changes direction of bouncer depending on if and where paddle collision occurred
     * @param myPaddle a paddle object
     */
    public void checkPaddleCollide (Paddle myPaddle){
        double thirdWidth = myPaddle.getWidth()/3;
        double x0 = myPaddle.getX();
        double x1 = myPaddle.getX() + thirdWidth;
        double x2 = myPaddle.getX() + thirdWidth*2;
        double x3 = myPaddle.getX() + thirdWidth*3;

        double bouncerCenterX = getX() + getWidth()/2;

        Shape intersection = Shape.intersect(this, myPaddle);
        if(intersection.getBoundsInLocal().getWidth() != -1 && directionY!=-1) {
            flipDirectionY();
            if (x0 <= bouncerCenterX && bouncerCenterX <= x1) {
                directionX = -1;
            } else if (x2 <= bouncerCenterX && bouncerCenterX <= x3) {
                directionX = 1;
            }
        }
    }

    /**
     * Check if bouncer has collided with the top or sides of play area
     * @param STAGEWIDTH width of play area
     * @param STAGE_MARGIN top of play area
     */
    public void checkWallCollide (int STAGEWIDTH, int STAGE_MARGIN) {
        if(getX() > STAGEWIDTH - getWidth() || getX() < 0){
            flipDirectionX();
        }

        if(getY() < STAGE_MARGIN){
            flipDirectionY();
        }
    }

    /**
     * Check if bouncer has collided with the bottom of the stage
     * @param STAGE_HEIGHT the height of the stage
     * @return boolean representing if the bouncer has collided with stage
     */
    public  boolean checkBottomCollide (int STAGE_HEIGHT) {
        return( getY() > STAGE_HEIGHT - getHeight() );
    }

    /**
     * Check if bouncer has collided with a brick. Flip direction based on collision point.
     * @param myBrick single Brick object
     * @return boolean representing if bouncer has collided with brick
     */
    public boolean checkBrickCollide (Brick myBrick) {
        Shape intersection = Shape.intersect(this, myBrick);

        double ballRightX = getX() + getWidth();
        double ballLeftX = getX();
        if(intersection.getBoundsInLocal().getWidth() != -1) {
            if(ballLeftX <= myBrick.getX() ||  ballRightX >= myBrick.getX() + myBrick.getWidth()) {
                flipDirectionX();
            } else {
                flipDirectionY();
            }
            return true;
        }

        return false;
    }

    /**
     * Decreases velocity of bouncer
     */
    public void slowDown() {
        vel = 3;
    }

    /**
     * Increases velocity of bouncer
     */
    public void speedUp() {
        vel = 7;
    }

    /**
     * Set the starting velocity of the bouncer to be up and to the right
     */
    public void start() {
        directionX = 1;
        directionY = -1;
    }

    /**
     * Flips the x vector of the bouncer's movement
     */
    public void flipDirectionX() {
        directionX*=-1;
    }

    /**
     * Flips the y vector of the bouncer's movement
     */
    public void flipDirectionY() {
        directionY*=-1;
    }
}
