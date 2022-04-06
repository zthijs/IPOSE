import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;

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

        return entityBuilder()
        .viewWithBBox(HEIKO_IMAGE).scale(MINIMIZE_FACTOR_TO_SATISFACTION, MINIMIZE_FACTOR_TO_SATISFACTION)
        .with(new CellMoveComponent(80,80,speed))
        .with(new AStarMoveComponent(grid))
        .zIndex(5).at(-160,0).anchorFromCenter()
        .type(EntityTypes.ENEMY)
        .with(new CollidableComponent(true))
        .build();
    }
}
