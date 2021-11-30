package przychodnialekarska.controller;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;
import przychodnialekarska.objectClass.Pacjent;
import przychodnialekarska.objectClass.Usluga;
import przychodnialekarska.utils.LanguageString;
import przychodnialekarska.utils.UtilFunction;
import przychodnialekarska.utils.Variables;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class MakeEDocController implements Initializable {

    @FXML
    private TextField name1MedicineTextField, name2MedicineTextField, name3MedicineTextField, peselFilterTextField, nameTextField, surnameTextField, addressTextField, zipTextField, nameServiceTextField;

    @FXML
    private ChoiceBox serviceChoiceBox;

    @FXML
    private Label costServiceLabel;

    @FXML
    private TextArea desc1MedicineTextField, desc2MedicineTextField, desc3MedicineTextField, descServiceTextArea;

    @FXML
    private DatePicker datePicker;

    private ArrayList<Usluga> serviceList;
    private ArrayList<Pacjent> patientList;
    private Pacjent selectedPatient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceList = new ArrayList<Usluga>();
        patientList = new ArrayList<Pacjent>();
        selectedPatient = null;

        loadDatabase();

        serviceChoiceBox.getSelectionModel().selectedIndexProperty().addListener((num) -> onClickOnService());
        peselFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> liveFilter(observable, oldValue, newValue));

        peselFilterTextField.setOnKeyPressed((keyEvent) -> fillPesel(keyEvent));

        TextFormatter onlyDigitPeselFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> Pattern.compile("^[0-9]*$").matcher(change.getControlNewText()).matches() ? change : null);

        peselFilterTextField.setTextFormatter(onlyDigitPeselFormatter);
        peselFilterTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.peselFilterTextField, 11));

        name1MedicineTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.name1MedicineTextField, 20));
        name2MedicineTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.name2MedicineTextField, 20));
        name3MedicineTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.name3MedicineTextField, 20));

        desc1MedicineTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextAreaListener(observable, oldvalue, newvalue, this.desc1MedicineTextField, 60));
        desc2MedicineTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextAreaListener(observable, oldvalue, newvalue, this.desc2MedicineTextField, 60));
        desc3MedicineTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextAreaListener(observable, oldvalue, newvalue, this.desc3MedicineTextField, 60));

    }

    private void fillPesel(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            if(selectedPatient != null){
                peselFilterTextField.setText(selectedPatient.getPesel());
            }
        }
    }


    private void liveFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if(newValue.length() == 0){
            nameTextField.clear();
            surnameTextField.clear();
            addressTextField.clear();
            zipTextField.clear();
            selectedPatient = null;
            return;
        }

        for(Pacjent p : patientList){
            if(p.getPesel().startsWith(newValue)){
                //nameSurnamePatientLabel.setText(p.getName() + " " + p.getSurname());
                nameTextField.setText(p.getName());
                surnameTextField.setText(p.getSurname());
                addressTextField.setText(p.getAddress());
                zipTextField.setText(p.getZipPostCode());
                selectedPatient = p;
                break;
            }else{
                nameTextField.clear();
                surnameTextField.clear();
                addressTextField.clear();
                zipTextField.clear();
                selectedPatient = null;
            }
        }
    }


    private void onClickOnService() {
        int index = serviceChoiceBox.getSelectionModel().getSelectedIndex();
        if(index == -1) return;

        nameServiceTextField.setText(serviceList.get(index).getNameService());
        descServiceTextArea.setText(serviceList.get(index).getDescriptionService());
        costServiceLabel.setText(serviceList.get(index).getCostService() + " PLN");

    }

    private void loadDatabase(){
        //ladowanie uslug
        try{
            Connection c = DatabaseManager.getConnection();
            Statement statement = c.createStatement();
            String sql = "SELECT * from USLUGI";

            ResultSet res = statement.executeQuery(sql);
            while(res.next()) {
                serviceList.add(new Usluga(res.getInt("id_uslugi"), res.getString("nazwa"), res.getString("opis"),res.getDouble("koszt")));
                serviceChoiceBox.getItems().add(res.getString("nazwa"));
            }

            statement = c.createStatement();
            sql = "SELECT * from PACJENCI";
            ResultSet resPac = statement.executeQuery(sql);
            while(resPac.next()) {
                patientList.add(new Pacjent(resPac.getString("pesel_pacjenta"),resPac.getString("imie"),resPac.getString("nazwisko"),-1,resPac.getString("adres"),resPac.getString("kod_pocztowy"),""));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveeReceptClick(ActionEvent event) {
        if(selectedPatient == null) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_PATIENT_SELECTED).show();return;}
        if(name1MedicineTextField.getText().isEmpty()) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_NAME_FIRST_MEDICINE).show();return;}
        if(desc1MedicineTextField.getText().isEmpty()) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_DESCRIPTION_FIRST_MEDICINE).show();return;}


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz receptę");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = fileChooser.showSaveDialog(WindowManager.getInstance().getCurrentWindow());
        if(file != null){
            try{

                PdfReader reader = new PdfReader(getClass().getResource("/pdf/recepta.pdf"));
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file.getPath()));

                AcroFields form = stamper.getAcroFields();
                form.setField("kod",  String.format("%04d", new Random().nextInt(10000)));
                form.setField("pacjent", selectedPatient.getName() +  " " + selectedPatient.getSurname());
                form.setField("lekarz", Variables.imie + " " + Variables.nazwisko);

                form.setField("nazwa1", name1MedicineTextField.getText());
                form.setField("zalecenia1", desc1MedicineTextField.getText());

                form.setField("nazwa2", name2MedicineTextField.getText());
                form.setField("zalecenia2", desc2MedicineTextField.getText());

                form.setField("nazwa3", name3MedicineTextField.getText());
                form.setField("zalecenia3", desc3MedicineTextField.getText());

                stamper.setFormFlattening(true);
                stamper.close();
                reader.close();
                Desktop.getDesktop().open(file);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void cancelClick(ActionEvent event) {
        WindowManager.getInstance().getCurrentWindow().close();
    }

    public void saveeReferralClick(ActionEvent event) {
        if(selectedPatient == null) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_PATIENT_SELECTED).show();return;}
        if(serviceChoiceBox.getSelectionModel().getSelectedIndex() == -1) { UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_SERVICE_SELECTED).show(); return;}
        if(datePicker.getValue() == null) { UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_DATE_SELECTED).show(); return;}


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz skierowanie");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = fileChooser.showSaveDialog(WindowManager.getInstance().getCurrentWindow());
        if(file != null){
            try{
                PdfReader reader = new PdfReader(getClass().getResource("/pdf/skierowanie.pdf"));
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file.getPath()));

                AcroFields form = stamper.getAcroFields();
                form.setField("kod",  String.format("%04d", new Random().nextInt(10000)));
                form.setField("pacjent", selectedPatient.getName() +  " " + selectedPatient.getSurname());
                form.setField("pesel", selectedPatient.getPesel());
                form.setField("badanie", serviceList.get(serviceChoiceBox.getSelectionModel().getSelectedIndex()).getNameService());
                form.setField("data", String.valueOf(datePicker.getValue()));
                form.setField("lekarz", Variables.imie + " " + Variables.nazwisko);

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
