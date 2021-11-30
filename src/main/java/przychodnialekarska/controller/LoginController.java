package przychodnialekarska.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.Main;
import przychodnialekarska.WindowManager;
import przychodnialekarska.utils.LanguageString;
import przychodnialekarska.utils.UtilFunction;
import przychodnialekarska.utils.Variables;
import przychodnialekarska.utils.VisiblePasswordFieldSkin;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;


public class LoginController  implements Initializable {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private CheckBox checkPass;

    @FXML
    private ProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordField.setSkin(new VisiblePasswordFieldSkin(passwordField));
    }

    public void logIn(ActionEvent event){
        if(usernameField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_FOUND_LOGIN).show(); return;}
        if(passwordField.getText().isEmpty()){ UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.NO_FOUND_PASSWORD).show(); return;}

        try{
            Connection c = DatabaseManager.getConnection();
            String sql = "SELECT * FROM pracownicy WHERE login = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, usernameField.getText());
            //statement.setString(2, UtilFunction.encryptThisString(passwordField.getText()));
            //ResultSet res = statement.executeQuery("SELECT * FROM pracownicy WHERE login = '" + usernameField.getText() + "' AND haslo = '" + passwordField.getText() +"'");
            ResultSet res = statement.executeQuery();

            if(res.next()) {
                if(res.getString("haslo").equals(UtilFunction.encryptThisString(passwordField.getText()))){
                    Variables.imie = res.getString(2);
                    Variables.nazwisko = res.getString(3);
                    Variables.poziomUprawnien = Integer.valueOf(res.getString(11));
                    Variables.id_pracownika = res.getInt("id_pracownika");

                }else{
                    UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.BAD_PASSWORD).show();
                    return;
                }

            }else{
                UtilFunction.showAlert(Alert.AlertType.WARNING, LanguageString.BAD_LOGIN).show();

                return;
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        progressBar.setOpacity(1);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i = 1; i <= 100; i++){
                        progressBar.setProgress(i*0.01);
                        Thread.sleep(30);

                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                //Main.poziomUprawnien = 0;
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Parent root = null;
                        try {

                            root = FXMLLoader.load(Main.class.getResource("/fxml/MenuForm.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene scene = new Scene(root);

                        Stage stage = new Stage();
                        stage.setResizable(false);
                        WindowManager.getInstance().setMainWindow(stage);
                        stage.setTitle("System informatyczny obsługi przychodni lekarskiej - Menu");
                        stage.getIcons().add(new Image("/img/logo.png"));
                        stage.setScene(scene);

                        stage.show();


                        ((Node)(event.getSource())).getScene().getWindow().hide();
                    }


                });

            }

        });
        t.start();

    }


    public void helpAction(javafx.scene.input.MouseEvent event){
        System.out.println("help button");
    }



}
