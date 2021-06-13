import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Welcome extends Application{

    private Client client;
    private Stage ps;


    public Welcome(Client cl){
        client = cl;
    }

    public void start(Stage primaryStage) {
        ps = primaryStage;
        Button startGame = new Button("PLAY!");
        startGame.setOnAction(e -> openGame());
        Scene scene = new Scene(startGame, 200, 250);
        ps.setTitle("MyJavaFX");
        ps.setScene(scene);
        ps.show();
    }

    public void openGame(){
        client.startWhiteboard(ps);
    }
}