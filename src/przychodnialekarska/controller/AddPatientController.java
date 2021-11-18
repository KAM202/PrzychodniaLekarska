package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import przychodnialekarska.WindowManager;



public class AddPatientController {

    @FXML
    private TextField peselField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField mobileField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField zipCodeField;

    @FXML
    private TextField emailField;


    public void cancelButton(ActionEvent event){
        WindowManager.getInstance().closeWindow();
    }

    public void addPatientButton(ActionEvent event){
        if(peselField.getText().isEmpty() || nameField.getText().isEmpty() || surnameField.getText().isEmpty() || addressField.getText().isEmpty() || zipCodeField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Uzupełnij wszystkie pola!");
            alert.show();
            return;
        }
    }
}
