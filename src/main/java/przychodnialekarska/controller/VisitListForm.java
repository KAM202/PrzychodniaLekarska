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

import java.net.URL;
//import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
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
    private TableColumn idColumn;

    @FXML
    private TableColumn namePatientColumn;

    @FXML
    private TableColumn surnamePatientColumn;

    @FXML
    private TableColumn nameDoctorColumn;

    @FXML
    private TableColumn surnameDoctorColumn;

    @FXML
    private TableColumn dateColumn;

    @FXML
    private Label patientLabel;

    @FXML
    private Label patientPeselLabel;

    @FXML
    private Label patientMobileLabel;

    @FXML
    private Label doctorLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Button filterButton;

    @FXML
    private Button removeFilterButton;

    @FXML
    private TextField nameFilter;

    @FXML
    private TextField surnameFilter;

    @FXML
    private TextField surnameDoctorFilter;

    @FXML
    private DatePicker dateFilter;

    @FXML
    private CheckBox allVisitFilter;

    @FXML
    private ListView listView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, Integer>("id"));
        namePatientColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("namePatient"));
        surnamePatientColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("surnamePatient"));
        nameDoctorColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, Integer>("nameDoctor"));
        surnameDoctorColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("surnameDoctor"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, Date>("date"));

        test = FXCollections.observableArrayList();

        try{
           // Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            //Statement statement = c.createStatement();
            String sql = "SELECT wizyty.id_wizyty, pacjenci.imie, pacjenci.nazwisko, pracownicy.imie, pracownicy.nazwisko, wizyty.data, pacjenci.pesel_pacjenta, pacjenci.nr_telefonu FROM WIZYTY INNER JOIN pacjenci USING (pesel_pacjenta) INNER JOIN pracownicy USING (id_pracownika)";
            Statement statement = c.createStatement();
            //PreparedStatement statement = c.prepareStatement(sql);
            //statement.setString(1, "wizyty");
           // statement.setString(2, passwordField.getText());
           // ResultSet res = statement.executeQuery();
            ResultSet res = statement.executeQuery(sql);
            System.out.println("There are below tables:");
            while(res.next()) {
                System.out.println(res.getString("data"));
                //c = Main.pool.getConnection();
                statement = c.createStatement();
                ResultSet res2 = statement.executeQuery("SELECT uslugi.id_uslugi, uslugi.nazwa FROM SZCZEGOLY_WIZYT INNER JOIN USLUGI USING (id_uslugi) WHERE id_wizyty = '" + res.getInt("id_wizyty") + "'");
                ArrayList<Usluga> uslugaArrayList = new ArrayList<>();
                while (res2.next()) {
                    System.out.println(res2.getString("nazwa"));
                    uslugaArrayList.add(new Usluga(res2.getInt("id_uslugi"), res2.getString("nazwa"), "", 1.0));
                }

                test.add(new VisitWrapper(
                        new Pacjent(res.getString("pesel_pacjenta"), res.getString(2), res.getString(3), res.getInt("nr_telefonu"), "address", "", ""),
                        new Wizyta(res.getInt("id_wizyty"), null, null, res.getString("data")),
                        new Lekarz(1,"",res.getString(4),res.getString(5),213,"",""),
                        uslugaArrayList
                ));

            }
            //if(c != null) Main.pool.returnConnection(c);
        }catch(Exception e){
            e.printStackTrace();
        }
        visitTable.setItems(test);
        visitTable.setPlaceholder(new Label("Nie odnaleziono żadnej wizyty."));
        //visitTable.addEventHandler(e -> onClick(e));
        //visitTable.getSelectionModel().selectedIndexProperty()
       visitTable.getSelectionModel().selectedIndexProperty().addListener((num) -> onClick(test));

    }


    public void filterButtonClick(ActionEvent event) throws ParseException {
        ObservableList<VisitWrapper> test2 = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = dateFilter.getValue();

        if(nameFilter.getText().length() > 2 || surnameFilter.getText().length() > 2 || surnameDoctorFilter.getText().length() > 2 || dateFilter.getEditor().getText().length() > 2 || (!allVisitFilter.isSelected())){
            System.out.println("petla");
            for(int i = 0; i < test.size(); i++){
                if(nameFilter.getText().length() > 2 && !(test.get(i).getNamePatient().toLowerCase().contains(nameFilter.getText().toLowerCase()))) continue;
                if(surnameFilter.getText().length() > 2 && !(test.get(i).getSurnamePatient().toLowerCase().contains(surnameFilter.getText().toLowerCase()))) continue;
                if(surnameDoctorFilter.getText().length() > 2 && !(test.get(i).getSurnameDoctor().toLowerCase().contains(surnameDoctorFilter.getText().toLowerCase()))) continue;
                //sif(!dateFilter.getValue().equals(null) && ())
                // dodac obsluge daty
                //if(!(allVisitFilter.isSelected())) continue;
               // System.out.println((new SimpleDateFormat("yyyy-MM-dd").format(new Date())) < (new SimpleDateFormat("yyyy-MM-dd").parse(test.get(i).getDate())));

                //System.out.println((!test.get(i).getNamePatient().toLowerCase().contains(nameFilter.getText().toLowerCase())));
                test2.add(test.get(i));
            }
            visitTable.setItems(test2);
        }else{
            visitTable.setItems(test);
        }


        /*if(nameFilter.getText().length() > 2){
            for(int i = 0; i < test.size(); i++){
                if(test.get(i).getNamePatient().toLowerCase().contains(nameFilter.getText().toLowerCase())){
                    test2.add(test.get(i));
                }
            }
            visitTable.setItems(test2);
            System.out.println(nameFilter.getText().length());
        }else{
            visitTable.setItems(test);
        }
        */

    }

    public void removeFilterButtonClick(ActionEvent event){
        nameFilter.setText("");
        surnameFilter.setText("");
        surnameDoctorFilter.setText("");
        dateFilter.setValue(null);
        dateFilter.getEditor().clear();
        allVisitFilter.setSelected(true);
        visitTable.setItems(test);
    }

    private void onClick(ObservableList<VisitWrapper> test) {
        int index = visitTable.getSelectionModel().getSelectedIndex();
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

        System.out.println(test.get(index).getNamePatient());

    }

}
