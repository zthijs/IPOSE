import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Enemy{
    private int health;
    private  int speed;
    private  int score;

    private String type;
    private static final String HEIKO_IMAGE = "heiko.png";
    private static final String VINCENT_IMAGE = "vincent.png";
    private static final String CHARLOTTE_IMAGE = "charlotte.png";


    private Entity ai;

    public  Enemy(int health, int speed, int score, String type, AStarGrid grid) {

        this.health = 50;
        this.speed = 2;
        this.score = 10;
        this.type = type;

        final double MINIMIZE_FACTOR_TO_SATISFACTION = 0.3;

        this.ai = entityBuilder()
                .viewWithBBox(HEIKO_IMAGE).scale(MINIMIZE_FACTOR_TO_SATISFACTION, MINIMIZE_FACTOR_TO_SATISFACTION)
                .with(new CellMoveComponent(80,80,speed))
                .with(new AStarMoveComponent(grid))
                .zIndex(120).at(-160,0).anchorFromCenter()
                .type(EntityTypes.ENEMY)
                .buildAndAttach();
    }

    public void walk(int x, int y) {
        this.ai.getComponent(AStarMoveComponent.class).moveToCell(x/80,y/80);
    }

    public int getHealth() {
        return health;
    }

    public void remove(){
        ai.removeFromWorld();
    }

    public void setHealth(int new_health) {
        this.health = new_health;
    }

    public int getScore() {
        return score;
    }
}
