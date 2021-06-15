import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Closing extends Application{
    
    private Stage ps;


    public Closing(){
    
    }

    public void start(Stage primaryStage) {
        ps = primaryStage;
        Button startGame = new Button("YOU WIN!");

        Scene scene = new Scene(startGame, 200, 250);
        scene.getStylesheets().add(
            getClass().getResource("closing.css").toExternalForm());
        ps.setTitle("Winning Screen");
        ps.setScene(scene);
        ps.show();
    }


}