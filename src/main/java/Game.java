import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import com.almasb.fxgl.time.TimerAction;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;

public class Game extends GameApplication {

    private final int CELL_WIDTH = 80;
    private final int CELL_HEIGHT = 80;

    private final AStarGrid GRID = new AStarGrid(1280/CELL_WIDTH,720/CELL_HEIGHT);

    private final int [][] PATH_1 = {{0, 80}, {80,80},{160, 80}, {160, 160}, {160, 240},{240, 240},{320,240},{320, 320},{320, 400}, {320,480}, {400,480}, {480,480},{560, 480},{640, 480},{720, 480},{800, 480},{880, 480},{960, 480},{1040, 480}};
    private final int [][] PATH_2 = {{0,640},{80, 640}, {120, 640}, {160, 640},{160,560},{160,480},{160,400}, {240,240},{240,320},{240,400},{240,320},{320,240},{400,240},{480,240},{480,320},{480,240},{480,400},{480,480},{560,480},{640,480},{640,400},{640,320},{640,240},{640,160},{560,160},{400,240},{560,80},{560,0},{640,0},{720,0},{800,0},{880,0},{920,0}};

    public final int [][] TOWERS_1 = {{80, 160},{480, 400},{880, 560}};

    public static void main (String[] args) {
        launch(args);
    }

    private final WozniakEntityFactory wozniakEntityFactory = new WozniakEntityFactory();

    @Override
    protected void initSettings(GameSettings s) {
        s.setTitle("Wozniak defense");
        s.setVersion("");
        s.setAppIcon("wozniak.png");
        s.setIntroEnabled(false);
        s.setFullScreenFromStart(true);
        s.setWidth(1280);
        s.setHeight(720);
        s.setMainMenuEnabled(false);

    }

    protected void initGame() {

        FXGL.getGameWorld().addEntityFactory(this.wozniakEntityFactory);

        // Stel de achtergrond en menu in op specifieke z indexen.
        spawn("background");
        spawn("stoneMenu");
        spawn("pathEnd",PATH_1[PATH_1.length - 1][0], PATH_1[PATH_1.length - 1][1]);

        // Maak op de grid elke tegel niet toegangbaar.
        GRID.forEach(tile -> {
            tile.setState(CellState.NOT_WALKABLE);
        });
        
        // Maak alle tegels op het pad toegangbaar.
        for (int[] cords : PATH_1) {
            spawn("mudPiece", cords[0], cords[1]);
            GRID.get(cords[0]/CELL_WIDTH,cords[1]/CELL_HEIGHT).setState(CellState.WALKABLE);
        }

        // Plaats de torens langs het pad.
        for (int[] cords: TOWERS_1) {
            spawn("platform", cords[0], cords[1]);
        }

        startWave(1,10,1000);
        startWave(1,10,1000);




    }

    private Entity spawnEnemy(){
        return spawn("enemy", new SpawnData(-160,0).put("grid", GRID).put("speed", 500));
    }

    private void startWave(int waveNumber, int enemyAmount, int interval){
        AtomicInteger count = new AtomicInteger();
        FXGL.getGameTimer().runAtInterval(() -> {
            if(count.get() < enemyAmount){
                count.set(count.get() + 1);
                spawnEnemy().getComponent(AStarMoveComponent.class).moveToCell(PATH_1[PATH_1.length - 1][0]/80, PATH_1[PATH_1.length - 1][1]/80);
            }
        }, Duration.millis(interval));
    }

    //getGameTimer().runAtInterval(()->{})


    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.ENEMY, EntityTypes.PATH_END) {
            @Override
            protected void onCollision(Entity enemy, Entity path_end) {
                FXGL.play("beep.wav");
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
        var music = FXGL.getAssetLoader().loadMusic("audio.wav");

        FXGL.onKey(KeyCode.P, () -> {
            FXGL.getAudioPlayer().playMusic(music);
    
        });
        FXGL.onKey(KeyCode.L, () -> {
            FXGL.getAudioPlayer().stopMusic(music);
    
        });
    }
}


