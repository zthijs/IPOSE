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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Game extends GameApplication {
    private int [][] mudPaths = {{0, 80}, {80,80},{160, 80}, {160, 160}, {160, 240},{240, 240},{320,240},{320, 320},{320, 400}, {320,480}, {400,480}, {480,480},{560, 480},{640, 480},{720, 480},{800, 480},{880, 480},{960, 480},{1040, 480}};
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
        entityBuilder()
                .view("gras.png")
                .zIndex(10)
                .anchorFromCenter()
                .scale(2, 2).anchorFromCenter()
                .buildAndAttach();

        entityBuilder().view("stone.jpg").zIndex(200).at(1000,0).scale(2, 2).buildAndAttach();


        var grid = new AStarGrid(1280/80,720/80);
        int cellWidth = 80;
        int cellHeight = 80;


        Enemy nee = new Enemy(60, 100, 60, "AIspeed", grid);

        grid.forEach(huts -> {
            huts.setState(CellState.NOT_WALKABLE);
        });

        for (int[] cords : mudPaths) {
            genMudPiece(cords[0],cords[1]);
            grid.get(cords[0]/cellWidth,cords[1]/cellHeight).setState(CellState.WALKABLE);
        }

        nee.walk(mudPaths[mudPaths.length -1][0],mudPaths[mudPaths.length - 1][1]);



        
    }

    private void genMudPiece(int x, int y){

        entityBuilder()
                .view("mud.png")
                .zIndex(11).at(x,y)
                .onClick(f->{

                })
                .buildAndAttach();
    }


    //fix dit, moet EntityType & Entity opgeven, moet var van klasse aanpassen
    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.BULLET, EntityTypes.ENEMY) {
            @Override
            protected void onCollision(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();

                //bullet.getDamage bestaat nog niet, tijdelijk gecomment hieronder
                //enemy.setHealth(enemy.getHealth() - bullet.getDamage());

                //if(enemy.getHealth() <= 0) {
                //    enemy.remove();
                //}



                FXGL.inc("score", +1);

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
        if (var == true) {
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


