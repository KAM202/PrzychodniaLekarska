package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;
import przychodnialekarska.utils.Formatter;
import przychodnialekarska.utils.Validate;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;


public class AddPatientController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TextFormatter onlyDigitPeselFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("^[0-9]*$").matcher(change.getControlNewText()).matches() ? change : null;
        });
        peselField.setTextFormatter(onlyDigitPeselFormatter);

        TextFormatter onlyLettersIncludePolishName = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*").matcher(change.getControlNewText()).matches() ? change : null;
        });
        nameField.setTextFormatter(onlyLettersIncludePolishName);

        TextFormatter onlyLettersIncludePolishSurname = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*").matcher(change.getControlNewText()).matches() ? change : null;
        });

        surnameField.setTextFormatter(onlyLettersIncludePolishSurname);

        TextFormatter onlyDigitMobileFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("^[0-9]*$").matcher(change.getControlNewText()).matches() ? change : null;
        });
        mobileField.setTextFormatter(onlyDigitMobileFormatter);

        // [0-9]{2}-[0-9]{3}$

        TextFormatter zipCodeFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            System.out.println(change + " - " + Pattern.compile("[0-9]{2}-[0-9]{3}$").matcher(change.getControlNewText()).matches());
            return Pattern.compile("^[0-9]*$").matcher(change.getControlNewText()).matches() ? change : null;
        });

        zipCodeField.setTextFormatter(zipCodeFormatter);

        TextFormatter onlyEmailFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("^(.+)@(.+)$").matcher(change.getControlNewText()).matches() ? change : null;
        });

        emailField.setTextFormatter(onlyEmailFormatter);
    }

    public void cancelButton(ActionEvent event){
        WindowManager.getInstance().closeWindow();
    }

    public void addPatientButton(ActionEvent event) throws SQLException {

        if(peselField.getText().isEmpty() || nameField.getText().isEmpty() || surnameField.getText().isEmpty() || addressField.getText().isEmpty() || zipCodeField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Uzupełnij wszystkie pola!");
            alert.show();
            return;
        }
        if(!Validate.ValidatePesel(peselField.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Zły pesel!");
            alert.show();
        }


        Connection c = DatabaseManager.getConnection();
        System.out.println(c);
        PreparedStatement statement = c.prepareStatement("INSERT INTO PACJENCI(pesel_pacjenta, imie, nazwisko, nr_telefonu, adres, kod_pocztowy, email) VALUES(?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, peselField.getText());
        statement.setString(2, nameField.getText());
        statement.setString(3, surnameField.getText());
        statement.setString(4, mobileField.getText());
        statement.setString(5, addressField.getText());
        statement.setString(6, zipCodeField.getText());
        statement.setString(7, emailField.getText());
        statement.executeUpdate();


        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Dodano nowego pacjenta!");
        alert.show();

        WindowManager.getInstance().closeWindow();

    }

}
