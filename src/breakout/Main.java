package breakout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


/**
 * @version 1.1 01/12/2020
 * @author Eric Jiang
 * @netid edj9
 */
public class Main extends Application {
    public static final String TITLE = "Bakeout - Eric Jiang";
    public static final int STAGE_WIDTH = 700;
    public static final int STAGE_MARGIN = 50;
    public static final int STAGE_HEIGHT = 400+STAGE_MARGIN;
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


    public static final int PLAYER_LIVES = 3;

    // some things needed to remember during game
    private Scene myScene;
    private Bouncer mainBouncer;
    private Paddle myPaddle;
    private int score;
    private int currentLevel = 0;
    private boolean gamePaused = true;

    // brick widths
    int brickSpacing = 10;
    int brickWidth = ( STAGE_WIDTH - STAGE_PADDING_X*2 )/6 - brickSpacing*2;

    int paddleWidth = 100;
    int PADDLE_WIDTH_WIDE = 200;
    int paddleHeight = 5;
    int bouncerWidth = 10;
    int bouncerHeight = 10;

    boolean lasersEnabled = false;
    int laserInterval = 1; //seconds
    int framesBetweenLasers = laserInterval * FRAMES_PER_SECOND;
    int laserCount = 3;
    int laserFramesLeft = framesBetweenLasers*laserCount;

    private ArrayList <Brick> myBricks = new ArrayList<>();
    private ArrayList <Bouncer> myBouncers = new ArrayList<>();
    private ArrayList <PowerUp> myPowerUps = new ArrayList<>();
    private ArrayList <Laser> myLasers = new ArrayList<>();

    private ArrayList <String[][]> myBrickLayouts = new ArrayList<>();

    private HashMap<String, String> myPowerUpMap = new HashMap<>();
    Group root = new Group();

    int playerScore = 0;
    int playerLives = 3;
    LevelText currLvlTxt;
    LevelText scoreText;
    LevelText lifeText;

    LevelText startScreenTxt;
    LevelText newLevelText;

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
            // read in file of brick layouts for multiple levels
            File file = new File(getClass().getClassLoader().getResource("brick_layouts.txt").getFile());
            Scanner myReader = new Scanner(file);
            String[][] brickLayout = new String[3][6];

            storeLayoutsAsArray(myReader, brickLayout);

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found.");
            e.printStackTrace();
        }
    }

    private void storeLayoutsAsArray(Scanner myReader, String[][] brickLayout) {
        int brickRow = 0;
        while (myReader.hasNextLine()) {
            String line = myReader.nextLine();

            if(line.equals("-")){ //check if level has been completely processed
                myBrickLayouts.add(brickLayout); //store current layout in arraylist of all layouts
                brickRow = 0;
                brickLayout = new String[3][6];
            } else { // store each space-separated string in array
                String[] rowBricks = line.split(" ");
                brickLayout[brickRow] = rowBricks;
                brickRow+=1;
            }
        }
    }

    private void displayStartScreen(String splashMsg){
        pauseGame();

        clearOldSprites();
        resetPlayerLives();
        resetPlayerScore();

        startScreenTxt = new LevelText(0, 30, STAGE_WIDTH, splashMsg, 14);
        root.getChildren().add(startScreenTxt);
    }

    private void pauseGame() {
        gamePaused = true;
    }

    // Create the game's "scene": what shapes will be in the game and their starting properties
    private Scene setupGame (int width, int height, Paint background) {
        // read in file with brick layouts
        createBrickLayouts();
        // create one top level collection to organize the things in the scene
        displayStartScreen("Welcome to BrickBreaker.\nPress to start.");
        // create a scene that contains all game objects
        Scene scene = new Scene(root, width, height, background);
        // handle keyboard input for level changes/cheat codes
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));

        return scene;
    }

    private void generateBricks(int newLevel){
        int brickX = STAGE_PADDING_X + brickSpacing;
        int brickY = STAGE_MARGIN+STAGE_PADDING_Y;
        String[][] myBrickLayout = myBrickLayouts.get(newLevel-1); //get brick layout corresponding to current level
        for (int row = 0; row < myBrickLayout.length; row++) {
            for (int col = 0; col < myBrickLayout[row].length; col++) {
                String cell = myBrickLayout[row][col];
                String index = String.valueOf(row) + String.valueOf(col);
                int brickStrength = Integer.valueOf(cell.substring(0,1));
                if(cell.length()>1){ // if brick contains a powerup
                    storePowerUp(cell, index);
                }
                if(brickStrength!=0) {
                    generateBrick(brickX, brickY, index, brickStrength);
                }
                brickX += brickSpacing*2 + brickWidth;
            }
            brickX = STAGE_PADDING_X + brickSpacing;
            brickY += brickSpacing + brickHeight;
        }
    }

    private void storePowerUp(String cell, String index) {
        String powerUp = cell.substring(1);
        myPowerUpMap.put(index, powerUp);
    }

    private void generateBrick(int brickX, int brickY, String index, int brickStrength) {
        Brick myBrick = new Brick(brickX, brickY, brickWidth, brickHeight, Color.BLACK, brickStrength,index);
        myBricks.add(myBrick);
        root.getChildren().add(myBrick);
    }

    private void clearOldSprites(){
        myPowerUpMap.clear();

        myBricks.clear();
        myBouncers.clear();
        myPowerUps.clear();
        myLasers.clear();

        disableLasers();
        resetLaserFrames();

        root.getChildren().clear();
    }


    private void resetPlayerLives() {
        playerLives = PLAYER_LIVES;
    }
    private void resetPlayerScore() {
        playerScore = 0;
    }

    private void showNewLevelText(){
        newLevelText = new LevelText(
                (STAGE_WIDTH- 300)/2,
                300,
                300,
                "Level: " + currentLevel + "\nLeft click to start",
                20
        );
        root.getChildren().add(newLevelText);
    }

    private void initLevel(int newLevel) {
        myScene.setOnMouseMoved(e -> handleMouseMove(e.getX(), e.getY()));
        myScene.addEventFilter(MouseEvent.MOUSE_CLICKED, handleMouseInput);

        pauseGame();
        clearOldSprites();
        showNewLevelText();

        setupStatusDisplay();

        initPaddle();
        initMainBouncer();
        generateBricks(newLevel);
    }

    private void initPaddle() {
        myPaddle = new Paddle(
                STAGE_WIDTH/2 - paddleWidth/2,
                STAGE_HEIGHT - paddleHeight,
                paddleWidth,
                paddleHeight,
                Color.WHITE
        );

        root.getChildren().add(myPaddle);
    }

    private void setupStatusDisplay() {
        int TEXT_WIDTH = 100;
        int TEXT_MARGINX = 0;

        // setup scoreboard
        currLvlTxt = new LevelText(TEXT_MARGINX, 20, TEXT_WIDTH,"Lvl " + currentLevel, 14);
        scoreText = new LevelText((STAGE_WIDTH- TEXT_WIDTH)/2, 20, TEXT_WIDTH,"Score: "+ playerScore, 14);
        lifeText = new LevelText(STAGE_WIDTH-TEXT_WIDTH, 20, TEXT_WIDTH, "Lives: " + playerLives, 14);


        root.getChildren().add(currLvlTxt);
        root.getChildren().add(scoreText);
        root.getChildren().add(lifeText);
    }

    // Change properties of shapes in small ways to animate them over time
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start
    private void step (double elapsedTime) {
        if(currentLevel!=0 && currentLevel!=4)
        updateLevelText();

        if(currentLevel!=0 && !gamePaused) {
            if(myBricks.size()==0){
                if(currentLevel==myBrickLayouts.size()) {
                displayStartScreen("You Won!");
                }else{
                    currentLevel+=1;
                    initLevel(currentLevel);
                }
            }

            else if(myBouncers.size()==0){
                System.out.println("no more bouncers");
                if(playerLives==0){
                    System.out.println("no more player lives");
                    displayStartScreen("You Lose\nPress 1 to try again.");
                } else {
                    //if the player died but still has lives
                    playerLives-=1;
                    initMainBouncer();
                    resetPaddlePosition();
                    pauseGame();
                    myScene.addEventFilter(MouseEvent.MOUSE_CLICKED, handleMouseInput);

                    System.out.println("new player lives"+playerLives);
                }

            }

        ArrayList<Bouncer> deadBouncers = new ArrayList<Bouncer>();
        ArrayList<Brick> deadBricks = new ArrayList<Brick>();
        ArrayList<Laser> deadLasers = new ArrayList<Laser>();

        HashSet<String> deadBrickIndices = new HashSet<>();

        for(Bouncer myBouncer: myBouncers) {
            myBouncer.checkPaddleCollide(myPaddle);
            myBouncer.checkWallCollide(STAGE_WIDTH, STAGE_MARGIN);

            if(myBouncer.checkBottomCollide(STAGE_HEIGHT)){
                deadBouncers.add(myBouncer);
            }

            myBouncer.move();

            for (Brick myBrick : myBricks) {
                myBrick.changeColor();

                if( myBouncer.checkBrickCollide(myBrick) ) {
                    myBrick.decrementStrength();
                }

                for(Laser myLaser: myLasers){
                    if (myBrick.checkLaserCollide(myLaser)){
                        myBrick.decrementStrength();
                        deadLasers.add(myLaser);
                        playerScore+=myBrick.brickScore;
                    }
                }

                if (myBrick.isDead()) {
                    playerScore+=myBrick.brickScore;
                    deadBricks.add(myBrick);
                    deadBrickIndices.add(myBrick.getIndex());
                }

            }
        }

        myBouncers.removeAll(deadBouncers);
        root.getChildren().removeAll(deadBouncers);

        myBricks.removeAll(deadBricks);
        root.getChildren().removeAll(deadBricks);

        myLasers.removeAll(deadLasers);
        root.getChildren().removeAll(deadLasers);


        for (String index: myPowerUpMap.keySet()){
            if(deadBrickIndices.contains(index)){
                int rowIndex = Integer.parseInt(index.substring(0,1));
                int colIndex = Integer.parseInt(index.substring(1));
                int POWERUPSIZE = 10;
                int POWERUPSPACING = ((STAGE_WIDTH - STAGE_PADDING_X*2)/6-POWERUPSIZE)/2;

                Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE));

                PowerUp myPowerUp = new PowerUp(
                        STAGE_PADDING_X + POWERUPSPACING + colIndex*( POWERUPSIZE+ POWERUPSPACING*2),
                        STAGE_MARGIN+STAGE_PADDING_Y +  brickHeight*rowIndex,
                        POWERUPSIZE,
                        POWERUPSIZE,
                        image,
                        myPowerUpMap.get(index)
                );
                myPowerUps.add(myPowerUp);
                root.getChildren().add(myPowerUp);
            }
        }

        ArrayList <PowerUp> deadPowerUps = new ArrayList();

        for (PowerUp myPowerUp: myPowerUps) {
            if(myPowerUp.checkPaddleCollide(myPaddle)){
                String power = myPowerUp.getPower();
                switch(power){
                    case "a":
                        myPaddle.setWidth(PADDLE_WIDTH_WIDE);
                    case "b":
                        for(Bouncer myBouncer: myBouncers){
                            myBouncer.slowDown();
                        }
                        break;
                    case "c":
                        System.out.println("new bouncer baby");
                        Bouncer powerBouncer = new Bouncer(
                                (int) myPaddle.getX(),
                                STAGE_HEIGHT - paddleHeight - bouncerHeight,
                                bouncerWidth,
                                bouncerHeight,
                                Color.PLUM
                        );

                        myBouncers.add(powerBouncer);
                        root.getChildren().add(powerBouncer);
                        powerBouncer.start();
                    case "d":
                        System.out.println("enabling lasers");
                        enableLasers();
                }
                deadPowerUps.add(myPowerUp);
            } else if(myPowerUp.checkBottomCollide(STAGE_HEIGHT)){
                deadPowerUps.add(myPowerUp);
            }
            myPowerUp.moveDown();
        }

        myPowerUps.removeAll(deadPowerUps);
        root.getChildren().removeAll(deadPowerUps);

        if(lasersEnabled){
            if(laserFramesLeft%framesBetweenLasers == 0) {
                System.out.println("Success");
                Laser myLaser = new Laser(
                        (int) (myPaddle.getX() + myPaddle.getWidth()/2 - 5/2),
                        STAGE_HEIGHT - paddleHeight - 10,
                        5,
                        10
                );
                myLasers.add(myLaser);
                root.getChildren().add(myLaser);
            }
            else {
//                System.out.println(laserFramesLeft%framesBetweenLasers);
            }

            laserFramesLeft-=1;

//             System.out.println(myLasers.get(0).getY());
//                myLasers.get(0).moveUp();

            if(laserFramesLeft == 0) {
                disableLasers();
            }
        }

        for(Laser myLaser: myLasers){
            myLaser.moveUp();
            if(myLaser.checkTopCollide(STAGE_MARGIN)){
                deadLasers.add(myLaser);
            }
        }

        myLasers.removeAll(deadLasers);
        root.getChildren().removeAll(deadLasers);
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


        }

    }

    private void initMainBouncer(){
        mainBouncer = new Bouncer(
                STAGE_WIDTH/2 - bouncerWidth/2,
                STAGE_HEIGHT - paddleHeight - bouncerHeight,
                bouncerWidth,
                bouncerHeight,
                Color.PLUM
        );

        myBouncers.add(mainBouncer);
        root.getChildren().add(mainBouncer);
    }
    private void resetPaddlePosition(){
        myPaddle.setX(STAGE_WIDTH/2 - paddleWidth/2);
    }
    private void updateLevelText(){
        scoreText.setText("Score: "+ playerScore);
        currLvlTxt.setText("Lvl " + currentLevel);
        lifeText.setText("Lives: "+playerLives);
    }

    private void disableLasers() {
        lasersEnabled = false;
    }
    private void enableLasers() {
        lasersEnabled = true;
    }

    private void resetLaserFrames () {
        laserFramesLeft = framesBetweenLasers*laserCount;
    }

    // What to do each time a key is pressed
    private void handleMouseMove ( double x, double y) {
        myPaddle.moveTo(x);

        if(gamePaused){
            mainBouncer.moveTo(x);
        }
    }

    private void handleKeyInput (KeyCode code) {
        if(code.isDigitKey()){
            if(code == KeyCode.DIGIT1){
                currentLevel=1;
            } else if(code == KeyCode.DIGIT2){
                currentLevel=2;
            } else if(code == KeyCode.DIGIT3){
                currentLevel=3;
            }
            initLevel(currentLevel);
            resetPlayerScore();
            resetPlayerLives();
        } else if (code == KeyCode.L){
            playerLives+=1;
        }else if (code == KeyCode.S){
            for(Bouncer myBouncer: myBouncers){
                myBouncer.slowDown();
            }
        }

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
                root.getChildren().remove(newLevelText);
                for( Bouncer bouncer: myBouncers ){
                    bouncer.start();
                }
                gamePaused = false;
            }
            myScene.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleMouseInput);
        }
    };


    public static void main(String args[]){
        /* Internally calls the init, start method */
        launch(args);
    }
}
