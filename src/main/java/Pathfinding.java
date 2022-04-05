import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class Pathfinding extends GameApplication {

    private Entity ai;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setHeight(720);
        gameSettings.setWidth(1280);
    }

    @Override
    protected void initGame(){
        var grid = new AStarGrid(1280/40,720/40);
        int cellWidth = 40;
        int cellHeight = 40;
        int AIspeed = 200;

        ai = entityBuilder()
                .viewWithBBox(new Rectangle(40,40, Color.CRIMSON))
                .with(new CellMoveComponent(cellWidth,cellHeight,AIspeed))
                .with(new AStarMoveComponent(grid))
                .zIndex(1)
                .anchorFromCenter()
                .buildAndAttach();

        for (int y = 0; y < 720/cellHeight; y++) {
            for (int x = 0; x < 1280/cellWidth; x++) {
                final var finalX = x;
                final var finalY = y;
                var view = new Rectangle(cellWidth,cellHeight);
                view.setStroke(Color.LIGHTGRAY);

                var e = entityBuilder()
                        .at(x * cellWidth, y * cellHeight)
                        .view(view)
                        .buildAndAttach();

                e.getViewComponent().addOnClickHandler(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        ai.getComponent(AStarMoveComponent.class).moveToCell(finalX,finalY);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        grid.get(finalX,finalY).setState(CellState.NOT_WALKABLE);
                        view.setFill(Color.BLACK);
                    }
                });
            }

        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
