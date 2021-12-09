package przychodnialekarska.controller;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;
import przychodnialekarska.objectClass.Lekarz;
import przychodnialekarska.objectClass.Pacjent;
import przychodnialekarska.objectClass.Usluga;
import przychodnialekarska.utils.LanguageString;
import przychodnialekarska.utils.UtilFunction;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MakeVisitController implements Initializable {

    private ArrayList<Usluga> serviceList;
    private ArrayList<Pacjent> patientList;
    private ArrayList<Lekarz> doctorList;

    private String selectedPatientPesel;
    private ArrayList<Usluga> selectedServiceList;
    private int selectedDoctorId;


    @FXML
    private Label nameSurnamePatientLabel, priceServicesLabel;

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
    private Button removeButton, cancelButton, addButton, registerNewPatientButton, addServiceButton, billButton;

    private double priceServices;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceList = new ArrayList<Usluga>();
        patientList = new ArrayList<Pacjent>();
        doctorList = new ArrayList<Lekarz>();
        selectedServiceList = new ArrayList<Usluga>();
        selectedPatientPesel = "";
        priceServices = 0.0;
        priceServicesLabel.setText(priceServices + " PLN");

        loadDatabase();

        for(Lekarz l : doctorList){
            doctorChoiceBox.getItems().add("lek. " + l.getName() + " " + l.getSurname());
        }

        for(Usluga u : serviceList){
            addServiceComboBox.getItems().add(u.getNameService());
        }

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

        });

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
                patientList.add(new Pacjent(res.getString("pesel_pacjenta"), res.getString("imie"), res.getString("nazwisko"), -1, "address", null, null));
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

        serviceListView.getItems().add(serviceList.get(index).getNameService()); //dodaje do listy
        selectedServiceList.add(serviceList.get(index)); //dodaje do listy ktora przechowuje uslugi
        priceServices += serviceList.get(index).getCostService();
        serviceList.remove(index); //usuwam z domyslnej puli

        addServiceComboBox.getItems().remove(index); //usuwam z comboboxa

        priceServicesLabel.setText(priceServices + " PLN");

        addServiceComboBox.setValue(null);

    }

    public void removeService(ActionEvent event) {
        int index = serviceListView.getSelectionModel().getSelectedIndex();
        if(index == -1) return; //informacja ze nei wybrano uslugi

        addServiceComboBox.getItems().add(selectedServiceList.get(index).getNameService()); // add to combobox
        serviceList.add(selectedServiceList.get(index)); //dodaje do glownej
        priceServices -= selectedServiceList.get(index).getCostService();
        selectedServiceList.remove(index);
        serviceListView.getItems().remove(index); //usuwam z listy
        priceServicesLabel.setText(priceServices + " PLN");


    }

    public void cancelButton(ActionEvent event) {
        WindowManager.getInstance().getCurrentWindow().close();
    }

    public void registerNewPatientButton(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddPatientForm.fxml"));

            Stage stage = WindowManager.getInstance().createWindow(root);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void helpAction(javafx.scene.input.MouseEvent event){
        try {
            HelpController.resourceName = "help1.mp4";
            HelpController.title = "Jak umówić pacjenta na wizytę?";
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/HelpForm.fxml"));

            Stage stage = WindowManager.getInstance().createWindow(root);
            stage.setTitle("System informatyczny obsługi przychodni lekarskiej - Pomoc");
            stage.getIcons().add(new Image("/img/logo.png"));
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    HelpController.mediaPlayer.stop();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addVisit(ActionEvent event) {
        if(selectedPatientPesel.length() != 11) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_PESEL_SELECTED).show(); return;}
        if(doctorChoiceBox.getSelectionModel().getSelectedIndex() == -1) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_DOCTOR_SELECTED).show(); return;}
        if(dateDatePicker.getValue() == null) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_DATE_SELECTED).show(); return;}
        if(selectedServiceList.size() == 0) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_SERVICES_ADDED).show(); return;}


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = dateDatePicker.getValue();

        try{
            Connection c = DatabaseManager.getConnection();
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

            //poprawne lub niepoprawne dodanie i zamkniecie okna
            Alert alert = new Alert(Alert.AlertType.INFORMATION, LanguageString.VISIT_ADDED);
            alert.show();
            WindowManager.getInstance().getCurrentWindow().close();


        }catch(Exception e){
            e.printStackTrace();
        }



    }

    public void billClick(ActionEvent event) {
        if(selectedPatientPesel.length() < 11) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_PESEL_SELECTED).show(); return;}
        if(dateDatePicker.getValue() == null) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_DATE_SELECTED).show(); return;}
        if(selectedServiceList.size() == 0) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_SERVICES_ADDED).show(); return;}


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz rachunek");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = fileChooser.showSaveDialog(WindowManager.getInstance().getCurrentWindow());
        if(file != null){
            try{

                PdfReader reader = new PdfReader(getClass().getResource("/pdf/rachunek.pdf"));
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file.getPath()));

                AcroFields form = stamper.getAcroFields();
                form.setField("data", String.valueOf(dateDatePicker.getValue()));
                form.setField("numer",  String.format("%04d", new Random().nextInt(10000)));

                for(int i = 0; i < selectedServiceList.size(); i++){
                    if(i == 5) break;
                    form.setField("usluga" + (i+1), selectedServiceList.get(i).getNameService());
                    form.setField("cena" + (i+1), String.valueOf(selectedServiceList.get(i).getCostService()));
                }

                form.setField("suma", String.valueOf(priceServices));

                stamper.setFormFlattening(true);
                stamper.close();
                reader.close();
                Desktop.getDesktop().open(file);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
}
