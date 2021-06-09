import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
    public static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
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
        System.out.println("Enter your username.");
        String userName = userInput.nextLine();
        out.println(userName);

        Thread client = new Thread(new ClientListener());
        client.start();

        Application.launch(args);

    }

    static class ServerListener implements Runnable {

        public void run() {

            try {
                String incoming = "";

                while ((incoming = socketIn.readLine()) != null) {

                    if(incoming.startsWith("COORDINATE1")){
                        gc.beginPath();
                        draw(incoming);
                    }
                    else if(incoming.startsWith("COORDINATE")){
                        draw(incoming);
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
        Whiteboard w = new Whiteboard(out, scene, pane);
        w.start(primaryStage);
    }

    static class ClientListener implements Runnable{
        public void run(){
            String line = userInput.nextLine().trim();
        
            while (!line.equals("/quit")) {
                
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
                    // start pictionary game with other clients
                    // we'll have to implement a check to make sure other clients are present
                }
                else if (line.toLowerCase().equals("/whiteboard")) {
                    // open the whiteboard for free drawing
                }
                
                out.println(line);
                line = userInput.nextLine().trim();

            }
            try{ 
            out.println("QUIT");
            out.close();
            userInput.close();
            socketIn.close();
            socket.close();
            }
            catch(Exception e){};
        }
    }

}