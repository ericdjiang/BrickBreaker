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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * The Main class serves as the foundation for the BrickBreaker game.
 * This class contains the scene, stage, and game timeline, and
 * calls initializes bricks, game objects, new levels, and
 * handles user mouse/keyboard input.
 *
 * @author Eric Jiang
 * @version 1.1 01/19/2020
 * @netid edj9
 */


public class Main extends Application {
    // stage styling
    private final String TITLE = "Bakeout - Eric Jiang";
    private final int STAGE_WIDTH = 700;
    private final int STAGE_MARGIN = 50;
    private final int STAGE_HEIGHT = 400 + STAGE_MARGIN;
    private final int STAGE_PADDING_X = 50;
    private final int STAGE_PADDING_Y = 50;
    private final Paint BACKGROUND = Color.BLACK;

    // timeline speed
    private final int FRAMES_PER_SECOND = 60;
    private final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    private final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

    private final String BRICK_LAYOUT_FILE = "./brick_layouts.txt";

    // game object sizing
    private final int BRICKS_PER_ROW = 6;
    private final int BRICK_HEIGHT = 30;
    private final int PADDLE_WIDTH = 100;
    private final int PADDLE_WIDTH_WIDE = 200;
    private final int PADDLE_HEIGHT = 5;
    private final int BOUNCER_WIDTH = 10;
    private final int BOUNCER_HEIGHT = 10;
    private final int BRICK_SPACING = 10;
    private final int BRICK_WIDTH = (STAGE_WIDTH - STAGE_PADDING_X * 2) / BRICKS_PER_ROW - BRICK_SPACING * 2;
    private final int POWERUP_SIZE = 10;
    private final int POWERUP_SPACING = ((STAGE_WIDTH - STAGE_PADDING_X * 2) / BRICKS_PER_ROW - POWERUP_SIZE) / 2;
    private final int LASER_WIDTH = 3;
    private final int LASER_HEIGHT = 20;

    // game objects
    private Scene myScene;
    private Bouncer mainBouncer;
    private Paddle myPaddle1;
    private Paddle myPaddle2;

    // game states
    private final int NUMBER_OF_LEVELS = 3;
    private int currentLevel = 0;
    private boolean gamePaused = true;

    // laser durations
    private boolean lasersEnabled = false;
    private final int LASER_INTERVAL = 1; //seconds
    private final int FRAMES_BETWEEN_LASERS = LASER_INTERVAL * FRAMES_PER_SECOND;
    private final int LASER_COUNT = 3;
    private int laserFramesLeft = FRAMES_BETWEEN_LASERS * LASER_COUNT;

    // root node to hold scene objects
    private Group root = new Group();

    // lists to hold active scene objects
    private ArrayList < Brick > myBricks = new ArrayList < > ();
    private ArrayList < Bouncer > myBouncers = new ArrayList < > ();
    private ArrayList < PowerUp > myPowerUps = new ArrayList < > ();
    private ArrayList < Laser > myLasers = new ArrayList < > ();

    private ArrayList < String[][] > myBrickLayouts = new ArrayList < > ();
    private HashMap < String, String > myPowerUpMap = new HashMap < > ();

    // player data
    private final int PLAYER_START_LIVES = 3;
    private int playerLives = 3;
    private int playerScore = 0;

    // text objects
    private LevelText currLvlTxt;
    private LevelText scoreText;
    private LevelText lifeText;
    private LevelText startScreenTxt;
    private LevelText newLevelText;
    private int NEWLVL_START_Y = 300;
    private int NEWLVL_TXT_WIDTH = 300;
    private int NEWLVL_FNT_SIZE = 14;

    // power up mappings
    private final String WIDE_PADDLE_POWER = "a";
    private final String SLOW_BOUNCERS_POWER = "b";
    private final String EXTRA_BOUNCER_POWER = "c";
    private final String LASER_POWER = "d";
    private final String FAST_BOUNCERS_POWER = "e";


    // splash screen messages
    private final String WELCOME_MSG = "Welcome to BakeOut by Eric Jiang (edj9) \nPress 1 to START";
    private final String LOSE_MSG = "You lose\nPress 1 to try again";
    private final String WIN_MSG = "You win! Press 1 to start over";

    /**
     * Begins the game loop via timeline
     * @param stage holds the scene and all objects to be drawn to the screen
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        myScene = setupGame(STAGE_WIDTH, STAGE_HEIGHT, BACKGROUND);

        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();

        // game loop which repeatedly calls step() method to rerender scene
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step());
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    /**
     * Displays a splash screen with the specified message
     * @param splashMsg the message to be displayed on the splash screen
     */
    private void displaySplashScreen(String splashMsg) {
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

    /**
     * Initializes a new scene when the player first boots up the game
     * Stores parsed brick layouts for each level in an array and adds event listener
     * @param width width of scene
     * @param height height of scene
     * @param background background color of scene
     * @return
     */
    private Scene setupGame(int width, int height, Paint background) {
        // read in file with brick layouts
        myBrickLayouts = new LayoutParser().createBrickLayouts(BRICK_LAYOUT_FILE);
        // create one top level collection to organize the things in the scene
        displaySplashScreen(WELCOME_MSG);
        // create a scene that contains all game objects
        Scene scene = new Scene(root, width, height, background);
        // handle keyboard input for level changes/cheat codes
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));

        return scene;
    }

    private void initLevel(int newLevel) {
        myScene.setOnMouseMoved(e -> handleMouseMove(e.getX()));
        myScene.addEventFilter(MouseEvent.MOUSE_CLICKED, handleMouseInput);

        pauseGame();
        clearOldSprites();
        showNewLevelText();

        initStatusDisplay();

        initPaddle();
        initMainBouncer();
        generateBricks(newLevel);
    }

    private void initPaddle() {
        myPaddle1 = new Paddle(
                STAGE_WIDTH / 2 - PADDLE_WIDTH / 2,
                STAGE_HEIGHT - PADDLE_HEIGHT,
                PADDLE_WIDTH,
                PADDLE_HEIGHT,
                Color.WHITE
        );

        myPaddle2 = new Paddle(
                STAGE_WIDTH / 2 - PADDLE_WIDTH / 2,
                STAGE_HEIGHT - PADDLE_HEIGHT * 10,
                PADDLE_WIDTH,
                PADDLE_HEIGHT,
                Color.WHITE
        );

        root.getChildren().add(myPaddle1);
        root.getChildren().add(myPaddle2);
    }

    private void resetPaddlePosition() {
        myPaddle1.setX(STAGE_WIDTH / 2 - PADDLE_WIDTH / 2);
        myPaddle2.setX(STAGE_WIDTH / 2 - PADDLE_WIDTH / 2);

    }

    private void initMainBouncer() {
        mainBouncer = new Bouncer(
                STAGE_WIDTH / 2 - BOUNCER_WIDTH / 2,
                STAGE_HEIGHT - PADDLE_HEIGHT - BOUNCER_HEIGHT,
                BOUNCER_WIDTH,
                BOUNCER_HEIGHT,
                Color.PLUM
        );

        myBouncers.add(mainBouncer);
        root.getChildren().add(mainBouncer);
    }

    private void initStatusDisplay() {
        int TEXT_WIDTH = 100;
        int TEXT_MARGINX = 0;

        // setup scoreboard
        currLvlTxt = new LevelText(TEXT_MARGINX, 20, TEXT_WIDTH, "Lvl " + currentLevel, 14);
        scoreText = new LevelText((STAGE_WIDTH - TEXT_WIDTH) / 2, 20, TEXT_WIDTH, "Score: " + playerScore, 14);
        lifeText = new LevelText(STAGE_WIDTH - TEXT_WIDTH, 20, TEXT_WIDTH, "Lives: " + playerLives, 14);


        root.getChildren().add(currLvlTxt);
        root.getChildren().add(scoreText);
        root.getChildren().add(lifeText);
    }


    private void showNewLevelText() {
        newLevelText = new LevelText(
                (STAGE_WIDTH - NEWLVL_START_Y) / 2,
                NEWLVL_START_Y,
                NEWLVL_TXT_WIDTH,
                "Level: " + currentLevel + "\nLeft click to start",
                NEWLVL_FNT_SIZE
        );
        root.getChildren().add(newLevelText);
    }

    private void updateLevelText() {
        scoreText.setText("Score: " + playerScore);
        currLvlTxt.setText("Lvl " + currentLevel);
        lifeText.setText("Lives: " + playerLives);
    }

    /**
     * Loops through array containing brick layouts
     * and generates brick arrangement based on input
     * @param newLevel the level corresponding to the brick layout stored in myBrickLayouts List
     */
    private void generateBricks(int newLevel) {
        int brickX = STAGE_PADDING_X + BRICK_SPACING;
        int brickY = STAGE_MARGIN + STAGE_PADDING_Y;
        String[][] myBrickLayout = myBrickLayouts.get(newLevel - 1); //get brick layout corresponding to current level
        for (int row = 0; row < myBrickLayout.length; row++) {
            for (int col = 0; col < myBrickLayout[row].length; col++) {
                handleCellContents(brickX, brickY, myBrickLayout[row][col], row, col);

                brickX += BRICK_SPACING * 2 + BRICK_WIDTH;
            }
            brickX = STAGE_PADDING_X + BRICK_SPACING;
            brickY += BRICK_SPACING + BRICK_HEIGHT;
        }
    }

    /**
     * Generates a brick at the specified location
     * If specified, stores powerup location and value in map
     * @param brickX x location of brick
     * @param brickY y location of brick
     * @param cell the string representing the strength and powerup of a brick
     * @param row the row index of the brick in the layout
     * @param col the column index of the brick in the layout
     */
    private void handleCellContents(int brickX, int brickY, String cell, int row, int col) {
        String index = String.valueOf(row) + col;
        int brickStrength = Integer.valueOf(cell.substring(0, 1));

        if (cell.length() > 1) { // if brick contains a powerup
            storePowerUp(cell, index);
        }
        if (brickStrength != 0) {
            initBrick(brickX, brickY, index, brickStrength);
        }
    }

    private void storePowerUp(String cell, String index) {
        String powerUp = cell.substring(1);
        myPowerUpMap.put(index, powerUp);
    }

    private void initBrick(int brickX, int brickY, String index, int brickStrength) {
        Brick myBrick = new Brick(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT, Color.BLACK, brickStrength, index);
        myBricks.add(myBrick);
        root.getChildren().add(myBrick);
    }

    /**
     * Handles all collisions, rerenders moving objects, and deletes dead nodes
     */
    private void step() {
        if (currentLevel > 0 && currentLevel <= NUMBER_OF_LEVELS && !gamePaused) {
            updateLevelText();
            handleDeadNodes();
            handleBouncers();
            HashSet < String > deadBrickIndices = getDeadBrickIndeces();
            handleLasers();
            initPowerUps(deadBrickIndices);
            ArrayList < PowerUp > deadPowerUps = new ArrayList();
            handlePowerUps(deadPowerUps);
            shootLasers();
        }
    }

    /**
     * Checks for collisions between bouncers and wall, bricks, and screen bottom.
     * Bouncer movement is handled based on collision type
     */
    private void handleBouncers() {
        ArrayList<Bouncer> deadBouncers = new ArrayList < Bouncer > ();
        for (Bouncer myBouncer: myBouncers) {
            myBouncer.checkPaddleCollide(myPaddle1);
            myBouncer.checkPaddleCollide(myPaddle2);
            myBouncer.checkWallCollide(STAGE_WIDTH, STAGE_MARGIN);
            if (myBouncer.checkBottomCollide(STAGE_HEIGHT)) deadBouncers.add(myBouncer);

            myBouncer.move();

            for (Brick myBrick: myBricks) {
                if (myBouncer.checkBrickCollide(myBrick)) {
                    myBrick.decrementStrength();
                }
            }
        }

        myBouncers.removeAll(deadBouncers);
        root.getChildren().removeAll(deadBouncers);
    }

    /**
     * Moves all lasers up
     * Checks for collisions between lasers and bricks
     */
    private void handleLasers(){
        ArrayList < Laser > deadLasers = new ArrayList < > ();

        for (Laser myLaser: myLasers) {
            myLaser.moveUp();
            if (myLaser.checkTopCollide(STAGE_MARGIN)) {
                deadLasers.add(myLaser);
            }
            for (Brick myBrick: myBricks) {
                if (myBrick.checkLaserCollide(myLaser)) {
                    myBrick.decrementStrength();
                    deadLasers.add(myLaser);
                    playerScore += myBrick.brickScore;
                }
            }
        }
        myLasers.removeAll(deadLasers);
        root.getChildren().removeAll(deadLasers);
    }


    /**
     * Generates PowerUps if bricks at corresponding locations are dead
     * @param deadBrickIndices dead brick locations which contain powerups
     */
    private void initPowerUps(HashSet<String> deadBrickIndices) {
        for (String index: myPowerUpMap.keySet()) {
            if (deadBrickIndices.contains(index)) {
                initPowerUp(index);
            }
        }
    }

    private void initPowerUp(String index) {
        int rowIndex = Integer.parseInt(index.substring(0, 1));
        int colIndex = Integer.parseInt(index.substring(1));
        String powerUpName = myPowerUpMap.get(index);
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(powerUpName + ".gif"));

        PowerUp myPowerUp = new PowerUp(
                STAGE_PADDING_X + POWERUP_SPACING + colIndex * (POWERUP_SIZE + POWERUP_SPACING * 2),
                STAGE_MARGIN + STAGE_PADDING_Y + BRICK_HEIGHT * rowIndex,
                POWERUP_SIZE,
                POWERUP_SIZE,
                image,
                powerUpName
        );
        myPowerUps.add(myPowerUp);
        root.getChildren().add(myPowerUp);
    }

    /**
     * Checks to see if any PowerUps have collided with paddle or bottom of screen
     * @param deadPowerUps stores the PowerUp objects which have either collided with paddle or bottom of screen
     */
    private void handlePowerUps(ArrayList<PowerUp> deadPowerUps) {
        for (PowerUp myPowerUp: myPowerUps) {
            if (myPowerUp.checkPaddleCollide(myPaddle1) || myPowerUp.checkPaddleCollide(myPaddle2)) {
                handleNewPowerUp(myPowerUp);
                deadPowerUps.add(myPowerUp);
            } else if (myPowerUp.checkBottomCollide(STAGE_HEIGHT)) {
                deadPowerUps.add(myPowerUp);
            }
            myPowerUp.moveDown();
        }

        myPowerUps.removeAll(deadPowerUps);
        root.getChildren().removeAll(deadPowerUps);
    }

    /**
     * Gives user a power up based on the value specified by myPowerUp
     * @param myPowerUp the PowerUp objects which collided with the paddle
     */
    private void handleNewPowerUp(PowerUp myPowerUp) {
        String power = myPowerUp.getPower();
        switch (power) {
            case WIDE_PADDLE_POWER:
                widenPaddles();
            case SLOW_BOUNCERS_POWER:
                slowDownBouncers();
                break;
            case EXTRA_BOUNCER_POWER:
                initAdditionalBouncer();
            case LASER_POWER:
                enableLasers();
            case FAST_BOUNCERS_POWER:
                speedUpBouncers();
        }
    }

    private void widenPaddles(){
        myPaddle1.setWidth(PADDLE_WIDTH_WIDE);
        myPaddle2.setWidth(PADDLE_WIDTH_WIDE);
    }
    private void initAdditionalBouncer() {
        Bouncer powerBouncer = new Bouncer(
                (int) myPaddle1.getX(),
                STAGE_HEIGHT - PADDLE_HEIGHT - BOUNCER_HEIGHT,
                BOUNCER_WIDTH,
                BOUNCER_HEIGHT,
                Color.PLUM
        );

        myBouncers.add(powerBouncer);
        root.getChildren().add(powerBouncer);
        powerBouncer.start();
    }

    private void slowDownBouncers() {
        for (Bouncer myBouncer : myBouncers) {
            myBouncer.slowDown();
        }
    }

    private void speedUpBouncers() {
        for (Bouncer myBouncer : myBouncers) {
            myBouncer.speedUp();
        }
    }

    private void shootLasers() {
        if (lasersEnabled) {
            if (laserFramesLeft % FRAMES_BETWEEN_LASERS == 0) {
                initLaser();
            }
            laserFramesLeft -= 1;
            if (laserFramesLeft == 0) {
                disableLasers();
            }
        }
    }

    private void initLaser() {
        Laser myLaser = new Laser(
                (int)(myPaddle1.getX() + myPaddle1.getWidth() / 2 - LASER_WIDTH / 2),
                STAGE_HEIGHT - PADDLE_HEIGHT - LASER_HEIGHT,
                LASER_WIDTH,
                LASER_HEIGHT
        );
        myLasers.add(myLaser);
        root.getChildren().add(myLaser);
    }

    /**
     * Checks if all bricks and all bouncers are dead
     */
    private void handleDeadNodes() {
        if (myBricks.size() == 0) {
            handleAllBricksDead();
        } else if (myBouncers.size() == 0) {
            handleAllBouncersDead();
        }
    }

    /**
     * Handles when no more bouncers are on screen.
     * Either displays splashscreen or resets paddle/bouncer and decrements lives
     */
    private void handleAllBouncersDead() {
        System.out.println("no more bouncers");
        if (playerLives == 0) {
            displaySplashScreen(LOSE_MSG);
        } else {
            //if the player died but still has lives
            playerLives -= 1;
            initMainBouncer();
            resetPaddlePosition();
            pauseGame();
            myScene.addEventFilter(MouseEvent.MOUSE_CLICKED, handleMouseInput);

            System.out.println("new player lives" + playerLives);
        }
    }

    /**
     * Handles when all bricks are destroyed.
     * Either displays 'win' splash screen or moves to next level
     */
    private void handleAllBricksDead() {
        if (currentLevel == myBrickLayouts.size()) {
            displaySplashScreen(WIN_MSG);
        } else {
            currentLevel += 1;
            initLevel(currentLevel);
        }
    }

    /**
     * Generates a hashset of the indeces (row and col locations) of all dead bricks
     * If powerups are located at these dead brick indeces, they will be generated and dropped
     * @return hashset of dead brick locations
     */
    private HashSet<String> getDeadBrickIndeces(){
        ArrayList < Brick > deadBricks = new ArrayList < Brick > ();
        HashSet < String > deadBrickIndices = new HashSet < > ();

        for (Brick myBrick: myBricks) {
            myBrick.changeColor();

            if (myBrick.isDead()) {
                playerScore += myBrick.brickScore;
                deadBricks.add(myBrick);
                deadBrickIndices.add(myBrick.getIndex());
            }
        }

        myBricks.removeAll(deadBricks);
        root.getChildren().removeAll(deadBricks);

        return deadBrickIndices;
    }

    /**
     * Deletes all scene objects, effectively clearing the stage
     */
    private void clearOldSprites() {
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
        playerLives = PLAYER_START_LIVES;
    }

    private void resetPlayerScore() {
        playerScore = 0;
    }

    private void disableLasers() {
        lasersEnabled = false;
    }

    private void enableLasers() {
        lasersEnabled = true;
    }

    private void resetLaserFrames() {
        laserFramesLeft = FRAMES_BETWEEN_LASERS * LASER_COUNT;
    }

    /**
     * Event handler that moves ball and paddles to mouse location when game is paused
      */
    private void handleMouseMove(double x) {
        myPaddle1.moveTo(x);
        myPaddle2.moveTo(x);

        if (gamePaused) {
            mainBouncer.moveTo(x);
        }
    }

    /**
     * Event handler that handles keyboard cheatcode inputs
     * @param code cheat code
     */
    private void handleKeyInput(KeyCode code) {
        if (code.isDigitKey()) {
            if (code == KeyCode.DIGIT1) {
                currentLevel = 1;
            } else if (code == KeyCode.DIGIT2) {
                currentLevel = 2;
            } else if (code == KeyCode.DIGIT3) {
                currentLevel = 3;
            }
            initLevel(currentLevel);
            resetPlayerScore();
            resetPlayerLives();
        } else if (code == KeyCode.L) {
            playerLives += 1;
        } else if (code == KeyCode.S) {
            slowDownBouncers();
        } else if (code == KeyCode.A) {
            widenPaddles();
        } else if (code == KeyCode.B) {
            slowDownBouncers();
        } else if (code == KeyCode.C) {
            initAdditionalBouncer();
        } else if (code == KeyCode.D) {
            enableLasers();
        }  else if (code == KeyCode.E) {
            speedUpBouncers();
        }
    }
    /**
     * Event handler that handles mouse click to launch bouncer
     */
    EventHandler < MouseEvent > handleMouseInput = new EventHandler < MouseEvent > () {
        @Override
        public void handle(MouseEvent event) {
            if (gamePaused) {
                root.getChildren().remove(newLevelText);
                for (Bouncer bouncer: myBouncers) {
                    bouncer.start();
                }
                gamePaused = false;
            }
            myScene.removeEventHandler(MouseEvent.MOUSE_CLICKED, handleMouseInput);
        }
    };


    public static void main(String[] args) {
        /* Internally calls the init, start method */
        launch(args);
    }
}