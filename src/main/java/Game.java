import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Game extends GameApplication {
    private Entity ai;
    private int [][] mudPaths = {{0, 80}, {80,80},{160, 80}, {160, 160}, {160, 240},{240, 240},{320,240},{320, 320},{320, 400}, {320,480}, {400,480}, {480,480},{560, 480},{640, 480},{720, 480},{800, 480},{880, 480},{960, 480}};
    public static void main (String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Wozniak defense");
        settings.setDeveloperMenuEnabled(false);
        settings.setVersion("");
        settings.setAppIcon("wozniak.png");
        settings.setIntroEnabled(false);
        settings.setFullScreenFromStart(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setMainMenuEnabled(true);

        
    }

    protected void initGame() {
        FXGL.getGameScene().setBackgroundRepeat(FXGL.image("gras.png"));

        entityBuilder()
                .view("gras.png")
                .zIndex(10)
                .anchorFromCenter()
                .scale(2, 2)
                .buildAndAttach();

        for (int[] cords : mudPaths) {
            genMudPiece(cords[0],cords[1]);
        }
        
        FXGL.getAudioPlayer().playMusic(FXGL.getAssetLoader().loadMusic("audio.wav"));
        


        var grid = new AStarGrid(1280/40,720/40);
        int cellWidth = 40;
        int cellHeight = 40;
        int AIspeed = 200;

        ai = entityBuilder()
                .viewWithBBox(new Rectangle(40,40, Color.CRIMSON))
                .with(new CellMoveComponent(cellWidth,cellHeight,AIspeed))
                .zIndex(10)
                .anchorFromCenter()
                .buildAndAttach();

        

        for (int[] cord : mudPaths) {
            ai.translate(cord[0], cord[1]);
        }
                
       
    }

    private void genMudPiece(double x, double y){
        Texture brickTexture = FXGL.getAssetLoader().loadTexture("mud.png");
        brickTexture.setTranslateX(x);
        brickTexture.setTranslateY(y);
        FXGL.getGameScene().addUINode(brickTexture);
    }


}

