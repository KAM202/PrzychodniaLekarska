package przychodnialekarska;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Stack;

public class WindowManager {
    private static final WindowManager INSTANCE = new WindowManager();

    private Stack<Stage> windows;

    private WindowManager(){
        windows = new Stack();
    }

    public void setMainWindow(Stage stage){
        windows.push(stage);
    }

    public Stage getCurrentWindow(){
        return windows.lastElement();
    }

    public static WindowManager getInstance() {
        return INSTANCE;
    }

    public Stage newWindow() {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(windows.lastElement());
        windows.push(stage);
        stage.setOnCloseRequest(event -> {
            windows.remove(stage);
        });
        return stage;
    }

    public Stage getMainWindow() {
        return windows.firstElement();
    }

    private void setHandlers(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, getEscKeyHandler());
    }

    public EventHandler<KeyEvent> getEscKeyHandler() {
        EventHandler<KeyEvent> escKeyHandler = null;
        if(escKeyHandler == null) {
            escKeyHandler = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(event.getCode() == KeyCode.ESCAPE) {
                        closeWindow();
                    }
                }
            };
        }
        return escKeyHandler;
    }


    public Stage createWindow(Parent view) {
        Stage stage = newWindow();
        Scene scene = view.getScene();
        if(scene == null) {
            scene = new Scene(view);
            setHandlers(scene);
        }
        stage.setScene(scene);
        return stage;
    }

    public void closeWindow() {
        windows.lastElement().close();
    }

}
