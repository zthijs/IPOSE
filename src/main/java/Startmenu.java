import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

public class Startmenu extends FXGLMenu{
    public Startmenu(@NotNull MenuType type){

        super(type);

        Button button = new Button("Start Game");
        Button button1 = new Button("Close Game");
        VBox vBox = new VBox(10);
        BorderPane pane = new BorderPane();

        vBox.getChildren().addAll(button,button1);
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinHeight(FXGL.getAppHeight());

        pane.setMinHeight(FXGL.getAppHeight());
        pane.setMinWidth(FXGL.getAppWidth());

        pane.setCenter(vBox);

        button.setOnAction(e -> { fireNewGame(); });
        button1.setOnAction(e ->{ fireExit(); });
        getContentRoot().getChildren().add(pane);

        BackgroundImage mainBackground = new BackgroundImage
                (new Image("assets/textures/gras.png", FXGL.getAppHeight(), FXGL.getAppWidth(), true, false),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                        BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT);
        getContentRoot().setBackground(new Background(mainBackground));
    }
}
