import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Game extends GameApplication {
    private Entity ai;
    private int [][] mudPaths = {{0, 80}, {80,80},{160, 80}, {160, 160}, {160, 240},{240, 240},{320,240},{320, 320},{320, 400}, {320,480}, {400,480}, {480,480},{560, 480},{640, 480},{720, 480},{800, 480},{880, 480},{960, 480}};
    private int [][] mudPaths2 = { {80,80},{160, 80}, {160, 160}, {160, 240}};
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
        settings.setWidth(1280);
        settings.setHeight(720);
        
    }

    protected void initGame() {
        FXGL.getGameScene().setBackgroundRepeat(FXGL.image("gras.png"));



        var grid = new AStarGrid(1280/80,720/80);
        int cellWidth = 80;
        int cellHeight = 80;
        int AIspeed = 200;

        ai = entityBuilder()
                .viewWithBBox(new Rectangle(80,80, Color.CRIMSON))
                .with(new CellMoveComponent(cellWidth,cellHeight,AIspeed))
                .with(new AStarMoveComponent(grid))
                .zIndex(10).at(0,0)
                .anchorFromCenter()
                .buildAndAttach();
                
        for (int y = 0; y < 720/cellHeight; y++) {
            for (int x = 0; x < 1280/cellWidth; x++) {
                final var finalX = x;
                final var finalY = y;
                var view = new Rectangle(cellWidth,cellHeight);
                //view.setStroke(Color.LIGHTGRAY);

                var e = entityBuilder()
                        .at(x * cellWidth, y * cellHeight)
                        .view(view)
                        .buildAndAttach();

                e.getViewComponent().addOnClickHandler(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        ai.getComponent(AStarMoveComponent.class).moveToCell(finalX,finalY);
                    }
                });
            }
        }


        grid.forEach(huts -> {
            huts.setState(CellState.NOT_WALKABLE);
        });

        for (int[] cords : mudPaths) {
            genMudPiece(cords[0],cords[1]);
            grid.get(cords[0]/cellWidth,cords[1]/cellHeight).setState(CellState.WALKABLE);
        }



        
    }

    private void genMudPiece(double x, double y){
        Texture brickTexture = FXGL.getAssetLoader().loadTexture("mud.png");
        brickTexture.setTranslateX(x);
        brickTexture.setTranslateY(y);
        FXGL.getGameScene().addUINode(brickTexture);
    }

    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.BULLET, EntityTypes.ENEMY) {
            @Override
            protected void onCollision(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();
                //line hieronder wil je schade toepassen bij de enemy entity
                //if statement -> enemy.removeFromWorld() als enemy.health <= 0?
                FXGL.inc("score", +1);
            }
        });
    }

    @Override
    protected void initUI(){
        //method voor het maken van tekstelementen binnen UI -> DRY
        Label scoreText = new Label("Score: ");
        scoreText.setTranslateX(5);
        //scoreText.setTranslateY(0);

        Label score = new Label();
        score.setTranslateX(40);
        //score.setTranslateY(0);

        score.textProperty().bind(FXGL.getWorldProperties().intProperty("score").asString());

        FXGL.getGameScene().addUINode(score);
        FXGL.getGameScene().addUINode(scoreText);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("score",0);
    }


}

