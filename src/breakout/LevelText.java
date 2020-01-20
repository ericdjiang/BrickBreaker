package breakout;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * This class holds a Text object which display data about the current level
 *
 * @author  Eric Jiang
 * @version 1.0
 * @since   2020-01-20
 */

public class LevelText extends Text{
    LevelText(int startX, int startY, int TEXT_WIDTH, String msg,  int fontSize){
        super(startX, startY, msg);
        setFill(Color.WHITE);
        setWrappingWidth(TEXT_WIDTH);
        setTextAlignment(TextAlignment.CENTER);

        setFont(Font.font ("Verdana", fontSize));
    }
}
