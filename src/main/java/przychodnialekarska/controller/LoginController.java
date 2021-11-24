package przychodnialekarska.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.Main;
import przychodnialekarska.WindowManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LoginController /* implements Initializable*/{

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private CheckBox checkPass;

    @FXML
    private ProgressBar progressBar;


    public void logIn(ActionEvent event){
        if(passwordField.getText().isEmpty() || usernameField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Uzupełnij wszystkie pola!");
            alert.show();
            return;
        }

        try{
            //Connection c = Main.pool.getConnection();
            Connection c = DatabaseManager.getConnection();
            //Statement statement = c.createStatement();
            String sql = "SELECT * FROM pracownicy WHERE login = ? AND haslo = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, usernameField.getText());
            statement.setString(2, passwordField.getText());
            //ResultSet res = statement.executeQuery("SELECT * FROM pracownicy WHERE login = '" + usernameField.getText() + "' AND haslo = '" + passwordField.getText() +"'");
            ResultSet res = statement.executeQuery();
            System.out.println("There are below tables:");
            if(res.next()) {
                Main.imie = res.getString(2);
                Main.nazwisko = res.getString(3);
                Main.poziomUprawnien = Integer.valueOf(res.getString(11));
                System.out.println("Zalogowany!");
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING, "Niepoprawne dane logowania!");
                alert.show();
                return;
            }
            //if(c != null) Main.pool.returnConnection(c);
            //Main.pool.getConnection().mak
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
                    System.out.println("koniec");
                }catch(Exception e){
                    e.printStackTrace();
                }
                //Main.poziomUprawnien = 0;
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Parent root = null;
                        try {
                            System.out.println("LADOWANIE MENU: " + Main.class.getResource("/fxml/MenuForm.fxml"));
                            root = FXMLLoader.load(Main.class.getResource("/fxml/MenuForm.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene scene = new Scene(root);

                        Stage stage = new Stage();
                        WindowManager.getInstance().setMainWindow(stage);
                        stage.setTitle("System informatyczny obsługi przychodni lekarskiej - Menu");
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


    public void viewPass(ActionEvent event){

        if(checkPass == null) return;
        if(passwordField == null){
            //System.out.println(usernameField.getText());
            return;
        }

        //poprawic ukrywanie i pokazywanie hasla !!!

        if(checkPass.isSelected()){
            passwordField.setPromptText(passwordField.getText());
            passwordField.setText("");

        }else{
            passwordField.setText(passwordField.getPromptText());
            passwordField.setText("");

        }
    }



    //  @Override
   // public void initialize(URL url, ResourceBundle resourceBundle) {

    //}
}
