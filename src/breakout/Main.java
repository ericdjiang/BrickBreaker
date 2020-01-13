package breakout;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @version 1.1 01/12/2020
 * @author Eric Jiang
 * @netid edj9
 */
public class Main extends Application {
    public static final String TITLE = "Brick Breaker";
    public static final int SIZE = 400;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.AZURE;
    public static final Paint HIGHLIGHT = Color.OLIVEDRAB;
    public static final String BOUNCER_IMAGE = "ball.gif";
    public static final String PADDLE_IMAGE = "paddle.gif";
    public static final int BOUNCER_SPEED = 300;
    public static final Paint MOVER_COLOR = Color.PLUM;
    public static final int MOVER_SIZE = 50;
    public static final int MOVER_SPEED = 5;
    public static final Paint GROWER_COLOR = Color.BISQUE;
    public static final double GROWER_RATE = 1.1;
    public static final int GROWER_SIZE = 50;

    // some things needed to remember during game
    private Scene myScene;
//    private Bouncer myBouncer;
    private Bouncer myBouncer;
    private Rectangle myMover;
    private Rectangle myGrower;

    private Paddle myPaddle;

    private int DIRECTION_X = 1;
    private int DIRECTION_Y = 1;

    @Override
    public void start(Stage stage) throws Exception {
        myScene = setupGame(SIZE, SIZE, BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();
        // attach "game loop" to timeline to play it (basically just calling step() method repeatedly forever)
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    // Create the game's "scene": what shapes will be in the game and their starting properties
    private Scene setupGame (int width, int height, Paint background) {
        // create one top level collection to organize the things in the scene
        Group root = new Group();

        // set up paddle
        int paddleWidth = 100;
        int paddleHeight = 20;
        myPaddle = new Paddle(SIZE - 40, SIZE - 40, paddleWidth, paddleHeight, Color.BLACK);

        // set up bouncer
        Image bouncerImg = new Image(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE));
        myBouncer = new Bouncer(
                192,
                222,
                40,
                40,
                "myBouncer",
                bouncerImg
        );


        root.getChildren().add(myPaddle);
        root.getChildren().add(myBouncer);

        // create a place to see the shapes
        Scene scene = new Scene(root, width, height, background);
        // respond to input
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        return scene;
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        // update "actors" attributes
        myBouncer.move(elapsedTime);
        if (myPaddle.getBoundsInParent().intersects(myBouncer.getBoundsInParent())) {
            myPaddle.setFill(HIGHLIGHT);
            myBouncer.flipDirectionY();
        }
        else {

        }

        if(myBouncer.getX() > SIZE - myBouncer.getFitWidth() || myBouncer.getX() < 0 + myBouncer.getFitWidth()){
            myBouncer.flipDirectionX();
        }
        if(myBouncer.getY() > SIZE -myBouncer. getFitHeight() || myBouncer.getY() < 0 + myBouncer.getFitHeight()){
            myBouncer.flipDirectionY();
        }


//        myBouncer.move(elapsedTime);

//        // update "actors" attributes
//        myBouncer.setX(myBouncer.getX() + DIRECTION_X * BOUNCER_SPEED * elapsedTime);
//        myBouncer.setY(myBouncer.getY() + DIRECTION_Y * BOUNCER_SPEED * elapsedTime);
//
//        System.out.println(myBouncer.getX());
//
//        myMover.setRotate(myMover.getRotate() - 1);
//        myGrower.setRotate(myGrower.getRotate() + 1);
//
//        // check for bouncer collision with walls
//        if(myBouncer.getX() > SIZE - myBouncer.getFitWidth() || myBouncer.getX() < 0 + myBouncer.getFitWidth()){
//            DIRECTION_X *= -1;
//        }
//        if(myBouncer.getY() > SIZE - myBouncer.getFitHeight() || myBouncer.getY() < 0 + myBouncer.getFitHeight()){
//            DIRECTION_Y *= -1;
//        }
//
//        // check for collisions
//        // with shapes, can check precisely
//        // NEW Java 10 syntax that simplifies things (but watch out it can make code harder to understand)
//        // var intersection = Shape.intersect(myMover, myGrower);
//
//        Shape intersection = Shape.intersect(myMover, myGrower);
//        if (intersection.getBoundsInLocal().getWidth() != -1) {
//            myMover.setFill(HIGHLIGHT);
//        }
//        else {
//            myMover.setFill(MOVER_COLOR);
//        }
//        // with images can only check bounding box
//        if (myGrower.getBoundsInParent().intersects(myBouncer.getBoundsInParent())) {
//            myGrower.setFill(HIGHLIGHT);
//        }
//        else {
//            myGrower.setFill(GROWER_COLOR);
//        }
    }

    // What to do each time a key is pressed
    private void handleKeyInput (KeyCode code) {
        if (code == KeyCode.RIGHT) {
            myPaddle.setX(myPaddle.getX() + MOVER_SPEED);
        }
        else if (code == KeyCode.LEFT) {
            myPaddle.setX(myPaddle.getX() - MOVER_SPEED);
        }
//        else if (code == KeyCode.UP) {
//            myPaddle.setY(myPaddle.getY() - MOVER_SPEED);
//        }
//        else if (code == KeyCode.DOWN) {
//            myPaddle.setY(myPaddle.getY() + MOVER_SPEED);
//        }
        // NEW Java 12 syntax that some prefer (but watch out for the many special cases!)
        //   https://blog.jetbrains.com/idea/2019/02/java-12-and-intellij-idea/
        // Note, must set Project Language Level to "13 Preview" under File -> Project Structure
        // switch (code) {
        //     case RIGHT -> myMover.setX(myMover.getX() + MOVER_SPEED);
        //     case LEFT -> myMover.setX(myMover.getX() - MOVER_SPEED);
        //     case UP -> myMover.setY(myMover.getY() - MOVER_SPEED);
        //     case DOWN -> myMover.setY(myMover.getY() + MOVER_SPEED);
        // }
    }

    // What to do each time a key is pressed
    private void handleMouseInput (double x, double y) {
        if (myGrower.contains(x, y)) {
            myGrower.setScaleX(myGrower.getScaleX() * GROWER_RATE);
            myGrower.setScaleY(myGrower.getScaleY() * GROWER_RATE);
        }
    }



    private static class Bouncer extends ImageView {
        int DIRECTION_X = 1;
        int DIRECTION_Y = -1;
        Bouncer(int startX, int startY, int width, int height, String type, Image image) {
            super( image );

            setTranslateX(width / 2 - getBoundsInLocal().getWidth() / 2);
            setTranslateY(height / 2 - getBoundsInLocal().getHeight() / 2 +30);
        }

        void flipDirectionX(){
            DIRECTION_X*=-1;
        }
        void flipDirectionY(){
            DIRECTION_Y*=-1;
        }

        void move (double elapsedTime) {
            setX(getX() + DIRECTION_X * BOUNCER_SPEED * elapsedTime);
            setY(getY() + DIRECTION_Y * BOUNCER_SPEED * elapsedTime);
        }
    }

    private static class Paddle extends Rectangle {
        Paddle(int startX, int startY, int width, int height, Color color) {
            super( width, height, color );

            setTranslateX(startX);
            setTranslateY(startY);
        }

        void moveLeft() { setTranslateX(getTranslateX()-5); }

        void moveRight() {
            setTranslateX(getTranslateX()+5);
        }

    }

    public static void main(String args[]){
        /* Internally calls the init, start method */
        launch(args);
    }
}
