package przychodnialekarska;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import przychodnialekarska.utils.Variables;

import java.sql.SQLException;
import java.util.Stack;

public class WindowManager {
    private static final WindowManager INSTANCE = new WindowManager();

    private Stack<Stage> windows;

    private WindowManager(){
        windows = new Stack();
    }

    public static void switchScene(ActionEvent e){
        int numer = Integer.valueOf(((Node) e.getSource()).getId());
        System.out.println(numer);

        String resourceName = Variables.resourceWindows.get(numer);

        try {
            if(resourceName != null){
                Parent root = FXMLLoader.load(Main.class.getResource(resourceName));

                Stage stage = getInstance().createWindow(root);
                stage.getIcons().add(new Image("/img/logo.png"));

                stage.setTitle(Variables.windowsNames.get(numer));
                stage.show();
            }
        }catch(Exception x){
            x.printStackTrace();
        }
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
        stage.setResizable(false);
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
        stage.setResizable(false);
        stage.setScene(scene);
        return stage;
    }

    public void closeWindow() {

        windows.lastElement().close();
        try {
            DatabaseManager.closeConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //if(ServiceController.thread != null) ServiceController.thread.interrupt();
    }

}
