package breakout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LevelGenerator {
    ArrayList< String[][] > myBrickLayouts = new ArrayList < > ();

    public ArrayList<String[][]> createBrickLayouts(String BRICK_LAYOUT_FILE) {
        try {
            // read in file of brick layouts for multiple levels
            File file = new File(getClass().getClassLoader().getResource(BRICK_LAYOUT_FILE).getFile());
            Scanner myReader = new Scanner(file);
            String[][] brickLayout = new String[3][6];

            storeLayoutsAsArray(myReader, brickLayout);

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found.");
            e.printStackTrace();
        }
        return myBrickLayouts;
    }

    public void storeLayoutsAsArray(Scanner myReader, String[][] brickLayout) {
        int brickRow = 0;
        while (myReader.hasNextLine()) {
            String line = myReader.nextLine();

            if (line.equals("-")) { //check if level has been completely processed
                myBrickLayouts.add(brickLayout); //store current layout in arraylist of all layouts
                brickRow = 0;
                brickLayout = new String[3][6];
            } else { // store each space-separated string in array
                String[] rowBricks = line.split(" ");
                brickLayout[brickRow] = rowBricks;
                brickRow += 1;
            }
        }
    }
}
