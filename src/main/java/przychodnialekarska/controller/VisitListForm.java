package przychodnialekarska.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.objectClass.*;
import przychodnialekarska.utils.Variables;

import java.net.URL;
//import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class VisitListForm implements Initializable {

    private ObservableList<VisitWrapper> test;

    @FXML
    private TableView visitTable;

    @FXML
    private TableColumn idColumn, namePatientColumn, surnamePatientColumn, nameDoctorColumn, surnameDoctorColumn, dateColumn;

    @FXML
    private Label patientLabel, patientPeselLabel, patientMobileLabel, doctorLabel, dateLabel;

    @FXML
    private Button filterButton, removeFilterButton;

    @FXML
    private TextField nameFilter, surnameFilter, surnameDoctorFilter;

    @FXML
    private DatePicker dateFilter;

    @FXML
    private CheckBox allVisitFilter;

    @FXML
    private ListView listView;

    private String additional;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, Integer>("id"));
        namePatientColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("namePatient"));
        surnamePatientColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("surnamePatient"));
        nameDoctorColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, Integer>("nameDoctor"));
        surnameDoctorColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("surnameDoctor"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, Date>("date"));

        test = FXCollections.observableArrayList();
        additional = (Variables.poziomUprawnien == 1)?Variables.additionalQuery:"";

        loadDatabase();


        visitTable.setItems(test);
        visitTable.setPlaceholder(new Label("Nie odnaleziono żadnej wizyty."));

        visitTable.getSelectionModel().selectedIndexProperty().addListener((num) -> onClick(test));
        nameFilter.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        surnameFilter.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        surnameDoctorFilter.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        allVisitFilter.selectedProperty().addListener((observable, oldValue, newValue) -> liveFilter());

    }
    private void loadDatabase(){
        try{
            // Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            //Statement statement = c.createStatement();
            String sql = "SELECT wizyty.id_wizyty, pacjenci.imie, pacjenci.nazwisko, pracownicy.imie, pracownicy.nazwisko, wizyty.data, pacjenci.pesel_pacjenta, pacjenci.nr_telefonu FROM WIZYTY INNER JOIN pacjenci USING (pesel_pacjenta) INNER JOIN pracownicy USING (id_pracownika)" + additional;
            Statement statement = c.createStatement();
            //PreparedStatement statement = c.prepareStatement(sql);
            //statement.setString(1, "wizyty");
            // statement.setString(2, passwordField.getText());
            // ResultSet res = statement.executeQuery();
            ResultSet res = statement.executeQuery(sql);

            while(res.next()) {
                //System.out.println(res.getString("data"));
                //c = Main.pool.getConnection();
                statement = c.createStatement();
                ResultSet res2 = statement.executeQuery("SELECT uslugi.id_uslugi, uslugi.nazwa FROM SZCZEGOLY_WIZYT INNER JOIN USLUGI USING (id_uslugi) WHERE id_wizyty = '" + res.getInt("id_wizyty") + "'");
                ArrayList<Usluga> uslugaArrayList = new ArrayList<>();
                while (res2.next()) {
                    //System.out.println(res2.getString("nazwa"));
                    uslugaArrayList.add(new Usluga(res2.getInt("id_uslugi"), res2.getString("nazwa"), "", 1.0));
                }

                test.add(new VisitWrapper(
                        new Pacjent(res.getString("pesel_pacjenta"), res.getString(2), res.getString(3), res.getInt("nr_telefonu"), "address", "", ""),
                        new Wizyta(res.getInt("id_wizyty"), null, null, res.getString("data")),
                        new Lekarz(1,"",res.getString(4),res.getString(5),213,"",""),
                        uslugaArrayList
                ));

            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void liveFilter(){
        ObservableList<VisitWrapper> test2 = FXCollections.observableArrayList();



            for(int i = 0; i < test.size(); i++){
                if(nameFilter.getText().length() > 0 && !(test.get(i).getNamePatient().toLowerCase().contains(nameFilter.getText().toLowerCase()))) continue;
                if(surnameFilter.getText().length() > 0 && !(test.get(i).getSurnamePatient().toLowerCase().contains(surnameFilter.getText().toLowerCase()))) continue;
                if(surnameDoctorFilter.getText().length() > 0 && !(test.get(i).getSurnameDoctor().toLowerCase().contains(surnameDoctorFilter.getText().toLowerCase()))) continue;
                if(dateFilter.getValue() != null && !(test.get(i).getDate().equals(dateFilter.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))) continue;
                if(!allVisitFilter.isSelected() && !(LocalDate.parse(test.get(i).getDate()).isAfter(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) || LocalDate.parse(test.get(i).getDate()).isEqual(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))))) continue;
                test2.add(test.get(i));
            }
            visitTable.setItems(test2);
            clear();
    }

    public void removeFilterButtonClick(ActionEvent event){
        nameFilter.setText("");
        surnameFilter.setText("");
        surnameDoctorFilter.setText("");
        dateFilter.setValue(null);
        dateFilter.getEditor().clear();
        allVisitFilter.setSelected(true);
    }

    public void clear(){
        patientLabel.setText("");
        patientPeselLabel.setText("");
        patientMobileLabel.setText("");
        doctorLabel.setText("");
        dateLabel.setText("");
        listView.getItems().clear();
    }

    private void onClick(ObservableList<VisitWrapper> test) {
        int index = visitTable.getSelectionModel().getSelectedIndex();
        if(index == -1) return;
        //System.out.println(visitTable.getSelectionModel().getSelectedIndex());
        patientLabel.setText(test.get(index).getNamePatient() + " " + test.get(index).getSurnamePatient());
        patientPeselLabel.setText(test.get(index).getPeselPatient());
        patientMobileLabel.setText(test.get(index).getNumberPatient().toString());
        doctorLabel.setText(test.get(index).getNameDoctor() + " " + test.get(index).getSurnameDoctor());
        dateLabel.setText(test.get(index).getDate());
        //listView.getItems().add("ITEM");
        listView.getItems().clear();
        for(int i = 0; i < test.get(index).getUslugi().size(); i++){
            listView.getItems().add(test.get(index).getUslugi().get(i).getNameService());
        }


    }

}
