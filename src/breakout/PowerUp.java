package breakout;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PowerUp extends ImageView {
    private final int VELOCITY_Y = 2;
    private String power;

    PowerUp(int startX, int startY, int width, int height, Image image, String power) {
        super(image);
        this.power = power;

        setFitWidth(width);
        setFitHeight(height);
        setX(startX);
        setY(startY);
    }

    /**
     * Translate downward at constant velocity
     */
    public void moveDown() {
        setY(getY()+VELOCITY_Y);
    }

    /**
     * Check if power up has collided with the bottom of the stage
     * @param STAGE_HEIGHT the height of the stage
     * @return boolean representing if the power up has collided with stage
     */
    public boolean checkBottomCollide (int STAGE_HEIGHT) {
        return( getY() > STAGE_HEIGHT - getFitHeight() );
    }

    /**
     * Check if power up has collided with a paddle
     * @param paddle a paddle object
     * @return boolean representing if the power up has collided a paddle
     */
    public boolean checkPaddleCollide (Paddle paddle) {
        return paddle.getBoundsInParent().intersects(this.getBoundsInParent());
    }

    /**
     * @return the coded special power
     */
    public String getPower (){
        return power;
    }
}