import java.io.PrintWriter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//hi

public class Whiteboard extends Application{

    private Canvas canvas = new Canvas(800, 500); 
    private GraphicsContext gc; 
    private ColorPicker cp = new ColorPicker();
    private Slider slider = new Slider();
    private Label label = new Label("1.0");
    private PrintWriter out;
    private Scene scene;
    private Pane pane;
    private GridPane grid = new GridPane();
    private TextField tf;
    private static String word = "";
    private int guesses = 0;
    private Text guessCount = new Text();
    private Text username = new Text();
    private Stage ps;


    public static String getWord() {
        return word;
    }

    public static void setWord(String word) {
        Whiteboard.word = word;
    }

    public Whiteboard(PrintWriter o, Scene s, Pane p, GraphicsContext g, Canvas c){
        out = o;
        scene = s;
        pane = p;
        gc = g;
        canvas = c;
    }

    public void start(Stage primaryStage) {
        ps = primaryStage;
        try{
            gc.setStroke(Color.BLACK); 
            gc.setLineWidth(5); 

            cp.setValue(Color.BLACK);
            cp.setOnAction(e->{
                gc.setStroke(cp.getValue());
            });

            slider.setMin(1);
            slider.setMax(100);
            slider.setShowTickLabels(true);
            slider.setShowTickMarks(true);
            slider.valueProperty().addListener(e->{
                double value = slider.getValue();
                String str = String.format("%.1f", value);
                label.setText(str);
                gc.setLineWidth(value);
            });

            grid.addRow(0, cp, slider, label);
            grid.setHgap(20);
            grid.setAlignment(Pos.TOP_CENTER);
            grid.setPadding(new Insets(20, 0, 0, 0));

            scene.setOnMousePressed(e->{ 
                gc.beginPath();
                gc.lineTo(e.getSceneX(), e.getSceneY());
                out.println("COORDINATE1: " + "x" + e.getSceneX() + "y" + e.getSceneY());
                gc.stroke();
            });

            scene.setOnMouseDragged(e->{
                gc.lineTo(e.getSceneX(), e.getSceneY());
                out.println("COORDINATE: " + "x" + e.getSceneX() + "y" + e.getSceneY()); 
                gc.stroke();
            });
            
            ps.setScene(scene);
            ps.show();
            HBox h = new HBox(10);
            h.getChildren().add(username);
            h.setAlignment(Pos.TOP_LEFT);
            h.setPadding(new Insets(5,5,5,5));
            username.setFont(Font.font("Verdana",20));
            pane.getChildren().addAll(canvas,h);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void draw(String incoming){
        double x = Double.valueOf(incoming.substring(incoming.indexOf("x") + 1, incoming.indexOf("y")));
        double y = Double.valueOf(incoming.substring(incoming.indexOf("y") + 1));
        gc.lineTo(x, y); 
        gc.stroke();
    }

    public void pictionary(int playerNum){
        if(playerNum == 1){ //display the word on the screen
            Platform.runLater(()->{
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("YOUR WORD IS:");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setResizable(true);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                getClass().getResource("word.css").toExternalForm());

                alert.setHeaderText("");
                alert.setContentText(getWord());
                ButtonType close = new ButtonType("Close");
                alert.getButtonTypes().setAll(close);

                alert.showAndWait();

                //Optional<ButtonType> result = alert.showAndWait();
            });
        }
        else{ 
            Platform.runLater(() ->{ 
                
                tf = new TextField(); 
                tf.setPrefWidth(100);
                
                
                Button submit = new Button("Guess!");
                submit.setPrefWidth(100);
                

                HBox h = new HBox(10);


                HBox h1 = new HBox();

                HBox h2 = new HBox();
             
                h.getChildren().add(tf);
                h1.getChildren().add(submit);
                h2.getChildren().add(guessCount);
                h.setAlignment(Pos.BOTTOM_LEFT);
                h1.setAlignment(Pos.BOTTOM_LEFT);
                h2.setAlignment(Pos.TOP_RIGHT);
                h.setPadding(new Insets(5,5,5,5));
                h1.setPadding(new Insets(5,5,5,5));
                h2.setPadding(new Insets(5,5,5,5));


                submit.setAlignment(Pos.CENTER_RIGHT);
                submit.setOnAction(e -> updateGuesses());

                

                pane.getChildren().addAll(label, tf, submit, guessCount);

            });
            
        
        }


    }
    
    public void setUserName(String user){
        username.setText(user);
    }

    public void updateGuesses(){
        Platform.runLater(() ->{ 
                        
            if(!(getWord().equals(tf.getText())) && (guesses < 7)){
                guessCount.setText("Keep Trying! Guesses: " + (++guesses));
            }
            else if(guesses >= 7){
                out.println("WON");
                Closing closing = new Closing("YOU LOST :(");
                closing.start(ps);
                
            }
            else{
                out.println("LOST");
                Closing closing = new Closing("YOU WON!!!");
                closing.start(ps);
                
            }
                   

        });
    }
}