package przychodnialekarska.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.objectClass.Pacjent;
import przychodnialekarska.objectClass.Usluga;
import przychodnialekarska.objectClass.Wizyta;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PatientListController implements Initializable {

    @FXML
    private TableView patientTable;

    @FXML
    private ListView historyListView;

    @FXML
    private TextField peselFilter, nameFilter, surnameFilter;

    @FXML
    private Button removeFilterButton, filterButton;

    @FXML
    private TableColumn peselColumn, nameColumn, surnameColumn, mobileColumn, addressColumn, zipColumn;

    private ObservableList<Pacjent> patientList;
    private ArrayList<ArrayList<Wizyta>> visitArrayList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        peselColumn.setCellValueFactory(new PropertyValueFactory<Usluga, String>("pesel"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Usluga, String>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<Usluga, Integer>("surname"));
        mobileColumn.setCellValueFactory(new PropertyValueFactory<Usluga, String>("numberPhone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<Usluga, String>("address"));
        zipColumn.setCellValueFactory(new PropertyValueFactory<Usluga, String>("zipPostCode"));
        patientList = FXCollections.observableArrayList();
        visitArrayList = new ArrayList<ArrayList<Wizyta>>();
        loadDatabase();
        patientTable.setPlaceholder(new Label("Brak pacjentów!"));

        patientTable.getSelectionModel().selectedIndexProperty().addListener((num) -> onClick());

        peselFilter.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        nameFilter.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        surnameFilter.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
    }

    public void liveFilter() {
        ObservableList<Pacjent> test2 = FXCollections.observableArrayList();
        for(int i = 0; i < patientList.size(); i++) {
            if (peselFilter.getText().length() > 0 && !(patientList.get(i).getPesel().toLowerCase().contains(peselFilter.getText().toLowerCase()))) continue;
            if (nameFilter.getText().length() > 0 && !(patientList.get(i).getName().toLowerCase().contains(nameFilter.getText().toLowerCase()))) continue;
            if (surnameFilter.getText().length() > 0 && !(patientList.get(i).getSurname().toLowerCase().contains(surnameFilter.getText().toLowerCase()))) continue;
            test2.add(patientList.get(i));
        }
        patientTable.setItems(test2);
        historyListView.getItems().clear();
    }

    private void onClick() {
        int index = patientTable.getSelectionModel().getSelectedIndex();
        if(index == -1) return;
        historyListView.getItems().clear();
        for(int i = 0; i < visitArrayList.get(index).size(); i++){
            historyListView.getItems().add("Wizyta w dniu: " + visitArrayList.get(index).get(i).getDate() + ".");
        }
    }

    private void loadDatabase(){
        try{

            Connection c = DatabaseManager.getConnection();
            Statement statement = c.createStatement();
            String sql = "SELECT * from PACJENCI";

            ResultSet res = statement.executeQuery(sql);
            patientList.clear();
            while(res.next()) {
                patientList.add(new Pacjent(res.getString("pesel_pacjenta"), res.getString("imie"), res.getString("nazwisko"),res.getInt("nr_telefonu"), res.getString("adres"), res.getString("kod_pocztowy"), null));

                Statement statement2 = c.createStatement();
                ResultSet res2 = statement2.executeQuery("SELECT * FROM WIZYTY WHERE pesel_pacjenta = '" + res.getString("pesel_pacjenta") + "'");
                ArrayList<Wizyta> tempVisit = new ArrayList<Wizyta>();
                while(res2.next()){
                    tempVisit.add(new Wizyta(res2.getInt("id_wizyty"), res2.getString("pesel_pacjenta"), res2.getString("id_pracownika"), res2.getString("data")));
                }
                visitArrayList.add(tempVisit);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        patientTable.setItems(patientList);
    }

    public void removeFilterButtonClick(ActionEvent event) {
        peselFilter.clear();
        nameFilter.clear();
        surnameFilter.clear();
        patientTable.setItems(patientList);
        historyListView.getItems().clear();
    }


}
