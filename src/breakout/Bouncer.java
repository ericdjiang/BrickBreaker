package breakout;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.awt.*;

public class Bouncer extends Rectangle {
    int DIRECTION_X = 1;
    int DIRECTION_Y = -1;
    int vel = 3;
    Bouncer (int startX, int startY, int width, int height, Color color) {
        super( width, height, color);

        this.vel = vel;

//        setFitWidth(width);
//        setFitHeight(height);
        setX(0);
        setY(0);
    }

    void move () {
        setX(getX() + DIRECTION_X * vel);
        setY(getY() + DIRECTION_Y * vel);
    }

    void checkPaddleCollision (Paddle myPaddle){
        double thirdWidth = myPaddle.getWidth()/3;
        double x0 = myPaddle.getX();
        double x1 = myPaddle.getX() + thirdWidth;
        double x2 = myPaddle.getX() + thirdWidth*2;
        double x3 = myPaddle.getX() + thirdWidth*3;

        double bouncerCenterX = getX() + getWidth()/2;

        if(getY()+getHeight() > myPaddle.getY()) {
            flipDirectionY();

            if (x0 <= bouncerCenterX && bouncerCenterX <= x1) {
                DIRECTION_X = -1;
            } else if (x2 <= bouncerCenterX && bouncerCenterX <= x3) {
                DIRECTION_X = 1;
            }
        }
    }

    void checkWallCollide (int STAGEWIDTH, int STAGEHEIGHT) {
        if(getX() > STAGEWIDTH - getWidth() || getX() < 0){
            flipDirectionX();
        }
        if(getY() > STAGEHEIGHT - getHeight() || getY() < 0){
            flipDirectionY();
        }
    }

    void flipDirectionX(){
        DIRECTION_X*=-1;
    }
    void flipDirectionY(){
        DIRECTION_Y*=-1;
    }
}
