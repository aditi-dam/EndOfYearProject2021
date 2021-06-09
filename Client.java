import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Application {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;

    public static Canvas canvas = new Canvas(800, 500); 
    public static GraphicsContext gc; 
    public static ColorPicker cp = new ColorPicker();
    public static Slider slider = new Slider();
    public static Label label = new Label("1.0");
    public static GridPane grid = new GridPane();
        
    public static StackPane pane = new StackPane();
    public static Scene scene = new Scene(pane, 800, 500);

    private static int playerNum = 0;

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Server IP?");
        String ip = userInput.nextLine();

        System.out.println("Port?");
        int port = userInput.nextInt();
        userInput.nextLine();

        socket = new Socket(ip, port);

        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        ServerListener listener = new ServerListener();
        Thread t = new Thread(listener);
        t.start();
        System.out.println(out); //DELETE
        System.out.println("Enter your username.");
        String userName = userInput.nextLine();
        out.println(userName);

        Application.launch(args);
        String line = userInput.nextLine().trim();

        if (line.toLowerCase().equals("/directions")) {
            // print directions
            System.out.println("Welcome to our Collaborative Whiteboard!");
            System.out.println("If you'd like to play a game of Pictionary, type in /pictionary.");
            System.out.println("If there are other clients on the whiteboard, then the game will start.");
            System.out.println("You can also type in /whiteboard just for free drawing.");
            System.out.println("To quit, type in /quit.");
            System.out.println("Have fun!");
        }
        else if (line.toLowerCase().equals("/pictionary")) {
            out.println("PLAYER 1");
            playerNum = 1;
            System.out.println(playerNum);
        }

        if(playerNum == 1){
            File words = new File("pictionary_ideas.txt");
            int randomWord = (int)(Math.random() * words.length());
            Scanner s = new Scanner(words);

            String word = "";
            for(int i = 0; i <= randomWord; i++){
                word = s.nextLine();
            }
            System.out.println(playerNum + ": " + word);
        }

        
        while (!line.equals("/quit")) {

            out.println(line);
            line = userInput.nextLine().trim();

        }
        

        out.println("QUIT");
        out.close();
        userInput.close();
        socketIn.close();
        socket.close();

    }

    static class ServerListener implements Runnable {

        public void run() {

            try {
                String incoming = "";
                System.out.println(incoming);

                while ((incoming = socketIn.readLine()) != null) {

                    if(incoming.startsWith("COORDINATE1")){
                        gc.beginPath();
                        draw(incoming);
                    }
                    else if(incoming.startsWith("COORDINATE")){
                        draw(incoming);
                    }
                    else if(incoming.startsWith("START")){
                        playerNum = 2;
                    }

                }
            } catch (Exception ex) {
                System.out.println("Exception caught in listener - " + ex);
            } finally {
                System.out.println("Listener exiting");
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
    }


    @Override
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