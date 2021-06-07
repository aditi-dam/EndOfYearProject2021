import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Application {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;
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
                    System.out.println(incoming); //COORDINATES SHOULD PRINT
                }
            } catch (Exception ex) {
                System.out.println("Exception caught in listener - " + ex);
            } finally {
                System.out.println("Listener exiting");
            }
        }
    }

    Canvas canvas = new Canvas(800, 500); 
    GraphicsContext gc; 
    
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane, 800, 500);

    @Override
    public void start(Stage primaryStage) {
        try{
            gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.BLACK); 
            gc.setLineWidth(5); 

            scene.setOnMousePressed(e->{ 
                gc.beginPath();
                gc.lineTo(e.getSceneX(), e.getSceneY()); 
                out.println("COORDINATE: " + "x" + e.getSceneX() + "y" + e.getSceneY()); //#1: Pass the coordinates that the user is drawing on to server
                gc.stroke();
            });

            scene.setOnMouseDragged(e->{
                gc.lineTo(e.getSceneX(), e.getSceneY());                
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