import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.physics.CollisionHandler;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Game extends GameApplication {

    private final int [][] PATH_1 = {{0, 80}, {80,80},{160, 80}, {160, 160}, {160, 240},{240, 240},{320,240},{320, 320},{320, 400}, {320,480}, {400,480}, {480,480},{560, 480},{640, 480},{720, 480},{800, 480},{880, 480},{960, 480},{1040, 480}};
    private final int [][] PATH_2 = { {80,80},{160, 80}, {160, 160}, {160, 240}};
    private Entity endpoint;

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

        entityBuilder().view("stone.jpg").zIndex(1000).at(1000,0).scale(2, 2).buildAndAttach();


        int cellWidth = 80;
        int cellHeight = 80;
        var grid = new AStarGrid(1280/cellWidth,720/cellHeight);


        //Enemy nee = new Enemy(60, 500, 60, "AIspeed", grid);

        grid.forEach(huts -> {
            huts.setState(CellState.NOT_WALKABLE);
        });

        for (int[] cords : PATH_1) {
            genMudPiece(cords[0],cords[1]);
            grid.get(cords[0]/cellWidth,cords[1]/cellHeight).setState(CellState.WALKABLE);
        }



        endpoint = FXGL.entityBuilder()
                .viewWithBBox(new Rectangle(80,80,Color.TRANSPARENT))
                .zIndex(-100)
                .at(1040,480)
                .type(EntityTypes.PATH_END)
                .with(new CollidableComponent(true))
                .buildAndAttach();


        //level 1
        startWave(1,10,1000);

        FXGL.getGameTimer().runAtInterval(() -> {
            Enemy nee = new Enemy(60, 500, 60, "AIspeed", grid);
            nee.walk(PATH_1[PATH_1.length -1][0],PATH_1[PATH_1.length - 1][1]);
        }, Duration.millis(1000));
    }

    private int startWave(int waveNumber, int enemyAmount, int interval){
        AtomicInteger count = new AtomicInteger();
        FXGL.getGameTimer().runAtInterval(() -> {
            count.set(count.get() + 1);
            Enemy enemy = new Enemy(100, 500, 50, "AIspeed",grid);
            enemy.walk(PATH_1[PATH_1.length - 1][0], PATH_1[PATH_1.length - 1][1]);
            if(count.get() >= enemyAmount){
                return waveNumber;
            }
        }, Duration.millis(interval));

    }

    private void genMudPiece(int x, int y){

        entityBuilder()
                .view("mud.png")
                .zIndex(11).at(x,y)
                .onClick(f->{

                })
                .buildAndAttach();
    }

    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.ENEMY, EntityTypes.PATH_END) {
            @Override
            protected void onCollision(Entity enemy, Entity endpoint) {
                enemy.removeFromWorld();
                FXGL.inc("score", -5);
                FXGL.inc("health",-1);
            }
        });
    }

    private void makeLabel(String labelContents, int Xcoord, int Ycoord, boolean var){
        Label label = new Label(labelContents);
        label.setStyle(
                "-fx-font-family: 'Comic Sans MS'; " +
                "-fx-font-size: 20; " +
                "-fx-text-fill: white;");
        label.setTranslateX(Xcoord);
        label.setTranslateY(Ycoord);
        FXGL.getGameScene().addUINode(label);
        if (var) {
            label.textProperty().bind(FXGL.getWorldProperties().intProperty(labelContents).asString());
        }
    }

    @Override
    protected void initUI(){
        makeLabel("Score: ",1016,12,false);
        makeLabel("Health: ",1016,44,false);
        makeLabel("Money: ",1016,76,false);
        makeLabel("score",1090,12,true);
        makeLabel("health",1090,44,true);
        makeLabel("money",1090,76,true);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("score",0);
        vars.put("health",20);
        vars.put("money",100);
    }

    //key input
    @Override
    protected void initInput() {
        FXGL.onKey(KeyCode.W, () -> {
            //entity.translateY(-7);
        });
    }
}


