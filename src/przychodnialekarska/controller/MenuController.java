package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import przychodnialekarska.Main;
import przychodnialekarska.WindowManager;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private GridPane menuItems;

    @FXML
    private Label loginAs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginAs.setText("Tomasz Suchoduł");
        String uprawnienia = "REJESTRATOR";

        switch(uprawnienia){
            case "REJESTRATOR":
                testFunction();
                System.out.println("test");
                break;
            case "LEKARZ":
                break;
        }
    }

    public void registerPatient(ActionEvent event){
        System.out.println("Rejestracja pacjenta");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../fxml/AddPatientForm.fxml"));

            Stage stage = WindowManager.getInstance().createWindow(root);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void testFunction(){
        int x = 0, y = 0;

        for(int i = 0; i < Main.buttonArrayList.size(); i++){
            if(x > 2){
                y++;
                x = 0;
            }
            if(Main.integerArrayListADMIN.indexOf(i) != -1){

                menuItems.add(Main.buttonArrayList.get(i), x, y);
                menuItems.setMargin(Main.buttonArrayList.get(i), new Insets(10,10,10,10));
               // menuItems.setFillHeight(Main.buttonArrayList.get(i),true);
                //menuItems.setFillWidth(Main.buttonArrayList.get(i),true);
                x++;
            }
        }


    }
}
