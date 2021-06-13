import java.io.PrintWriter;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//hi

public class Whiteboard extends Application{

    private static Canvas canvas = new Canvas(800, 500); 
    private static GraphicsContext gc; 
    private static ColorPicker cp = new ColorPicker();
    private static Slider slider = new Slider();
    private static Label label = new Label("1.0");
    private static PrintWriter out;
    private static Scene scene;
    private static Pane pane;
    private static GridPane grid = new GridPane();
    private static String word = "";


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
            
            primaryStage.setScene(scene);
            primaryStage.show();

            pane.getChildren().add(canvas);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void draw(String incoming){
        double x = Double.valueOf(incoming.substring(incoming.indexOf("x") + 1, incoming.indexOf("y")));
        double y = Double.valueOf(incoming.substring(incoming.indexOf("y") + 1));
        ///code for log
        System.out.println("x" + x + "y" + y);
        ///
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
                Label label = new Label("Name:");
                TextField tf = new TextField(); 
                pane.getChildren().addAll(label, tf);
            });
            
        
        }
        /*
        Platform.runLater(() ->{ 
                Text word = new Text();
                word.setText("Keep Trying");
                word.setX(50); 
                word.setY(50);
                pane.getChildren().add(word); 
            });
            */

    }
}