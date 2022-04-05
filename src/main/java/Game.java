import java.util.ArrayList;
import java.util.Arrays;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import javafx.scene.text.Text;

public class Game extends GameApplication {

    private int [][] = {}
    public static void main (String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Wozniak defense");
        settings.setVersion("");
        settings.setAppIcon("wozniak.png");
        settings.setIntroEnabled(false);
        settings.setFullScreenFromStart(true);
        settings.setWidth(1000);
        settings.setHeight(800);
        
    }

    protected void initGame() {
        FXGL.getGameScene().setBackgroundRepeat(FXGL.image("gras.png"));
        genMudPiece(0, 80);
        genMudPiece(80, 80);
        genMudPiece(160, 80);
        genMudPiece(160, 160);
        genMudPiece(160, 240);
        genMudPiece(160, 320);


        
    }

    private void genMudPiece(double x, double y){
        Texture brickTexture = FXGL.getAssetLoader().loadTexture("mud.png");
        brickTexture.setTranslateX(x);
        brickTexture.setTranslateY(y);
        FXGL.getGameScene().addUINode(brickTexture);
    }


}

