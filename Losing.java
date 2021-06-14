import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Losing extends Application{

    private Client client;
    private Stage ps;


    public Losing(){
    
    }

    public void start(Stage primaryStage) {
        ps = primaryStage;
        Button startGame = new Button("YOU LOSE!");

        Scene scene = new Scene(startGame, 200, 250);
        ps.setTitle("Losing Screen");
        ps.setScene(scene);
        ps.show();
    }


}