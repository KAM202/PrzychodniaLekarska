package przychodnialekarska.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;
import przychodnialekarska.objectClass.Usluga;
import przychodnialekarska.utils.Formatter;
import przychodnialekarska.utils.LanguageString;
import przychodnialekarska.utils.UtilFunction;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ServiceController implements Initializable {

    public static boolean anyChange = false;
    public static Thread thread;

    @FXML
    private TextField idTextField,nameTextField, costTextField;

    @FXML
    private TextArea descriptionTextField;

    @FXML
    private TableView serviceTableView;

    @FXML
    private TableColumn idTableColumn, nameTableColumn, descriptionTableColumn, costTableColumn;

    public static ObservableList<Usluga> uslugi;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uslugi = FXCollections.observableArrayList();
        idTableColumn.setCellValueFactory(new PropertyValueFactory<Usluga, Integer>("idService"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<Usluga, String>("nameService"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<Usluga, String>("descriptionService"));
        costTableColumn.setCellValueFactory(new PropertyValueFactory<Usluga, String>("costService"));

        getFromDatabase();



        TextFormatter onlyDoubleFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return Pattern.compile("\\d*|\\d+\\.\\d*").matcher(change.getControlNewText()).matches() ? change : null;
        });

        costTextField.setTextFormatter(onlyDoubleFormatter);

        serviceTableView.getSelectionModel().selectedIndexProperty().addListener((num) -> onClick());

        nameTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.nameTextField, 32));
        descriptionTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextAreaListener(observable, oldvalue, newvalue, this.descriptionTextField, 64));

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (anyChange == true) {
                        anyChange = false;
                        getFromDatabase();
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        thread.start();


    }

    public void saveClick(ActionEvent event){
        int index = serviceTableView.getSelectionModel().getSelectedIndex();
        if(index == -1) return;

        if(nameTextField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_NAME_SERVICE).show();return;}
        if(descriptionTextField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_DESCRIPTION_SERVICE).show();return;}
        if(costTextField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NULL_COST_SERVICE).show();return;}

        try{
            //Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            PreparedStatement statement = c.prepareStatement("UPDATE USLUGI SET nazwa = ?, opis = ?, koszt = ? WHERE id_uslugi = ?");
            statement.setString(1, nameTextField.getText());
            statement.setString(2, descriptionTextField.getText());
            statement.setString(3, costTextField.getText());
            statement.setString(4, String.valueOf(uslugi.get(index).getIdService()));

            statement.executeUpdate();
            //if(c != null) Main.pool.returnConnection(c);

        }catch (Exception e){
            e.printStackTrace();
        }


        uslugi.get(index).setNameService(nameTextField.getText());
        uslugi.get(index).setDescriptionService(descriptionTextField.getText());
        uslugi.get(index).setCostService(Double.parseDouble(costTextField.getText()));
        serviceTableView.refresh();
        //serviceTableView.setItems(uslugi);
        UtilFunction.showAlert(Alert.AlertType.INFORMATION, LanguageString.SERVICE_SAVED).show();
    }
    public void removeClick(ActionEvent event){
        int index = serviceTableView.getSelectionModel().getSelectedIndex();
        if(index == -1) return;

        try{
            Connection c = DatabaseManager.getConnection();
            PreparedStatement statement = c.prepareStatement("DELETE FROM USLUGI WHERE id_uslugi = ?");
            statement.setString(1, String.valueOf(uslugi.get(index).getIdService()));

            statement.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }
        uslugi.remove(index);
        UtilFunction.showAlert(Alert.AlertType.INFORMATION, LanguageString.SERVICE_REMOVED).show();

    }

    public void addClick(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddServiceForm.fxml"));

            Stage stage = WindowManager.getInstance().createWindow(root);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void getFromDatabase(){
        try{
            Connection c = DatabaseManager.getConnection();
            Statement statement = c.createStatement();
            String sql = "SELECT * from USLUGI";

            ResultSet res = statement.executeQuery(sql);
            uslugi.clear();
            while(res.next()) {
                uslugi.add(new Usluga(res.getInt("id_uslugi"), res.getString("nazwa"), res.getString("opis"),res.getDouble("koszt")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        serviceTableView.setItems(uslugi);
    }

    public void onClick(){
        int index = serviceTableView.getSelectionModel().getSelectedIndex();
        if(index == -1) return;
        idTextField.setText(String.valueOf(uslugi.get(index).getIdService()));
        nameTextField.setText(uslugi.get(index).getNameService());
        descriptionTextField.setText(uslugi.get(index).getDescriptionService());
        costTextField.setText(String.valueOf(uslugi.get(index).getCostService()));
    }



}
