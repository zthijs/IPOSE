import com.almasb.fxgl.app.FXGLApplication;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.physics.CollisionHandler;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Game extends GameApplication {

	private final int CELL_WIDTH = 80;
	private final int CELL_HEIGHT = 80;

	private int killcount = 0;

	private final AStarGrid GRID = new AStarGrid(1280 / CELL_WIDTH, 720 / CELL_HEIGHT);

	private final int [][] PATH = {{0, 80}, {80,80},{160, 80}, {160, 160}, {160, 240},{240, 240},{320,240},{320, 320},{320, 400}, {320,480}, {400,480}, {480,480},{560, 480},{640, 480},{720, 480},{800, 480},{880, 480},{960, 480},{1040, 480}};
    
    public final int [][] TOWERS = {{80, 400},{480, 340},{880, 160}};

	//nodig voor gameEnd
	private boolean gameActive = true;

	public static void main(String[] args) {

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
		s.setMainMenuEnabled(true);
		s.setSceneFactory(new UISceneFactory());

	}

	protected void initGame() {

		FXGL.getGameWorld().addEntityFactory(this.wozniakEntityFactory);

		// Stel de achtergrond en menu in op specifieke z indexen.
		spawn("background");
		spawn("stoneMenu");
		spawn("pathEnd", PATH[PATH.length - 1][0], PATH[PATH.length - 1][1]);

		// Maak op de grid elke tegel niet toegangbaar.
		GRID.forEach(tile -> {
			tile.setState(CellState.NOT_WALKABLE);
		});

		// Maak alle tegels op het pad toegangbaar.
		for (int[] cords: PATH) {
			spawn("mudPiece", cords[0], cords[1]);
			GRID.get(cords[0] / CELL_WIDTH, cords[1] / CELL_HEIGHT).setState(CellState.WALKABLE);
		}

		// Plaats de torens langs het pad.
		for (int[] cords: TOWERS) {
			spawn("platform", cords[0], cords[1]);
		}

		startWave(10, 1050, 0, 100);
		startWave(8, 500, 35, 200);
		startWave(18, 300, 53, 100);
		startWave(30, 700, 80, 220);
        startWave(40, 300, 110, 180);
		startWave(50, 300, 130, 300);

	}

	private Entity spawnEnemy(int speed) {
		return spawn("enemy", new SpawnData(-160, 0).put("grid", GRID).put("speed", speed));
	}

	private void startWave(int enemyAmount, int interval, int delay, int aiSpeed) {
		if (gameActive) {
			AtomicInteger count = new AtomicInteger();

			FXGL.runOnce(() -> {
				FXGL.inc("wave", 1);
				FXGL.run(() -> {
					if (gameActive) {
						count.set(count.get() + 1);
						spawnEnemy(aiSpeed).getComponent(AStarMoveComponent.class).moveToCell(PATH[PATH.length - 1][0] / 80, PATH[PATH.length - 1][1] / 80);
					}
				}, Duration.millis(interval), enemyAmount);
			}, Duration.seconds(delay));
		}
	}

	public void onUpdate(double tpf) {
		if (FXGL.geti("health")<= 0 || killcount == 156) {
			gameActive = false;
			String string;
			if(FXGL.geti("health")<= 0){
				string = "Je bent dood!";
			} else if(killcount == 158){
				string = "Je hebt het gehaald!";
			} else {
				string = "";
			}
			getDialogService().showInputBox(string + " Je hebt een score van " + FXGL.geti("score") + " punten! Wat is je naam?", answer -> {
                VBox content = new VBox();

                try {
                    FileWriter myWriter = new FileWriter("data.txt", true);
                    myWriter.write(answer + "," + FXGL.geti("score") + "\n");
                    myWriter.close();
                  } catch (IOException e) {
                    getDialogService().showErrorBox("Er ging wat mis met het score bord", ()->{});
                }

                try {
                    Scanner scanner = new Scanner(new File("data.txt"));
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        content.getChildren().add(getUIFactoryService().newText(line.split(",")[0] + " - " + line.split(",")[1]));
                    }
                } catch (Exception e) {
                    getDialogService().showErrorBox("Er ging wat mis met het score bord", ()->{});
                }
                
				Button btnClose = getUIFactoryService().newButton("Stop spel");
				btnClose.setOnAction(a -> {
					
                    System.exit(0);

				});
				btnClose.setPrefWidth(300);

				getDialogService().showBox("Score bord:", content, btnClose);
			});
		}
	}

	@Override
	protected void initPhysics() {
		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.ENEMY, EntityTypes.PATH_END) {
			@Override
			protected void onCollision(Entity enemy, Entity path_end) {
				FXGL.play("beep.wav");
				enemy.removeFromWorld();
				killcount++;
				FXGL.inc("health", -1);
			}
		});

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.ENEMY, EntityTypes.BULLET) {
			@Override
			protected void onCollision(Entity enemy, Entity bull) {
				FXGL.inc("score", 5);
				FXGL.inc("money", 10);

				bull.removeFromWorld();
				enemy.removeFromWorld();
				killcount++;
			}
		});
	}

	private void makeLabel(String labelContents, int Xcoord, int Ycoord, boolean
		var) {
		Label label = new Label(labelContents);
		label.setStyle(
			"-fx-font-size: 20; " +
			"-fx-text-fill: white;" +
			"-fx-font-weight: bold");
		label.setFont(Font.loadFont("resources/fonts/pixel.ttf", 20));
		label.setTranslateX(Xcoord);
		label.setTranslateY(Ycoord);
		FXGL.getGameScene().addUINode(label);
		if (var) {
			label.textProperty().bind(FXGL.getWorldProperties().intProperty(labelContents).asString());
		}
	}

	@Override
	protected void initUI() {
		makeLabel("Score: ", 1016, 12, false);
		makeLabel("Health: ", 1016, 44, false);
		makeLabel("Money: ", 1016, 76, false);
		makeLabel("Wave: ", 1016, 300, false);

		makeLabel("score", 1090, 12, true);
		makeLabel("health", 1090, 44, true);
		makeLabel("money", 1090, 76, true);
		makeLabel("wave", 1090, 300, true);

		makeLabel("Tower 1: 50", 1016, 120, false);
		makeLabel("Tower 2: 150", 1016, 152, false);
		makeLabel("Tower 3: 400", 1016, 184, false);
	}

	@Override
	protected void initGameVars(Map<String, Object> vars) {
		vars.put("score", 0);
		vars.put("health", 10);
		vars.put("money", 50);
		vars.put("wave", 0);
	}

	//key input
	@Override
	protected void initInput() {
		var music = FXGL.getAssetLoader().loadMusic("audio.wav");
		FXGL.getAudioPlayer().playMusic(music);

		FXGL.onKey(KeyCode.P, () -> {
			FXGL.getAudioPlayer().playMusic(music);

		});
		FXGL.onKey(KeyCode.L, () -> {
			FXGL.getAudioPlayer().stopMusic(music);

		});
	}
}