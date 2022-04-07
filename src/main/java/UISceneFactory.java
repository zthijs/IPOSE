import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.MenuType;


public class UISceneFactory extends SceneFactory{
    @Override
    public FXGLMenu newMainMenu(){
        return new Startmenu(MenuType.MAIN_MENU);
    }
}