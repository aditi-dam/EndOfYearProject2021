import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Welcome extends Application {

    private Client client;
    private Stage ps;


    public Welcome(Client cl){
        client = cl;
    }

    public void start(Stage primaryStage) {
        ps = primaryStage;

        Button showDirections = new Button("Directions");
        showDirections.setMaxSize(200, 200);
        showDirections.setOnAction(e -> openDirections());
        
        Button startGame = new Button("PLAY!");
        startGame.setMaxSize(200, 200);
        startGame.setOnAction(e -> openGame());
        
        VBox vbox = new VBox(50, showDirections, startGame);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 200, 250);
        
        ps.setTitle("MyJavaFX");
        ps.setScene(scene);
        ps.show();
    }

    public void openDirections() {
        client.openDirections();
    }

    public void openGame(){
        client.startWhiteboard(ps);
    }
}