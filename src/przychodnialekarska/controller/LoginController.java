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
import javafx.stage.Window;
import przychodnialekarska.Main;
import przychodnialekarska.WindowManager;

import java.io.IOException;


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

                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("../fxml/MenuForm.fxml"));
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
