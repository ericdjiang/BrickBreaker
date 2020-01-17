package breakout;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class LevelText extends Text{
    LevelText(int startX, int startY, int TEXT_WIDTH, String msg,  int fontSize){
        super(startX, startY, msg);
        setFill(Color.WHITE);
        setWrappingWidth(TEXT_WIDTH);
        setTextAlignment(TextAlignment.CENTER);

        setFont(Font.font ("Verdana", fontSize));
    }
}
