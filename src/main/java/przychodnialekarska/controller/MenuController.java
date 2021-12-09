package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import przychodnialekarska.Main;
import przychodnialekarska.WindowManager;
import przychodnialekarska.utils.Variables;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private GridPane menuItems;

    @FXML
    private Label loginAs;

    @FXML
    private Label permissions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginAs.setText(Variables.imie + " " + Variables.nazwisko);

        permissions.setText(Variables.UPRAWIENIA.values()[Variables.poziomUprawnien].toString());
        String uprawnienia = "REJESTRATOR";
        testFunction(Variables.poziomUprawnien);

    }

    public void testFunction(int access){
        int x = 0, y = 0;

        for(int i = 0; i < Variables.buttonArrayList.size(); i++){
            if(x > 2){
                y++;
                x = 0;
            }
            if(Variables.windowsIds.get(access).indexOf(i) != -1){

                menuItems.add(Variables.buttonArrayList.get(i), x, y);
                menuItems.setMargin(Variables.buttonArrayList.get(i), new Insets(10,10,10,10));
                x++;
            }
        }


    }

    public void logoutAction(ActionEvent event) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Main.class.getResource("/fxml/LoginForm.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setResizable(false);
        WindowManager.getInstance().setMainWindow(stage);
        stage.setTitle("System informatyczny obsługi przychodni lekarskiej - Logowanie");
        stage.getIcons().add(new Image("/img/logo.png"));
        stage.setScene(scene);
        stage.show();


        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
}
