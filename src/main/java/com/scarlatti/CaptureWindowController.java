package com.scarlatti;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 6/19/2020
 */
public class CaptureWindowController implements Initializable {


    @FXML
    private TextField textField;

    @FXML
    private Button captureButton;

    @FXML
    private Button ejectButton;

    @FXML
    private Pane embedPane;

    @FXML
    private Tab tab1;

    private JavaFxWindowCapturer windowCapturer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        windowCapturer = new JavaFxWindowCapturer(embedPane, tab1.selectedProperty());
        captureButton.setOnAction(e -> windowCapturer.captureWindow(textField.getText()));
        ejectButton.setOnAction(e -> windowCapturer.ejectWindow());
    }
}
