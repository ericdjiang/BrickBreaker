package breakout;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Shape;

public class PowerUp extends ImageView {
    private static final int VELOCITY_Y = 2;
    String power;
    int strength;
    double startX;
    double startY;

    PowerUp(int startX, int startY, int width, int height, Image image, String power) {
        super(image);

        this.power = power;

        setFitWidth(width);
        setFitHeight(height);
        setX(startX);
        setY(startY);
    }

    public void moveDown() {
        setY(getY()+VELOCITY_Y);
    }
    boolean checkBottomCollide (int STAGEHEIGHT) {
        return( getY() > STAGEHEIGHT - getFitHeight() );
    }

    boolean checkPaddleCollide (Paddle paddle) {
        return paddle.getBoundsInParent().intersects(this.getBoundsInParent());

    }

    public String getPower (){
        return power;
    }


}