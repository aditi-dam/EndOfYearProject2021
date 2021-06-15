import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Welcome extends Application {

    private Client client;
    private Stage ps;

    public Welcome(Client cl){
        client = cl;
    }

    public void start(Stage primaryStage) {
        ps = primaryStage;

        ps.setTitle("Welcome Screen");

        Text text = new Text("Pictionary!");

        Stop[] stops = new Stop[] { 
            new Stop(0, Color.YELLOW),  
            new Stop(1, Color.GREEN)
        };  
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops); 
        text.setFill(Color.DARKVIOLET);
        text.setFont(Font.font ("Phosphate",65)); 

        //Image img = new Image("pict.jpg");
        //rectangle.setFill(new ImagePattern(img));

        Button showDirections = new Button("DIRECTIONS");
        showDirections.setFont(Font.font("Phosphate", 20));
        showDirections.setMaxSize(200, 200);
        showDirections.setOnAction(e -> openDirections());
        
        Button startGame = new Button("WHITEBOARD!");
        showDirections.setFont(Font.font("Phosphate", 20));
        startGame.setMaxSize(200, 200);
        startGame.setOnAction(e -> openGame());
        
        VBox vbox = new VBox(50, text, showDirections, startGame);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 700, 300);
        vbox.getStylesheets().add(
            getClass().getResource("style.css").toExternalForm());
        vbox.setStyle("-fx-padding: 10;" + 
                      "-fx-border-style: dashed inside;" + 
                      "-fx-border-width: 5;" +
                      "-fx-border-insets: 5;" + 
                      "-fx-border-radius: 5;" + 
                      "-fx-border-color: darkviolet;");

        vbox.setBackground(new Background(new BackgroundFill(linearGradient, null, null)));
        
        ps.setTitle("Welcome Screen");
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