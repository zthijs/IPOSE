
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
public class Tower  {

    private Entity ai;
    private int value;
    private int damage;

    private static final String TOWER1_IMAGE = "tower1.png";

    public Tower(int value, int damage){
        this.value = value;
        this.damage = damage;

        this.ai = entityBuilder()
                .scale(0.4 ,0.4)
                .view(TOWER1_IMAGE)
                .zIndex(11).at(500, 300)
                .buildAndAttach();

    }

}
