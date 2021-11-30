package przychodnialekarska.controller;

import com.itextpdf.text.pdf.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import przychodnialekarska.WindowManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    @FXML
    private MediaView mediaView;

    @FXML
    private Button startStopButton;

    @FXML
    private Label actualTimeLabel, totalTimeLabel;

    @FXML
    private Slider seekSlider;

    private File file;
    private Media media;
    private MediaPlayer mediaPlayer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //file = new File(getClass().getResource("/mp4/kitty.mp4").toURI());

        //TODO dodać do kazdego helpa to okienko

        try {
            media = new Media(getClass().getResource("/mp4/kitty.mp4").toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        seekSlider.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::changeLabelHandler);
        seekSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, this::changeLabelHandler);

        mediaPlayer.currentTimeProperty().addListener(((observableValue, duration, t1) -> {
            seekSlider.setValue(t1.toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
            long secs = (long) t1.toSeconds();
            actualTimeLabel.setText(String.format("%01d:%02d", (secs % 3600)/60,secs % 60));
        }));

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                long secs = (long) media.getDuration().toSeconds();
                String time = String.format("%01d:%02d", (secs % 3600)/60,secs % 60);
                totalTimeLabel.setText(time);
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();
                startStopButton.setText("Start");

            }
        });


    }

    public void startStopButtonClick(ActionEvent e){
        if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)){
            mediaPlayer.pause();
            startStopButton.setText("Start");
        }else{
            mediaPlayer.play();
            startStopButton.setText("Pauza");
        }
        /*
        PrinterJob job = PrinterJob.createPrinterJob();

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../fxml/LoginForm.fxml"));
        } catch (IOException X) {
            X.printStackTrace();
        }
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        //WindowManager.getInstance().setMainWindow(stage);
        stage.setTitle("System informatyczny obsługi przychodni lekarskiej - Menu");
        stage.setScene(scene);

        //stage.show();


        if(job != null){
            job.showPrintDialog(WindowManager.getInstance().getCurrentWindow());
            job.printPage(root);
            job.endJob();
        }

         */

        /*
        try {


            JFileChooser chooser = new JFileChooser();
            chooser.setAcceptAllFileFilterUsed(false);
            //chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory()){
                        return true;
                    }else{
                        return f.getName().toLowerCase().endsWith(".pdf");
                    }
                }

                @Override
                public String getDescription() {
                    return "PDF Document (*.pdf)";
                }
            });
            int option = chooser.showSaveDialog(null);
            String path = "C:/output.pdf";
            if(option == JFileChooser.APPROVE_OPTION){
                path = chooser.getSelectedFile().getAbsolutePath();
            }

            PdfReader reader;
            reader = new PdfReader("src/example.pdf");
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(path));
            //PdfDocument x = new PdfDocument(new PdfReader((new FileInputStream("src/example.pdf")),
           //                                 new PdfWriter(new FileOutputStream(path))));

            AcroFields form = stamper.getAcroFields();
            form.setField("name", "Przykładowa nazwa");
            form.
            stamper.setFormFlattening(true);

            stamper.close();
            reader.close();

        } catch (Exception ioException) {
            ioException.printStackTrace();
        }



         */


    }

    public void mediaViewClick(MouseEvent e){
        startStopButtonClick(new ActionEvent());
    }

    private void changeLabelHandler(MouseEvent e) {
        long val = (long) (seekSlider.getValue() * mediaPlayer.getTotalDuration().toMillis() / 100);
        mediaPlayer.seek(Duration.millis(val));

        val = (long) mediaPlayer.getCurrentTime().toSeconds();
        actualTimeLabel.setText(String.format("%01d:%02d", (val % 3600)/60,val % 60));

        if(!mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) mediaPlayer.pause();
    }

}
