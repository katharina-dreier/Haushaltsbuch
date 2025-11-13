package org.meinprojekt.haushalt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader fxml = new FXMLLoader(
        getClass().getResource("/org/meinprojekt/haushalt/ui/main.fxml")
    );
    Scene scene = new Scene(fxml.load());
    
    stage.setTitle("Haushaltsbuch");
    stage.setScene(scene);
    stage.setMaximized(true);
    scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
