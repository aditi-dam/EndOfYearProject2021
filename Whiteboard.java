import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
//hi

public class Whiteboard extends Application{

    //we draw on the canvas using GraphicsContext
    Canvas canvas = new Canvas(800, 500); //like a paper
    GraphicsContext gc; //like a pencil

    StackPane pane = new StackPane();
    Scene scene = new Scene(pane, 800, 500);

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage){
        try{
            gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.BLACK); //default color
            gc.setLineWidth(5); //default width

            //drawing functionality
            scene.setOnMousePressed(e->{ 
                gc.beginPath();
                gc.lineTo(e.getSceneX(), e.getSceneY()); //get mouse positions and pass them to lineTo()s
                gc.stroke();
            });

            scene.setOnMouseDragged(e->{
                gc.lineTo(e.getSceneX(), e.getSceneY());                
                gc.stroke();
            });
            //until here
            
            stage.setScene(scene);
            stage.show();

            pane.getChildren().add(canvas);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}