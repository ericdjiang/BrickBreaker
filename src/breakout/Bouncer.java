package breakout;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bouncer extends ImageView {
    int DIRECTION_X = 1;
    int DIRECTION_Y = -1;
    int vel = 3;
    Bouncer(int startX, int startY, int width, int height, String type, Image image, int vel) {
        super( image );

        this.vel = vel;

//        setFitWidth(width);
//        setFitHeight(height);
        setTranslateX(width / 2 - getBoundsInLocal().getWidth() / 2);
        setTranslateY(height / 2 - getBoundsInLocal().getHeight() / 2 +30);
    }

    void move () {
        setX(getX() + DIRECTION_X * vel);
        setY(getY() + DIRECTION_Y * vel);
    }

    void flipDirectionX(){
        DIRECTION_X*=-1;
    }
    void flipDirectionY(){
        DIRECTION_Y*=-1;
    }
}
