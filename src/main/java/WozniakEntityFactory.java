import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class WozniakEntityFactory implements EntityFactory {

    private static final String HEIKO_IMAGE = "heiko.png";
    private static final String VINCENT_IMAGE = "vincent.png";
    private static final String CHARLOTTE_IMAGE = "charlotte.png";

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
            .zIndex(20)
            .at(1000,0)
            .scale(2, 2)
            .build();
    }

    @Spawns("enemy")
    public Entity enemy(SpawnData data){

        AStarGrid grid = data.get("grid");
        int speed = data.get("speed");

        return entityBuilder(data)
            .viewWithBBox(HEIKO_IMAGE).scale(MINIMIZE_FACTOR_TO_SATISFACTION, MINIMIZE_FACTOR_TO_SATISFACTION)
            .with(new CellMoveComponent(80,80,speed))
            .with(new AStarMoveComponent(grid))
            .zIndex(120)
            .anchorFromCenter()
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
            .viewWithBBox(new Rectangle(80,80,Color.RED))
            .type(EntityTypes.PATH_END)
                .zIndex(5000)
            .with(new CollidableComponent(true))
            .build();
    }

    @Spawns("platform")
    public Entity platform(SpawnData data){
        return entityBuilder(data)
            .view("platform.png")
            .zIndex(25)
            .build();
    }

    @Spawns("tower")
    public Entity tower(SpawnData data){
        return entityBuilder(data)
                .view("tower1.png")
                .zIndex(100)
                .build();
    }
}
