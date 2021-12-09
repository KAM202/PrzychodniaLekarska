package przychodnialekarska.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.objectClass.*;
import przychodnialekarska.utils.Variables;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ExaminationListController implements Initializable {

    @FXML
    private TextField peselTextField, nameTextField, surnameTextField;

    @FXML
    private ChoiceBox serviceChoiceBox;

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private TextArea descriptionTextArea, resultTextArea, recommendationsTextArea;

    @FXML
    private CheckBox reciptCheckBox;

    @FXML
    private ListView attListView;

    @FXML
    private TableView examinationTableView;

    @FXML
    private TableColumn idTableColumn, peselTableColumn, nameTableColumn, surnameTableColumn, serviceTableColumn, doctorTableColumn, dateTableColumn;

    @FXML
    private Button removeButton, viewAttButton;

    private ObservableList<ExaminationWrapper> examinationWrappers;
    private ArrayList<Usluga> uslugaArrayList;
    private String additional;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idTableColumn.setCellValueFactory(new PropertyValueFactory<ExaminationWrapper, Integer>("id"));
        peselTableColumn.setCellValueFactory(new PropertyValueFactory<ExaminationWrapper, String>("peselPatient"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<ExaminationWrapper, String>("namePatient"));
        surnameTableColumn.setCellValueFactory(new PropertyValueFactory<ExaminationWrapper, String>("surnamePatient"));
        serviceTableColumn.setCellValueFactory(new PropertyValueFactory<ExaminationWrapper, String>("serviceName"));
        doctorTableColumn.setCellValueFactory(new PropertyValueFactory<ExaminationWrapper, String>("doctorName"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<ExaminationWrapper, String>("date"));

        examinationTableView.setPlaceholder(new Label("Brak badań!"));

        examinationWrappers = FXCollections.observableArrayList();
        uslugaArrayList = new ArrayList<>();

        additional = (Variables.poziomUprawnien == 1)?Variables.addittionalQuery():"";

        loadDatabase();
        examinationTableView.getSelectionModel().selectedIndexProperty().addListener((num) -> onClickOnTable());
        attListView.getSelectionModel().selectedIndexProperty().addListener((num) -> {viewAttButton.setDisable(false);});

        peselTextField.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        surnameTextField.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        dateDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        serviceChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> liveFilter());
    }

    public void removeFilterClick(ActionEvent event) {
        peselTextField.clear();
        nameTextField.clear();
        surnameTextField.clear();
        serviceChoiceBox.setValue(null);
        dateDatePicker.setValue(null);
        clear();
    }
    private void clear(){
        descriptionTextArea.clear();
        resultTextArea.clear();
        recommendationsTextArea.clear();
        reciptCheckBox.setSelected(false);
        attListView.getItems().clear();
        viewAttButton.setDisable(true);
    }

    private void liveFilter(){
        ObservableList<ExaminationWrapper> tempExaminationWrapper = FXCollections.observableArrayList();
        for(ExaminationWrapper examinationWrapper : examinationWrappers){
            if(peselTextField.getText().length() > 0 && !(examinationWrapper.getPeselPatient().toLowerCase().startsWith(peselTextField.getText()))) continue;
            if(nameTextField.getText().length() > 0 && !(examinationWrapper.getNamePatient().toLowerCase().startsWith(nameTextField.getText()))) continue;
            if(surnameTextField.getText().length() > 0 && !(examinationWrapper.getSurnamePatient().toLowerCase().startsWith(surnameTextField.getText()))) continue;
            if(serviceChoiceBox.getValue() != null && !(examinationWrapper.getServiceName().equals(serviceChoiceBox.getValue()))) continue;

            if(dateDatePicker.getValue() != null && !(examinationWrapper.getDate().equals(dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))) continue;

            tempExaminationWrapper.add(examinationWrapper);
        }

        examinationTableView.setItems(tempExaminationWrapper);
        clear();
    }


    public void viewAttClick(ActionEvent event) {
        int index = attListView.getSelectionModel().getSelectedIndex();
        if(index == -1) return;
        try{
            Desktop.getDesktop().open(examinationWrappers.get(examinationTableView.getSelectionModel().getSelectedIndex()).getFiles().get(index));
            attListView.getSelectionModel().clearSelection();
        }catch(Exception e ){
            e.printStackTrace();
        }
    }

    private void onClickOnTable() {
        int index = examinationTableView.getSelectionModel().getSelectedIndex();
        if(index == -1) return;
        removeFilterClick(null);
        descriptionTextArea.setText(examinationWrappers.get(index).getDescription());
        resultTextArea.setText(examinationWrappers.get(index).getResult());
        recommendationsTextArea.setText(examinationWrappers.get(index).getRecommendations());
        reciptCheckBox.setSelected((examinationWrappers.get(index).getRecipt().equals("Tak")?true:false));
        attListView.getItems().clear();
        for(int i = 0; i < examinationWrappers.get(index).getFiles().size(); i++){
            attListView.getItems().add(examinationWrappers.get(index).getFiles().get(i).getName());

        }

    }

    private void loadDatabase(){
        try{
            Connection c = DatabaseManager.getConnection();
            Statement statement = c.createStatement();
            ResultSet resultSetWyniki = statement.executeQuery("SELECT wyniki_badan.id_badania, pacjenci.pesel_pacjenta, pacjenci.imie, pacjenci.nazwisko, uslugi.nazwa, pracownicy.imie, pracownicy.nazwisko, wizyty.data, wyniki_badan.opis_badania, wyniki_badan.wynik_badania, wyniki_badan.zalecenia, wyniki_badan.recepta FROM WYNIKI_BADAN INNER JOIN USLUGI USING(id_uslugi) INNER JOIN PACJENCI USING(pesel_pacjenta) INNER JOIN WIZYTY USING(id_wizyty) INNER JOIN PRACOWNICY USING (id_pracownika)" + additional);

            while(resultSetWyniki.next()){
                Statement statement2 = c.createStatement();
                ResultSet resultSet = statement2.executeQuery("SELECT * FROM SZCZEGOLY_BADAN WHERE id_badania = '" + resultSetWyniki.getInt("id_badania") + "'");
                ArrayList<File> tempFile = new ArrayList<File>();
                while(resultSet.next()){
                    tempFile.add(new File("images/" + resultSetWyniki.getInt("id_badania") + "/" + resultSet.getString("file_name")));
                }

                examinationWrappers.add(new ExaminationWrapper(
                   new Pacjent(resultSetWyniki.getString(2),resultSetWyniki.getString(3),resultSetWyniki.getString(4),-1,"","",""),
                   new WynikiBadan(resultSetWyniki.getInt(1),-1,-1,"",resultSetWyniki.getString(9),resultSetWyniki.getString(10),resultSetWyniki.getString(11),resultSetWyniki.getString(12)),
                   new Lekarz(-1,"",resultSetWyniki.getString(6),resultSetWyniki.getString(7),-1,"",""),
                   new Wizyta(-1,"","",resultSetWyniki.getString(8)),
                   new Usluga(-1,resultSetWyniki.getString(5),"",-1),
                        tempFile
                ));
            }

            statement = c.createStatement();
            ResultSet serviceResult = statement.executeQuery("SELECT * FROM USLUGI");
            examinationTableView.setItems(examinationWrappers);
            while(serviceResult.next()){
                uslugaArrayList.add(new Usluga(serviceResult.getInt("id_uslugi"), serviceResult.getString("nazwa"), serviceResult.getString("opis"), serviceResult.getDouble("koszt")));
                serviceChoiceBox.getItems().add(serviceResult.getString("nazwa"));
            }

        }catch (Exception e ){
            e.printStackTrace();
        }
    }
}
