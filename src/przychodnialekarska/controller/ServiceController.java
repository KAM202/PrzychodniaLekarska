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
        /*
        descriptionTableColumn.setCellValueFactory(tc ->{
            TableCell<Usluga, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionTableColumn.widthProperty());
            return cell;
        });

         */
        getFromDatabase();

        //uslugi.add(new Usluga(1,"Nazwa", "Opiwdqwqwdqqdwqdwqwds", 30));
        //uslugi.add(new Usluga(2,"Nazwa2", "Opiwdqwqwdqqdwqdwqwds", 60));
        //uslugi.add(new Usluga(3,"Nazwa3", "Opiwdqwqwdqqdwqdwqwds", 90));


        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        costTextField.setTextFormatter(formatter);

        serviceTableView.getSelectionModel().selectedIndexProperty().addListener((num) -> onClick());

        nameTextField.textProperty().addListener((observable, oldvalue, newvalue) -> this.changeTextFieldListener(observable, oldvalue, newvalue, this.nameTextField, 32));
        descriptionTextField.textProperty().addListener((observable, oldvalue, newvalue) -> this.changeTextFieldListener(observable, oldvalue, newvalue, this.descriptionTextField, 64));

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (anyChange == true) {
                        anyChange = false;
                        System.out.println("Byla jakas zmiana");
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

        //System.out.println(thread.getState());
    }

    public void saveClick(ActionEvent event){
        int index = serviceTableView.getSelectionModel().getSelectedIndex();
        if(index == -1) return;

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
        System.out.println(uslugi.get(index).getNameService());
    }
    public void removeClick(ActionEvent event){
        int index = serviceTableView.getSelectionModel().getSelectedIndex();
        if(index == -1) return;

        try{
            //Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            PreparedStatement statement = c.prepareStatement("DELETE FROM USLUGI WHERE id_uslugi = ?");
            statement.setString(1, String.valueOf(uslugi.get(index).getIdService()));

            statement.executeUpdate();
           // if(c != null) Main.pool.returnConnection(c);

        }catch (Exception e){
            e.printStackTrace();
        }
        uslugi.remove(index);
       // uslugi.remove(index);
        //serviceTableView.refresh();
        //serviceTableView.setItems(uslugi);
        //System.out.println(uslugi.get(index).getNameService());
    }

    public void addClick(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../fxml/AddServiceForm.fxml"));

            Stage stage = WindowManager.getInstance().createWindow(root);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(anyChange);
    }

    public void getFromDatabase(){
        try{
            //Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            Statement statement = c.createStatement();
            String sql = "SELECT * from USLUGI";

            ResultSet res = statement.executeQuery(sql);
            uslugi.clear();
            while(res.next()) {
                uslugi.add(new Usluga(res.getInt("id_uslugi"), res.getString("nazwa"), res.getString("opis"),res.getDouble("koszt")));
            }
            //if(c != null) Main.pool.returnConnection(c);
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
        System.out.println(String.valueOf(uslugi.get(index).getCostService()) + " zł");
    }

    public void changeTextFieldListener(final ObservableValue<? extends String> ov, final String oldValue, final String newValue, TextField textField, final Integer maxLength) {
        if (textField.getText().length() > maxLength) {
            String s = textField.getText().substring(0, maxLength);
            textField.setText(s);
        }
    }
    public void changeTextFieldListener(final ObservableValue<? extends String> ov, final String oldValue, final String newValue, TextArea textField, final Integer maxLength) {
        if (textField.getText().length() > maxLength) {
            String s = textField.getText().substring(0, maxLength);
            textField.setText(s);
        }
    }



}
