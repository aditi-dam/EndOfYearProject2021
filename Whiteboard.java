import java.io.PrintWriter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
//hi

public class Whiteboard extends Application{

    public static Canvas canvas = new Canvas(800, 500); 
    public static GraphicsContext gc; 
    public static ColorPicker cp = new ColorPicker();
    public static Slider slider = new Slider();
    public static Label label = new Label("1.0");
    public static PrintWriter out;
    public static Scene scene;
    public static Pane pane;
    public static GridPane grid = new GridPane();

    public Whiteboard(PrintWriter o, Scene s, Pane p){
        out = o;
        scene = s;
        pane = p;
    }

    public void start(Stage primaryStage) {
        try{
            gc = canvas.getGraphicsContext2D();
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
                ///code for log
                System.out.println("x: " + e.getSceneX() + "y: " + e.getSceneY());     
                ///
                gc.stroke();
            });
            //until here
            
            primaryStage.setScene(scene);
            primaryStage.show();

            pane.getChildren().add(canvas);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}