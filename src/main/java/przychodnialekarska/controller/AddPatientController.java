package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;
import przychodnialekarska.utils.LanguageString;
import przychodnialekarska.utils.UtilFunction;
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
    private TextField peselField, nameField, surnameField, mobileField, addressField, zipCodeField, emailField;

    @FXML
    private Label nameSurnameLabel, peselLabel, mobileLabel, addressLabel, zipLabel, emailLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TextFormatter onlyDigitPeselFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("^[0-9]*$").matcher(change.getControlNewText()).matches() ? change : null;
        });
        peselField.setTextFormatter(onlyDigitPeselFormatter);
        peselField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.peselField, 11));
        peselField.textProperty().addListener((observable, oldValue, newValue) -> peselLabel.setText(newValue));
        TextFormatter onlyLettersIncludePolishName = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*").matcher(change.getControlNewText()).matches() ? change : null;
        });
        nameField.setTextFormatter(onlyLettersIncludePolishName);
        nameField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.nameField, 26));
        nameField.textProperty().addListener((observable, oldValue, newValue) -> { nameSurnameLabel.setText(surnameField.getText().isEmpty()?newValue:newValue + " " + surnameField.getText()); });

        TextFormatter onlyLettersIncludePolishSurname = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*").matcher(change.getControlNewText()).matches() ? change : null;
        });

        surnameField.setTextFormatter(onlyLettersIncludePolishSurname);
        surnameField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.surnameField, 26));
        surnameField.textProperty().addListener((observable, oldValue, newValue) -> { nameSurnameLabel.setText(nameField.getText().isEmpty()?newValue:nameField.getText() + " " + newValue); });
        TextFormatter onlyDigitMobileFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("^[0-9]*$").matcher(change.getControlNewText()).matches() ? change : null;
        });
        mobileField.setTextFormatter(onlyDigitMobileFormatter);
        mobileField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.mobileField, 9));
        mobileField.textProperty().addListener((observable, oldValue, newValue) -> { char[] strings = newValue.toCharArray();String mobile = "";for(int i = 0; i< strings.length;i++){ if(i%3 == 0) mobile += " " + strings[i];else mobile +=strings[i]; }mobileLabel.setText(mobile); });

        zipCodeField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.zipCodeField, 6));
        zipCodeField.textProperty().addListener((observable, oldValue, newValue) -> zipLabel.setText(newValue));
        emailField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.emailField, 15));
        emailField.textProperty().addListener((observable, oldValue, newValue) -> emailLabel.setText(newValue));
        addressField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.addressField, 26));
        addressField.textProperty().addListener((observable, oldValue, newValue) -> addressLabel.setText(newValue));
    }

    public void cancelButton(ActionEvent event){
        WindowManager.getInstance().closeWindow();
    }

    public void addPatientButton(ActionEvent event) throws SQLException {
        if(peselField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_PESEL).show();return;}
        if(!Validate.ValidatePesel(peselField.getText())){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.INCORRECT_PESEL).show();return;}
        if(nameField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_NAME).show();return;}
        if(surnameField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_SURNAME).show();return;}
        if(!mobileField.getText().isEmpty() && mobileField.getText().length() != 9){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.INCORRECT_MOBILE).show();return;}
        if(addressField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_ADDRESS).show();return;}
        if(zipCodeField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_ZIP).show();return;}
        if(!Pattern.compile("[0-9]{2}-[0-9]{3}$").matcher(zipCodeField.getText()).matches()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.INCORRECT_ZIP).show();return;}
        if(!emailField.getText().isEmpty() && !Pattern.compile("^(.+)@(.+)$").matcher(emailField.getText()).matches()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.INCORRECT_EMAIL).show();return;}



        Connection c = DatabaseManager.getConnection();

        PreparedStatement statement = c.prepareStatement("INSERT INTO PACJENCI(pesel_pacjenta, imie, nazwisko, nr_telefonu, adres, kod_pocztowy, email) VALUES(?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, peselField.getText());
        statement.setString(2, nameField.getText());
        statement.setString(3, surnameField.getText());
        statement.setString(4, mobileField.getText());
        statement.setString(5, addressField.getText());
        statement.setString(6, zipCodeField.getText());
        statement.setString(7, emailField.getText());
        statement.executeUpdate();

        Alert alert = UtilFunction.showAlert(Alert.AlertType.CONFIRMATION, LanguageString.ASK_FOR_NEXT_ADD_PATIENT);
        alert.showAndWait();
        if(alert.getResult() == ButtonType.CANCEL || alert.getResult() == ButtonType.CLOSE){
            WindowManager.getInstance().getCurrentWindow().close();
        }else{
            peselField.clear();
            nameField.clear();
            surnameField.clear();
            mobileField.clear();
            addressField.clear();
            zipCodeField.clear();
            emailField.clear();
        }


    }

}
