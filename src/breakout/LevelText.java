package breakout;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LevelText extends Text{
    LevelText(int startX, int startY, String msg,  int fontSize){
        super(startX, startY, msg);
        setFill(Color.WHITE);
    }
}
