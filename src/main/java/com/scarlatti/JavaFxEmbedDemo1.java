package com.scarlatti;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 6/19/2020
 */
public class JavaFxEmbedDemo1 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(new CaptureWindowController());
        Parent parent = fxmlLoader.load(getClass().getResourceAsStream("/CaptureWindow.fxml"));

        Scene scene = new Scene(parent);

        // init JMetro
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFxEmbedDemo1");
//        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

//        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                System.out.println("Asdf");
//            }
//        });
//
//        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                event.consume();
//            }
//        });
    }
}
