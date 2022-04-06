import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.control.Label;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Game extends GameApplication {

    public final int [][] PATH_1 = {{0, 80}, {80,80},{160, 80}, {160, 160}, {160, 240},{240, 240},{320,240},{320, 320},{320, 400}, {320,480}, {400,480}, {480,480},{560, 480},{640, 480},{720, 480},{800, 480},{880, 480},{960, 480},{1040, 480}};
    public final int [][] TOWERS_1 = {{80, 160},{480, 400},{880, 560}};

    public final int [][] PATH_2 = { {80,80},{160, 80}, {160, 160}, {160, 240}};

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
        entityBuilder()
                .view("gras.png")
                .zIndex(10)
                .anchorFromCenter()
                .scale(2, 2).anchorFromCenter()
                .buildAndAttach();

        entityBuilder().view("stone.jpg").zIndex(200).at(1000,0).scale(2, 2).buildAndAttach();

        for (int[] cords: TOWERS_1) {
            entityBuilder().view("platform.png").zIndex(210).at(cords[0],cords[1]).onClick(v->{
                v.setVisible(!v.isVisible());
            }).buildAndAttach();
        }


        var grid = new AStarGrid(1280/80,720/80);
        int cellWidth = 80;
        int cellHeight = 80;


        Enemy nee = new Enemy(60, 100, 60, "AIspeed", grid);

        grid.forEach(v -> {
            v.setState(CellState.NOT_WALKABLE);
        });

        for (int[] cords : PATH_1) {
            genMudPiece(cords[0],cords[1]);
            grid.get(cords[0]/cellWidth,cords[1]/cellHeight).setState(CellState.WALKABLE);
        }

        nee.walk(PATH_1[PATH_1.length -1][0],PATH_1[PATH_1.length - 1][1]);

    }


    // Plaats de modderpaden op de grid.
    private void genMudPiece(int x, int y){
        entityBuilder().view("mud.png").zIndex(11).at(x,y).buildAndAttach();
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

