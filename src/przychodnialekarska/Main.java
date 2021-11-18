package przychodnialekarska;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import przychodnialekarska.controller.MenuController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {

    public static ArrayList<Button> buttonArrayList = new ArrayList<Button>();
    public static ArrayList<Integer> integerArrayListRejestrator = new ArrayList(Arrays.asList(0,1,2,3));
    public static ArrayList<Integer> integerArrayListLekarz = new ArrayList(Arrays.asList(1,3));
    public static ArrayList<Integer> integerArrayListADMIN = new ArrayList(Arrays.asList(0,1,2,3,4,5,6,7));

    @Override
    public void start(Stage primaryStage) throws Exception{
        test();
        WindowManager.getInstance().setMainWindow(primaryStage);
        Parent parent = FXMLLoader.load(getClass().getResource("fxml/LoginForm.fxml"));
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //Parent root = FXMLLoader.load(getClass().getResource("fxml/MenuForm.fxml"));
        //primaryStage.setTitle("Hello World");
        //primaryStage.setScene(new Scene(root, 1024, 576));
        //primaryStage.setResizable(false);

        //primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        //Main.st
    }

    public void test(){
        MenuController menuController = new MenuController();

        Button a = new Button("Zarejestruj pacjenta");
        a.setOnAction(e -> menuController.registerPatient(e));
        a.setAlignment(Pos.CENTER);

        a.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonArrayList.add(a);

        Button b = new Button("Umów wizytę/badanie");
        b.setOnAction(e -> menuController.registerPatient(e));
        b.setAlignment(Pos.CENTER);
        b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonArrayList.add(b);

        Button c = new Button("Lista wizyt");
        c.setOnAction(e -> menuController.registerPatient(e));
        c.setAlignment(Pos.CENTER);
        c.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonArrayList.add(c);

        Button d = new Button("Lista pacjentów");
        d.setOnAction(e -> menuController.registerPatient(e));
        d.setAlignment(Pos.CENTER);
        d.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonArrayList.add(d);

        Button ee = new Button("Wystawienie skierowania/e-recepty");
        ee.setOnAction(e -> menuController.registerPatient(e));
        ee.setAlignment(Pos.CENTER);
        ee.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonArrayList.add(ee);

        Button f = new Button("Dodaj opis badania");
        f.setOnAction(e -> menuController.registerPatient(e));
        f.setAlignment(Pos.CENTER);
        f.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonArrayList.add(f);

        Button g = new Button("Lista usług");
        g.setOnAction(e -> menuController.registerPatient(e));
        g.setAlignment(Pos.CENTER);
        g.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonArrayList.add(g);

        Button h = new Button("Raporty przychodów/kosztów");
        h.setOnAction(e -> menuController.registerPatient(e));
        h.setAlignment(Pos.CENTER);
        h.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonArrayList.add(h);
    }
}
