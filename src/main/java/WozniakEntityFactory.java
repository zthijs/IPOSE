import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class WozniakEntityFactory implements EntityFactory {

    private static final String [] IMAGES = {"heiko.png","vincent.png", "charlotte.png"};

    final double MINIMIZE_FACTOR_TO_SATISFACTION = 0.3;

    @Spawns("background")
    public Entity backGround(SpawnData data) {
        return entityBuilder(data)
            .view("gras.png")
            .zIndex(0)
            .scale(2, 2)
            .build();
    }

    @Spawns("stoneMenu")
    public Entity stoneMenu(SpawnData data){
        return entityBuilder(data)
            .view("stone.jpg")
            .zIndex(3000)
            .at(1000,0)
            .scale(2, 2)
            .build();
    }

    @Spawns("enemy")
    public Entity enemy(SpawnData data){

        AStarGrid grid = data.get("grid");
        int speed = data.get("speed");

        return entityBuilder(data)
            .viewWithBBox(IMAGES[new Random().nextInt(IMAGES.length)]).scale(MINIMIZE_FACTOR_TO_SATISFACTION, MINIMIZE_FACTOR_TO_SATISFACTION)
            .with(new CellMoveComponent(80,80,speed))
            .with(new AStarMoveComponent(grid))
            .zIndex(20)
            .anchorFromCenter()
            .collidable()
            .type(EntityTypes.ENEMY)
            .build();
    }

    @Spawns("mudPiece")
    public Entity mudPiece (SpawnData data){
        return entityBuilder(data)
            .view("mud.png")
            .zIndex(2)
            .build();
    }

    @Spawns("pathEnd")
    public Entity pathEnd(SpawnData data){

        return entityBuilder(data)
            .viewWithBBox(new Rectangle(80,80,Color.TRANSPARENT))
            .type(EntityTypes.PATH_END)
            .zIndex(0)
            .collidable()
            .build();
    }

    @Spawns("platform")
    public Entity platform(SpawnData data){
        return entityBuilder(data)
            .view("platform.png")
            .zIndex(25)
            .onClick(v->{
                if(FXGL.geti("money") >= 50){
                    FXGL.inc("money", -50);
                    FXGL.play("build.wav");
                    v.removeFromWorld();
                    spawn("tower1", v.getX() - 40, v.getY()-40);
                }else{
                    FXGL.play("notallowed.wav");
                }
            })
            .build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        
        Point2D nee = new Point2D(data.getX(), data.getY());
        List<Entity> closest = FXGL.getGameWorld().getEntitiesFiltered(e -> e.isType(EntityTypes.ENEMY));
        if(closest.size() < 1) {
            return entityBuilder(data).build();
        }else {
            FXGL.play("Gunfire.wav");
            return entityBuilder(data)
            .type(EntityTypes.BULLET)
            .viewWithBBox("bullit.png")
            .collidable()
            .zIndex(1000)
            .with(new ProjectileComponent(closest.get(0).getPosition().subtract(nee), 1250))
            .with(new OffscreenCleanComponent())
            .build();
        }

        
    }

    @Spawns("tower1")
    public Entity tower1(SpawnData data){
        FXGL.getGameTimer().runAtInterval(()->{
            spawn("bullet", data.getX(),data.getY());
        }, Duration.millis(3000));

        return entityBuilder(data)
            .view("tower1.png")
            .zIndex(25)
            .anchorFromCenter()
            .onClick(v->{
                if(FXGL.geti("money") >= 150){
                    FXGL.inc("money", -150);
                    FXGL.play("build.wav");
                    v.removeFromWorld();
                    spawn("tower2", v.getX(), v.getY());
                }else{
                    FXGL.play("notallowed.wav");
                }
            })
            .build();
    }

    @Spawns("tower2")
    public Entity tower2(SpawnData data){

        FXGL.getGameTimer().runAtInterval(()->{
            spawn("bullet", data.getX(),data.getY());
        }, Duration.millis(2000));

        return entityBuilder(data)
            .view("tower2.png")
            .anchorFromCenter()
            .zIndex(25)
            .onClick(v->{
                if(FXGL.geti("money") >= 400){
                    FXGL.inc("money", -400);
                    FXGL.play("build.wav");
                    v.removeFromWorld();
                    spawn("tower3", v.getX(), v.getY());
                }else{
                    FXGL.play("notallowed.wav");
                }
            })
            .build();
    }

    @Spawns("tower3")
    public Entity tower3(SpawnData data){
        FXGL.getGameTimer().runAtInterval(()->{
            spawn("bullet", data.getX(),data.getY());
        }, Duration.millis(1000));

        return entityBuilder(data)
            .view("tower3.png")
            .anchorFromCenter()
            .zIndex(25)
            .onClick(v->{
                FXGL.play("notallowed.wav");
            })
            .build();
    }
}
