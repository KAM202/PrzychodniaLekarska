package przychodnialekarska;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import przychodnialekarska.utils.UtilFunction;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        UtilFunction.loadButtons();
        WindowManager.getInstance().setMainWindow(primaryStage);
        File directory = new File("images");
        if(!directory.exists()) directory.mkdir();
        Parent parent = FXMLLoader.load(getClass().getResource("/fxml/LoginForm.fxml"));
        primaryStage.setTitle("System informatyczny obsługi przychodni lekarskiej - Logowanie");
        primaryStage.getIcons().add(new Image("/img/logo.png"));
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
