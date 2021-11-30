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
import javafx.stage.FileChooser;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.WindowManager;
import przychodnialekarska.objectClass.*;
import przychodnialekarska.utils.LanguageString;
import przychodnialekarska.utils.UtilFunction;
import przychodnialekarska.utils.Variables;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class AddDescriptionOfExaminationController implements Initializable {

    @FXML
    private TableView visitTable;

    @FXML
    private TableColumn idColumn, peselColumn, nameColumn, surnameColumn, dateColumn;

    @FXML
    private ListView serviceList, attachmentListView;

    @FXML
    private TextArea descriptionTextArea, resultTextArea, recommendationsTextArea;

    @FXML
    private CheckBox recipeCheckBox;

    @FXML
    private Button cancelButton, confirmButton, removeFilterButton, filterButton, addFileButton, removeFileButton;

    @FXML
    private TextField peselFilterTextField, nameFilterTextField, surnameFilterTextField;

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private Label serviceNameLabel;


    private ObservableList<VisitWrapper> visitList;

    private ArrayList<File> attachmentList;

    private String additional;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        visitTable.setPlaceholder(new Label("Brak wizyt!"));
        idColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("id"));
        peselColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("peselPatient"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("namePatient"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("surnamePatient"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<VisitWrapper, String>("date"));

        TextFormatter onlyDigitPeselFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> Pattern.compile("^[0-9]*$").matcher(change.getControlNewText()).matches() ? change : null);

        peselFilterTextField.setTextFormatter(onlyDigitPeselFormatter);
        peselFilterTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.peselFilterTextField, 11));
        nameFilterTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.nameFilterTextField, 26));
        surnameFilterTextField.textProperty().addListener((observable, oldvalue, newvalue) -> UtilFunction.changeTextFieldListener(observable, oldvalue, newvalue, this.surnameFilterTextField, 26));

        //Live search
        peselFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        nameFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        surnameFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> liveFilter());
        dateDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> liveFilter());

        dateDatePicker.getEditor().setDisable(true);

        visitList = FXCollections.observableArrayList();

        attachmentList = new ArrayList<File>();
        additional = (Variables.poziomUprawnien == 1)?Variables.additionalQuery:"";

        loadDatabase();
        visitTable.setItems(visitList);
        visitTable.getSelectionModel().selectedIndexProperty().addListener((num) -> onClickPatient());
        serviceList.getSelectionModel().selectedIndexProperty().addListener((num) -> onClickService());
        attachmentListView.getSelectionModel().selectedIndexProperty().addListener((num) -> onClickAttachment());


    }




    private void liveFilter() {
        ObservableList<VisitWrapper> tempVisitList = FXCollections.observableArrayList();

        for(VisitWrapper visitWrapper : visitList){
            if(peselFilterTextField.getText().length() > 0 && !(visitWrapper.getPeselPatient().toLowerCase().startsWith(peselFilterTextField.getText()))) continue;
            if(nameFilterTextField.getText().length() > 0 && !(visitWrapper.getNamePatient().toLowerCase().startsWith(nameFilterTextField.getText()))) continue;
            if(surnameFilterTextField.getText().length() > 0 && !(visitWrapper.getSurnamePatient().toLowerCase().startsWith(surnameFilterTextField.getText()))) continue;

            if(dateDatePicker.getValue() != null && !(visitWrapper.getDate().equals(dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))) continue;
            tempVisitList.add(visitWrapper);
        }
        visitTable.setItems(tempVisitList);
        disable();
        serviceList.getItems().clear();
    }

    public void removeFilter(ActionEvent event) {
        peselFilterTextField.clear();
        nameFilterTextField.clear();
        surnameFilterTextField.clear();
        dateDatePicker.setValue(null);
        disable();
        serviceList.getItems().clear();
    }

    private void onClickAttachment() {
        removeFileButton.setDisable(false);
    }


    private void onClickService() {
        int index = serviceList.getSelectionModel().getSelectedIndex();
        if(index == -1){
            serviceNameLabel.setText("");
            return;
        }
        descriptionTextArea.setEditable(true);
        resultTextArea.setEditable(true);
        recommendationsTextArea.setEditable(true);
        addFileButton.setDisable(false);
        recipeCheckBox.setDisable(false);
        if(visitTable.getSelectionModel().getSelectedIndex() == -1) return;
        serviceNameLabel.setText(visitList.get(visitTable.getSelectionModel().getSelectedIndex()).getUslugi().get(index).getNameService());
    }

    private void onClickPatient() {
        int index = visitTable.getSelectionModel().getSelectedIndex();
        if(index == -1) return;
        serviceList.getItems().clear();
        for(Usluga u : visitList.get(index).getUslugi()){
            serviceList.getItems().add(u.getNameService());
        }
        disable();
    }

    private void disable(){
        descriptionTextArea.setEditable(false);
        descriptionTextArea.clear();
        resultTextArea.setEditable(false);
        resultTextArea.clear();
        recommendationsTextArea.setEditable(false);
        recommendationsTextArea.clear();
        addFileButton.setDisable(true);
        recipeCheckBox.setDisable(true);
        recipeCheckBox.setSelected(false);
        serviceNameLabel.setText("");

    }

    private void loadDatabase(){
        ArrayList<WynikiBadan> tempWyniki = new ArrayList<WynikiBadan>();
        visitList.clear();
        disable();
        serviceList.getItems().clear();
        try{
            Connection c = DatabaseManager.getConnection();
            Statement statement = c.createStatement();
            ResultSet resultSetWyniki = statement.executeQuery("SELECT * FROM WYNIKI_BADAN");
            while(resultSetWyniki.next()) tempWyniki.add(new WynikiBadan(resultSetWyniki.getInt("id_badania"), resultSetWyniki.getInt("id_uslugi"),resultSetWyniki.getInt("id_wizyty"),null,null,null,null,null));

            statement = c.createStatement();
            String sql = "SELECT wizyty.id_wizyty, pacjenci.pesel_pacjenta, pacjenci.imie, pacjenci.nazwisko, wizyty.data, wizyty.id_pracownika FROM WIZYTY INNER JOIN pacjenci USING (pesel_pacjenta)" + additional;
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                Statement newStatement = c.createStatement();
                ResultSet newResultSet = newStatement.executeQuery("SELECT szczegoly_wizyt.id_uslugi, uslugi.nazwa, uslugi.opis FROM SZCZEGOLY_WIZYT INNER JOIN uslugi USING (id_uslugi) WHERE id_wizyty = '" + resultSet.getInt("id_wizyty") + "'");
                ArrayList<Usluga> tempServiceList = new ArrayList<Usluga>();
                while(newResultSet.next()){
                    if(!exist(tempWyniki, newResultSet.getInt("id_uslugi"), resultSet.getInt("id_wizyty"))) tempServiceList.add(new Usluga(newResultSet.getInt("id_uslugi"),newResultSet.getString("nazwa"),newResultSet.getString("opis"),-1));
                }
                newResultSet.close();
                if(tempServiceList.size() > 0) visitList.add(new VisitWrapper(new Pacjent(resultSet.getString("pesel_pacjenta"),resultSet.getString("imie"),resultSet.getString("nazwisko"),-1,"","",""), new Wizyta(resultSet.getInt("id_wizyty"),"","",resultSet.getString("data")), null, tempServiceList));
            }
            resultSet.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean exist(ArrayList<WynikiBadan> wb, int id_uslugi, int id_wizyty){
        for(int i = 0; i < wb.size(); i++){
            if(wb.get(i).getIdService() == id_uslugi && wb.get(i).getIdVisit() == id_wizyty) {
                return true;
            }
        }
        return false;
    }


    public void addFileClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Dodaj zdjęcie do badania");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JPG/JPEG/PNG", "*.jpeg","*.png","*.jpg")
        );
        File file = fileChooser.showOpenDialog(WindowManager.getInstance().getCurrentWindow());
        if(file != null){
            if(file.length() / 1048576 > 10) {
                UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.TOO_LARGE_FILE_SIZE).show(); return;
            }
            attachmentList.add(file);
            attachmentListView.getItems().add(file.getName());
        }
    }

    public void removeFileClick(ActionEvent event) {
        int index = attachmentListView.getSelectionModel().getSelectedIndex();
        if(index == -1) return;
        attachmentListView.getItems().remove(index);
        attachmentList.remove(index);
        if(attachmentListView.getItems().size() == 0) removeFileButton.setDisable(true);
    }

    public void confirmClick(ActionEvent event) {
        if(visitTable.getSelectionModel().getSelectedIndex() == -1) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_VISIT_SELECTED).show();return;}
        if(serviceList.getSelectionModel().getSelectedIndex() == -1) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_SERVICE_SELECTED).show();return;}
        if(descriptionTextArea.getText().isEmpty()) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_DESCRIPTION_OF_THE_EXAMINATION).show();return;}
        if(resultTextArea.getText().isEmpty()) {UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_EXAMINATION_RESULT).show();return;}

        String optionalString = " Przypominamy, że nie uzupełniono takich pól jak: ";
        int sizeString = optionalString.length();
        if(recommendationsTextArea.getText().isEmpty()) optionalString += "zalecenia, ";
        if(!recipeCheckBox.isSelected()) optionalString += "czy recepta jest wystawiona, ";
        if(attachmentList.size() == 0) optionalString += "załączniki, ";
        if(optionalString.charAt(optionalString.length()-2) == ',') optionalString.substring(optionalString.length()-2);


        Alert alert = UtilFunction.showAlert(Alert.AlertType.CONFIRMATION, LanguageString.ASK_FOR_ADD_DESCRIPTION_OF_EXAMINATION + ((sizeString != optionalString.length())?optionalString:""));
        alert.showAndWait();
        if(alert.getResult() == ButtonType.CANCEL || alert.getResult() == ButtonType.CLOSE) return;

        int lastId = -1;
        try{
            Connection c = DatabaseManager.getConnection();
            PreparedStatement statement = c.prepareStatement("INSERT INTO WYNIKI_BADAN(id_uslugi, id_wizyty, pesel_pacjenta, opis_badania, wynik_badania, zalecenia, recepta) VALUES(?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, visitList.get(visitTable.getSelectionModel().getSelectedIndex()).getUslugi().get(serviceList.getSelectionModel().getSelectedIndex()).getIdService());
            statement.setInt(2, visitList.get(visitTable.getSelectionModel().getSelectedIndex()).getId());
            statement.setString(3, visitList.get(visitTable.getSelectionModel().getSelectedIndex()).getPeselPatient());
            statement.setString(4, descriptionTextArea.getText());
            statement.setString(5, resultTextArea.getText());
            statement.setString(6, recommendationsTextArea.getText());
            statement.setString(7, (recipeCheckBox.isSelected()?"Tak":"Nie"));
            statement.executeUpdate();

            if(attachmentList.size() > 0) {
                statement = c.prepareStatement("SELECT MAX(id_badania) FROM WYNIKI_BADAN");
                ResultSet resultSet = statement.executeQuery();

                while(resultSet.next()){
                    lastId = resultSet.getInt(1);
                }
                resultSet.close();

                String query = "INSERT INTO SZCZEGOLY_BADAN (id_badania, file_name) VALUES ";
                for(int i = 0 ; i < attachmentList.size(); i++){
                    query += "(" + lastId + ",'" + attachmentList.get(i).getName() + "')";
                    if (i != attachmentList.size() - 1) query += ", ";
                }

                PreparedStatement statemen2 = c.prepareStatement(query);
                statemen2.executeUpdate();
            }

        }catch(Exception e){
            e.printStackTrace();
        }


            for (int i = 0; i < attachmentList.size(); i++) {


                attachmentList.get(i).setReadable(true);


                try{


                    BufferedImage img = ImageIO.read(attachmentList.get(i));
                    File directory = new File("images/" + lastId);
                    if(!directory.exists()) directory.mkdir();

                    File f = new File("images/" + lastId + "/" + attachmentList.get(i).getName());
                    ImageIO.write(img, "PNG", f);
                }catch(Exception e){

                }
            }

        alert = UtilFunction.showAlert(Alert.AlertType.CONFIRMATION, LanguageString.ASK_FOR_NEXT_ADD_DESCRIPTION_OF_EXAMINATION);
        alert.showAndWait();
        if(alert.getResult() == ButtonType.CANCEL || alert.getResult() == ButtonType.CLOSE){
            WindowManager.getInstance().getCurrentWindow().close();
            return;
        }else{
            loadDatabase();
        }

    }


}
