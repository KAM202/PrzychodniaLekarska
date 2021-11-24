package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import przychodnialekarska.Main;
import przychodnialekarska.WindowManager;
import przychodnialekarska.enums.ButtonNameEnum;

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
        loginAs.setText(Main.imie + " " + Main.nazwisko);

        permissions.setText(ButtonNameEnum.UPRAWIENIA.values()[Main.poziomUprawnien].toString());
        String uprawnienia = "REJESTRATOR";
        System.out.println(Main.poziomUprawnien + "poziom");
        testFunction(Main.poziomUprawnien);

    }

    public void helpAction(javafx.scene.input.MouseEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/HelpForm.fxml"));

            Stage stage = WindowManager.getInstance().createWindow(root);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void registerPatient(ActionEvent event){

        System.out.println("Rejestracja pacjenta");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../../../resources/fxml/AddPatientForm.fxml"));

            Stage stage = WindowManager.getInstance().createWindow(root);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void testFunction(int access){
        int x = 0, y = 0;

        for(int i = 0; i < Main.buttonArrayList.size(); i++){
            if(x > 2){
                y++;
                x = 0;
            }
            //System.out.println(ButtonNameEnum.windowsIds.get(access).get(0));
            if(ButtonNameEnum.windowsIds.get(access).indexOf(i) != -1){

                menuItems.add(Main.buttonArrayList.get(i), x, y);
                menuItems.setMargin(Main.buttonArrayList.get(i), new Insets(10,10,10,10));
               // menuItems.setFillHeight(Main.buttonArrayList.get(i),true);
                //menuItems.setFillWidth(Main.buttonArrayList.get(i),true);
                x++;
            }
        }


    }
}
