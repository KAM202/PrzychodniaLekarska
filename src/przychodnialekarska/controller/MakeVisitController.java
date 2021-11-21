package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;
import przychodnialekarska.objectClass.Lekarz;
import przychodnialekarska.objectClass.Pacjent;
import przychodnialekarska.objectClass.Usluga;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MakeVisitController implements Initializable {

    private ArrayList<Usluga> serviceList;
    private ArrayList<Pacjent> patientList;
    private ArrayList<Lekarz> doctorList;

    private String selectedPatientPesel;
    private ArrayList<Usluga> selectedServiceList;
    private int selectedDoctorId;
    //private ArrayList<String> serviceListOnView;


    @FXML
    private Label nameSurnamePatientLabel;

    @FXML
    private TextField peselPatientTextField;

    @FXML
    private ChoiceBox doctorChoiceBox;

    @FXML
    private ComboBox addServiceComboBox;

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private ListView serviceListView;

    @FXML
    private Button removeButton, cancelButton, addButton, registerNewPatientButton, addServiceButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceList = new ArrayList<Usluga>();
        patientList = new ArrayList<Pacjent>();
        doctorList = new ArrayList<Lekarz>();
        selectedServiceList = new ArrayList<Usluga>();
        selectedPatientPesel = "";
        //serviceListOnView = new ArrayList<String>();
        loadDatabase();

        for(Lekarz l : doctorList){
            doctorChoiceBox.getItems().add("lek. " + l.getName() + " " + l.getSurname());
        }

        for(Usluga u : serviceList){
            addServiceComboBox.getItems().add(u.getNameService());
        }

        //String pesel = "123456789";


        peselPatientTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode() == KeyCode.ENTER){
                    if(selectedPatientPesel.length() > 1){
                        peselPatientTextField.setText(selectedPatientPesel);
                    }
                }
            }
        });

        peselPatientTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length() <= 1){
                nameSurnamePatientLabel.setText("");
                return;
            }
            for(Pacjent p : patientList){
                if(p.getPesel().startsWith(newValue)){
                    nameSurnamePatientLabel.setText(p.getName() + " " + p.getSurname());
                    selectedPatientPesel = p.getPesel();
                    break;
                }else{
                    nameSurnamePatientLabel.setText("");
                }
            }

            System.out.println("textfield changed from " + oldValue + " to " + newValue);
        });

        //serviceListView.getItems().addAll(serviceList);

    }

    private void loadDatabase(){
        try{
            //Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            PreparedStatement statement = c.prepareStatement("SELECT id_pracownika, imie, nazwisko FROM pracownicy WHERE stanowiko = 1");
            ResultSet res = statement.executeQuery();
            while(res.next()){
                doctorList.add(new Lekarz(res.getInt("id_pracownika"), null, res.getString("imie"), res.getString("nazwisko"), -1, null, null));
            }
            statement = c.prepareStatement("SELECT pesel_pacjenta, imie, nazwisko FROM pacjenci");
            res = statement.executeQuery();
            while(res.next()){
                patientList.add(new Pacjent(res.getString("pesel_pacjenta"), res.getString("imie"), res.getString("nazwisko"), -1, null, null));
            }

            statement = c.prepareStatement("SELECT id_uslugi, nazwa, koszt FROM uslugi");
            res = statement.executeQuery();
            while(res.next()){
                serviceList.add(new Usluga(res.getInt("id_uslugi"),res.getString("nazwa"), null, res.getDouble("koszt")));
                //patientList.add(new Pacjent(res.getString("pesel_pacjenta"), res.getString("imie"), res.getString("nazwisko"), -1, null, null));
            }
            //if(c != null) Main.pool.returnConnection(c);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addService(ActionEvent event) {
        int index = addServiceComboBox.getSelectionModel().getSelectedIndex();
        if(index == -1) return; //informacja ze nei wybrano uslugi

        serviceListView.getItems().add(serviceList.get(index).getNameService());
        selectedServiceList.add(serviceList.get(index));
        //serviceListView.getItems().add(serviceList.get(index));
        serviceList.remove(index);
        //serviceList.remove(index);
        addServiceComboBox.getItems().remove(index);

        addServiceComboBox.setValue("");


    }

    public void removeService(ActionEvent event) {
        int index = serviceListView.getSelectionModel().getSelectedIndex();
        if(index == -1) return; //informacja ze nei wybrano uslugi

        //serviceList.add(serviceListView.getItems().get(index).toString());

        //addServiceComboBox.getItems().add(serviceListView.getItems().get(index).toString());
        addServiceComboBox.getItems().add(selectedServiceList.get(index).getNameService());
        serviceList.add(selectedServiceList.get(index));
        serviceListView.getItems().remove(index);

    }

    public void cancelButton(ActionEvent event) {
        WindowManager.getInstance().closeWindow();
    }

    public void registerNewPatientButton(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../fxml/AddPatientForm.fxml"));

            Stage stage = WindowManager.getInstance().createWindow(root);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addVisit(ActionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = dateDatePicker.getValue();
        //System.out.println(formatter.format(date));
        //System.out.println(doctorChoiceBox.getSelectionModel().getSelectedIndex());

        try{

            //Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            System.out.println(c);
            PreparedStatement statement = c.prepareStatement("INSERT INTO WIZYTY(pesel_pacjenta, id_pracownika, data) VALUES(?, ?, ?)");
            statement.setString(1, selectedPatientPesel);
            statement.setInt(2, doctorList.get(Integer.valueOf(doctorChoiceBox.getSelectionModel().getSelectedIndex())).getId());
            statement.setString(3, formatter.format(date));
            statement.executeUpdate();


            statement = c.prepareStatement("SELECT MAX(id_wizyty) FROM Wizyty");
            ResultSet resultSet = statement.executeQuery();
            int lastVisitId = -1;
            while(resultSet.next()){
                lastVisitId = resultSet.getInt(1);
            }

            String query = "INSERT INTO SZCZEGOLY_WIZYT (id_wizyty, id_uslugi) VALUES ";

            for(int i = 0; i < selectedServiceList.size(); i++) {
                query += "(" + lastVisitId + "," + selectedServiceList.get(i).getIdService() + ")";
                if (i != selectedServiceList.size() - 1) query += ", ";
            }

            statement = c.prepareStatement(query);
            statement.executeUpdate();
            //if(c != null) Main.pool.returnConnection(c);

            //poprawne lub niepoprawne dodanie i zamkniecie okna
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Dodano wizyte!");
            alert.show();
            WindowManager.getInstance().closeWindow();


        }catch(Exception e){
            e.printStackTrace();
        }



    }
}
