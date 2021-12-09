package przychodnialekarska.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    @FXML
    private MediaView mediaView;

    @FXML
    private Button startStopButton;

    @FXML
    private Label actualTimeLabel, totalTimeLabel, titleLabel;

    @FXML
    private Slider seekSlider;

    private File file;
    private Media media;
    public static MediaPlayer mediaPlayer;
    public static String resourceName = "kitty.mp4";
    public static String title = "";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //file = new File(getClass().getResource("/mp4/kitty.mp4").toURI());

        //TODO dodać do kazdego helpa to okienko

        try {
            media = new Media(getClass().getResource("/mp4/" + resourceName).toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        titleLabel.setText(title);
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
