package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class AddServiceController implements Initializable {

    @FXML
    private TextField nameTextField, costTextField;

    @FXML
    private TextArea descriptionTextField;

    @FXML
    private Button cancelButton, addButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void cancelClick(ActionEvent event) {
        WindowManager.getInstance().getCurrentWindow().close();
        //WindowManager.getInstance().closeWindow();
    }

    public void addClick(ActionEvent event) {
        ServiceController.anyChange = true;

        try{
            //Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            PreparedStatement statement = c.prepareStatement("INSERT INTO USLUGI(nazwa, opis, koszt) VALUES(?, ?, ?)");
            statement.setString(1, nameTextField.getText());
            statement.setString(2, descriptionTextField.getText());
            statement.setString(3, costTextField.getText());
            statement.executeUpdate();
            //if(c != null) Main.pool.returnConnection(c);
            System.out.println("dodano usluge");

        }catch (Exception e){
            e.printStackTrace();
        }

        //ServiceController.uslugi.add(new Usluga(5, nameTextField.getText(), descriptionTextField.getText(), Double.parseDouble(costTextField.getText())));


        WindowManager.getInstance().getCurrentWindow().close();
    }
}
