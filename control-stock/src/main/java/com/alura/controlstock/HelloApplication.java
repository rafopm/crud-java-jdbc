package com.alura.controlstock;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        URI uri = Paths.get("src/main/java/com/alura/controlstock/view/ViewProducto.fxml").toAbsolutePath().toUri();
        System.out.println("aqui "+uri);
        Parent root = FXMLLoader.load(uri.toURL());
        Scene scene = new Scene(root);
        stage.setTitle("Producto");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}