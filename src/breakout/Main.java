package breakout;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.module.FindException;
import java.lang.reflect.Array;
import java.util.*;


import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors


/**
 * @version 1.1 01/12/2020
 * @author Eric Jiang
 * @netid edj9
 */
public class Main extends Application {
    public static final String TITLE = "Bakeout - Eric Jiang";
    public static final int STAGE_WIDTH = 700;
    public static final int STAGE_HEIGHT = 400;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.BLACK;
    public static final Paint HIGHLIGHT = Color.OLIVEDRAB;
    public static final String BOUNCER_IMAGE = "ball.gif";
    public static final int BOUNCER_SPEED = 300;
    public static final Paint MOVER_COLOR = Color.PLUM;
    public static final int MOVER_SIZE = 50;
    public static final int MOVER_SPEED = 5;
    public static final Paint GROWER_COLOR = Color.BISQUE;
    public static final double GROWER_RATE = 1.1;
    public static final int GROWER_SIZE = 50;

    public static final String BRICK_LAYOUT_FILE = "./brick_layouts.txt";
    public static final int STAGE_PADDING_X = 50;
    public static final int STAGE_PADDING_Y = 50;
    public static final int brickHeight = 30;


    // some things needed to remember during game
    private Scene myScene;
    private Bouncer mainBouncer;
    private Paddle myPaddle;
    private int score;
    private int currentLevel = 1;
    private boolean gamePaused = true;

    private ArrayList <Brick> myBricks = new ArrayList<>();
    private ArrayList <Bouncer> myBouncers = new ArrayList<>();
    private ArrayList <String[][]> myBrickLayouts = new ArrayList<>();
    private ArrayList <PowerUp> myPowerUps = new ArrayList<>();

    private HashMap<String, String> myPowerUpMap = new HashMap<>();
    Group root = new Group();


    @Override
    public void start(Stage stage) throws Exception {
        myScene = setupGame(STAGE_WIDTH, STAGE_HEIGHT, BACKGROUND);
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

    private void createBrickLayouts(){
        try {
            File file = new File(
                    getClass().getClassLoader().getResource("brick_layouts.txt").getFile()
            );
            Scanner myReader = new Scanner(file);

            String[][] brickLayout = new String[3][6];
            int brickRow = 0;
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();

                if(line.equals("-")){
                    myBrickLayouts.add(brickLayout);
                    brickRow = 0;
                    brickLayout = new String[3][6];
                } else {
                    String[] rowBricks = line.split(" ");
                    brickLayout[brickRow] = rowBricks;
                    brickRow+=1;
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    // Create the game's "scene": what shapes will be in the game and their starting properties
    private Scene setupGame (int width, int height, Paint background) {
        // read in file with brick layouts
        createBrickLayouts();
        // create one top level collection to organize the things in the scene
        initLevel(currentLevel);
        // create a place to see the shapes
        Scene scene = new Scene(root, width, height, background);
        // respond to input
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseMoved(e -> handleMouseMove(e.getX(), e.getY()));
//        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, handleMouseInput);
        return scene;
    }

    private void generateBricks(int newLevel){
        int brickSpacing = 10;

        int brickWidth = ( STAGE_WIDTH - STAGE_PADDING_X*2 )/6 - brickSpacing*2;
        int brickX = STAGE_PADDING_X + brickSpacing;


        int brickY = STAGE_PADDING_Y;



        String[][] myBrickLayout = myBrickLayouts.get(newLevel-1);
        for (int row = 0; row < myBrickLayout.length; row++) {
            for (int col = 0; col < myBrickLayout[row].length; col++) {
                String cell = myBrickLayout[row][col];

                String index = String.valueOf(row) + String.valueOf(col);

                int brickStrength = Integer.valueOf(cell.substring(0,1));
                if(cell.length()>1){
                    String powerUp = cell.substring(1);
                    myPowerUpMap.put(index, powerUp);
                }

                if(brickStrength!=0) {
                    Brick myBrick = new Brick(
                            brickX,
                            brickY,
                            brickWidth,
                            brickHeight,
                            Color.BLACK,
                            brickStrength,
                            index
                    );

                    myBricks.add(myBrick);
                    root.getChildren().add(myBrick);
                }
                brickX += brickSpacing*2 + brickWidth;
            }
            brickX = STAGE_PADDING_X + brickSpacing;
            brickY += brickSpacing + brickHeight;
            System.out.println();
        }
    }

    private void clearOldSprites(){
        myBricks.clear();
        myBouncers.clear();

        root.getChildren().clear();

        System.out.println("clearing sprites");
    }


    private void initLevel(int newLevel) {
        gamePaused = true;
        clearOldSprites();
        // set up paddle
        int paddleWidth = 100;
        int paddleHeight = 5;
        myPaddle = new Paddle(
                STAGE_WIDTH/2 - paddleWidth/2,
                STAGE_HEIGHT - paddleHeight,
                paddleWidth,
                paddleHeight,
                Color.WHITE,
                3
        );

        int bouncerWidth = 10;
        int bouncerHeight = 10;
        // set up bouncer
        mainBouncer = new Bouncer(
                STAGE_WIDTH/2 - bouncerWidth/2,
                STAGE_HEIGHT - paddleHeight - bouncerHeight,
                bouncerWidth,
                bouncerHeight,
                Color.PLUM
        );

        myBouncers.add(mainBouncer);

        root.getChildren().add(myPaddle);
        root.getChildren().add(mainBouncer);

        System.out.println("initing level " + newLevel);

        generateBricks(newLevel);
//        switch(newLevel){
//            case 1:
//                for (int i = 0; i < 6; i++) {
//                    Brick myBrick = new Brick(
//                            STAGE_WIDTH/6*i,
//                            0,
//                            STAGE_WIDTH/8,
//                            50,
//                            Color.BLACK,
//                            new Random().nextInt(3) + 1
//                    );
//
//                    myBricks.add(myBrick);
//                    root.getChildren().add(myBrick);
//                }
//                break;
//            case 2:
//                for (int i = 0; i < 6; i++) {
//                    Brick myBrick = new Brick(
//                            STAGE_WIDTH/6*i,
//                            0,
//                            STAGE_WIDTH/8,
//                            50,
//                            Color.BLACK,
//                            new Random().nextInt(3) + 1
//                    );
//
//                    myBricks.add(myBrick);
//                    root.getChildren().add(myBrick);
//                }
//            default:
//                System.out.println("done");
//
//                break;
//        }
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        ArrayList<Bouncer> deadBouncers = new ArrayList<Bouncer>();
        ArrayList<Brick> deadBricks = new ArrayList<Brick>();
        HashSet<String> deadBrickIndices = new HashSet<>();

        for(Bouncer myBouncer: myBouncers) {
            myBouncer.checkPaddleCollide(myPaddle);
            myBouncer.checkWallCollide(STAGE_WIDTH);

            if(myBouncer.checkBottomCollide(STAGE_HEIGHT)){
                deadBouncers.add(myBouncer);
            }

            myBouncer.move();

            for (Brick myBrick : myBricks) {
                myBrick.changeColor();

                if (myBouncer.checkBrickCollide(myBrick) && myBrick.isDead()) {
                    deadBricks.add(myBrick);
                    deadBrickIndices.add(myBrick.getIndex());
                }
            }
        }

        myBouncers.removeAll(deadBouncers);
        root.getChildren().removeAll(deadBouncers);

        myBricks.removeAll(deadBricks);
        root.getChildren().removeAll(deadBricks);


        for (String index: myPowerUpMap.keySet()){
            if(deadBrickIndices.contains(index)){
                int indexX = Integer.parseInt(index.substring(0));
                int indexY = Integer.parseInt(index.substring(0,0));
                int POWERUPSIZE = 30;
                int POWERUPSPACING = ((STAGE_WIDTH - STAGE_PADDING_X*2)/6-POWERUPSIZE)/2;

                Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE));
                PowerUp myPowerUp = new PowerUp(
                        STAGE_PADDING_X + POWERUPSPACING + POWERUPSPACING*indexX,
                        STAGE_PADDING_Y +  brickHeight*indexY,
                        POWERUPSIZE,
                        POWERUPSIZE,
                        image
                );
                myPowerUps.add(myPowerUp);
                root.getChildren().add(myPowerUp);
            }
        }

        for (PowerUp myPowerUp: myPowerUps) {
            myPowerUp.moveDown();
        }

//        System.out.println(root.getChildren().size());

//        for (Node child : root.getChildren()) {
//            if(child instanceof Brick) {
//                Brick brick = (Brick) child;
//
//                myBouncer.checkBrickCollide(brick, root);
//                brick.changeColor();
//
//                if(brick.strength==0){
//                    root.getChildren().remove(brick);
//                }
//            }
//        }

        if(myBricks.size()==0){
            initLevel(currentLevel+1);
        }
        else if(myBouncers.size()==0){
            initLevel(1);
        }
    }



    // What to do each time a key is pressed
    private void handleMouseMove ( double x, double y) {
        myPaddle.moveTo(x);

        if(gamePaused){
            mainBouncer.moveTo(x);
        }
    }

    private void handleKeyInput (KeyCode code) {
//        if (code == KeyCode.RIGHT) {
//            myPaddle.moveRight();
//        }
//        else if (code == KeyCode.LEFT) {
//            myPaddle.moveLeft();
//        }
    }


    EventHandler<MouseEvent> handleMouseInput=new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(gamePaused){
                for( Bouncer bouncer: myBouncers ){
                    bouncer.start();
                }
                gamePaused = false;
            }
//            myScene.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleMouseInput);
        }
    };


    public static void main(String args[]){
        /* Internally calls the init, start method */
        launch(args);
    }
}
