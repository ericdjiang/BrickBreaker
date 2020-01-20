package breakout;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.awt.*;
import java.util.ArrayList;

public class Bouncer extends Rectangle {
    int directionX = 0;
    int directionY = 0;
    int vel = 5;


    Bouncer (int startX, int startY, int width, int height, Color color) {
        super( width, height, color);

        setX(startX);
        setY(startY);
    }

    public void move () {
        setX(getX() + directionX * vel);
        setY(getY() + directionY * vel);
    }

    public void moveTo(double x) {
        setX(x-getWidth()/2);
    }

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

    public void checkWallCollide (int STAGEWIDTH, int STAGE_MARGIN) {
        if(getX() > STAGEWIDTH - getWidth() || getX() < 0){
            flipDirectionX();
        }

        if(getY() < STAGE_MARGIN){
            flipDirectionY();
        }
    }

    public  boolean checkBottomCollide (int STAGEHEIGHT) {
        return( getY() > STAGEHEIGHT - getHeight() );
    }

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

    public void slowDown() {
        vel = 3;
    }

    public void speedUp() {
        vel = 7;
    }
    void start() {
        directionX = 1;
        directionY = -1;
    }

    public void flipDirectionX() {
        directionX*=-1;
    }

    public void flipDirectionY() {
        directionY*=-1;
    }
}
