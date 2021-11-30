package przychodnialekarska.utils;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import przychodnialekarska.WindowManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilFunction {

    public static void changeTextFieldListener(final ObservableValue<? extends String> ov, final String oldValue, final String newValue, TextField textField, final Integer maxLength) {
        if (textField.getText().length() > maxLength) {
            String s = textField.getText().substring(0, maxLength);
            textField.setText(s);
        }
    }
    public static void changeTextAreaListener(final ObservableValue<? extends String> ov, final String oldValue, final String newValue, TextArea textArea, final Integer maxLength) {
        if (textArea.getText().length() > maxLength) {
            String s = textArea.getText().substring(0, maxLength);
            textArea.setText(s);
        }
    }


    public static Alert showAlert(Alert.AlertType alertType, String text){
        return new Alert(alertType, text);
    }

    public static String encryptThisString(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadButtons(){
        for(int i = 0; i < Variables.windowsNames.size(); i++){
            Button temp = new Button(Variables.windowsNames.get(i));
            temp.setOnAction(e -> WindowManager.switchScene(e));
            temp.setAlignment(Pos.CENTER);
            temp.setId(String.valueOf(i));

            temp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            Variables.buttonArrayList.add(temp);
        }

    }
}
