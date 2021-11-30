package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;
import przychodnialekarska.utils.LanguageString;
import przychodnialekarska.utils.UtilFunction;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class AddServiceController implements Initializable {

    @FXML
    private TextField nameTextField, costTextField;

    @FXML
    private TextArea descriptionTextField;

    @FXML
    private Button cancelButton, addButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TextFormatter onlyDoubleFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("\\d*|\\d+\\.\\d*").matcher(change.getControlNewText()).matches() ? change : null;
        });
        costTextField.setTextFormatter(onlyDoubleFormatter);
        nameTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.nameTextField, 32));
        descriptionTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextAreaListener(observable, oldvalue, newvalue, this.descriptionTextField, 32));
    }


    public void cancelClick(ActionEvent event) {
        WindowManager.getInstance().getCurrentWindow().close();
    }

    public void addClick(ActionEvent event) {
        if(nameTextField.getText().isEmpty()) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_NAME_SERVICE).show();return;}
        if(descriptionTextField.getText().isEmpty()) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_DESCRIPTION_SERVICE).show();return;}
        if(costTextField.getText().isEmpty()) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_COST_SERVICE).show();return;}

        ServiceController.anyChange = true;

        try{

            Connection c = DatabaseManager.getConnection();
            PreparedStatement statement = c.prepareStatement("INSERT INTO USLUGI(nazwa, opis, koszt) VALUES(?, ?, ?)");
            statement.setString(1, nameTextField.getText());
            statement.setString(2, descriptionTextField.getText());
            statement.setString(3, costTextField.getText());
            statement.executeUpdate();


        }catch (Exception e){
            e.printStackTrace();
        }

        Alert alert = UtilFunction.showAlert(Alert.AlertType.CONFIRMATION, LanguageString.ASK_FOR_NEXT_ADD_SERVICE);
        alert.showAndWait();
        if(alert.getResult() == ButtonType.CANCEL || alert.getResult() == ButtonType.CLOSE){
            WindowManager.getInstance().getCurrentWindow().close();
        }else{
            nameTextField.clear();
            descriptionTextField.clear();
            costTextField.clear();
        }


    }
}
